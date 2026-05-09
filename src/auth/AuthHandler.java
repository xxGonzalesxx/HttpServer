package auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class AuthHandler implements HttpHandler {
    private final SessionManager sessionManager;  // ← добавил final

    public AuthHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // CORS headers
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if (method.equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (path.equals("/api/auth/login") && method.equalsIgnoreCase("POST")) {
            handleLogin(exchange);
        } else if (path.equals("/api/auth/register") && method.equalsIgnoreCase("POST")) {
            handleRegister(exchange);
        } else if (path.equals("/api/auth/check") && method.equalsIgnoreCase("GET")) {
            handleCheckAuth(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);

        String username = extractJsonValue(body, "username");
        String password = extractJsonValue(body, "password");

        if (username == null || password == null) {
            sendResponse(exchange, 400, "{\"error\":\"Username and password required\"}");
            return;
        }

        User user = User.authenticate(username, password);

        if (user != null) {
            String sessionId = sessionManager.createSession(user);
            String response = "{\"success\":true,\"username\":\"" + user.getUsername() + "\",\"message\":\"Login successful\"}";
            sendResponseWithCookie(exchange, 200, response, sessionId);
        } else {
            sendResponse(exchange, 401, "{\"success\":false,\"error\":\"Invalid credentials\"}");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);

        String username = extractJsonValue(body, "username");
        String email = extractJsonValue(body, "email");
        String password = extractJsonValue(body, "password");

        if (username == null || email == null || password == null) {
            sendResponse(exchange, 400, "{\"error\":\"All fields required\"}");
            return;
        }

        if (password.length() < 6) {
            sendResponse(exchange, 400, "{\"error\":\"Password must be at least 6 characters\"}");
            return;
        }

        if (User.userExists(username, email)) {
            sendResponse(exchange, 409, "{\"error\":\"Username or email already exists\"}");
            return;
        }

        boolean registered = User.register(username, email, password);

        if (registered) {
            sendResponse(exchange, 200, "{\"success\":true,\"message\":\"Registration successful! Please login.\"}");
        } else {
            sendResponse(exchange, 500, "{\"error\":\"Registration failed\"}");
        }
    }

    private void handleCheckAuth(HttpExchange exchange) throws IOException {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null) {
            String sessionId = extractSessionId(cookieHeader);
            if (sessionId != null && sessionManager.isValidSession(sessionId)) {
                User user = sessionManager.getUserFromSession(sessionId);
                if (user != null) {
                    sendResponse(exchange, 200, "{\"authenticated\":true,\"username\":\"" + user.getUsername() + "\"}");
                    return;
                }
            }
        }
        sendResponse(exchange, 401, "{\"authenticated\":false}");  // ← изменил 200 на 401
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }

    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\"";
        int index = json.indexOf(search);
        if (index == -1) return null;

        int colonIndex = json.indexOf(":", index);
        if (colonIndex == -1) return null;

        int startQuote = json.indexOf("\"", colonIndex);
        if (startQuote == -1) return null;

        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;

        return json.substring(startQuote + 1, endQuote);
    }

    private String extractSessionId(String cookieHeader) {
        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] parts = cookie.trim().split("=");
            if (parts.length == 2 && "SESSION_ID".equals(parts[0])) {
                return parts[1];
            }
        }
        return null;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private void sendResponseWithCookie(HttpExchange exchange, int statusCode, String response, String sessionId) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Set-Cookie", "SESSION_ID=" + sessionId + "; Path=/; HttpOnly");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}