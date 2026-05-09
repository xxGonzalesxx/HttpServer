import auth.database.DatabaseConnection;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import auth.AuthHandler;
import auth.SessionManager;
import auth.database.DatabaseConnection;

public class Main {
    private static SessionManager sessionManager;

    public static void main(String[] args) throws IOException {
        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8080;

        // Инициализация БД
        DatabaseConnection.getConnection();
        sessionManager = new SessionManager();

        System.out.println("🔧 Starting Portfolio Server with Auth...");
        System.out.println("📡 Port: " + port);
        System.out.println("📁 Serving from: src/Static/");

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

            // API для авторизации
            AuthHandler authHandler = new AuthHandler(sessionManager);
            server.createContext("/api/auth", authHandler);

            // Выход
            server.createContext("/api/logout", exchange -> {
                String sessionId = getSessionIdFromCookie(exchange);
                if (sessionId != null) {
                    sessionManager.invalidateSession(sessionId);
                }
                exchange.getResponseHeaders().set("Location", "/login.html");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
            });

            // Защищенные страницы
            server.createContext("/dashboard.html", exchange -> {
                if (isAuthenticated(exchange)) {
                    serveStaticFile(exchange, "/dashboard.html");
                } else {
                    redirectToLogin(exchange);
                }
            });

            // Публичные страницы и статика
            server.createContext("/", exchange -> {
                String path = exchange.getRequestURI().getPath();

                // Публичные пути
                if (isPublicPath(path)) {
                    serveStaticFile(exchange, path);
                } else if (isAuthenticated(exchange)) {
                    serveStaticFile(exchange, path);
                } else {
                    redirectToLogin(exchange);
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("✅ AUTH SERVER STARTED!");
            System.out.println("🌐 http://localhost:" + port);
            System.out.println("🔐 Login: http://localhost:" + port + "/login.html");
            Thread.currentThread().join();

        } catch (Exception e) {
            System.out.println("❌ SERVER ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isAuthenticated(HttpExchange exchange) {
        String sessionId = getSessionIdFromCookie(exchange);
        return sessionId != null && sessionManager.isValidSession(sessionId);
    }

    private static String getSessionIdFromCookie(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                String[] parts = cookie.trim().split("=");
                if (parts.length == 2 && "SESSION_ID".equals(parts[0])) {
                    return parts[1];
                }
            }
        }
        return null;
    }

    private static boolean isPublicPath(String path) {
        return path.equals("/") || path.equals("/index.html") ||
                path.equals("/projects.html") || path.equals("/info.html") ||
                path.equals("/contacts.html") || path.equals("/login.html") ||
                path.equals("/register.html") ||
                path.startsWith("/css/") || path.startsWith("/js/") ||
                path.equals("/favicon.ico");
    }

    private static void serveStaticFile(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/")) path = "/index.html";

        File file = new File("src/Static" + path);

        if (file.exists() && !file.isDirectory()) {
            String contentType = getContentType(path);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = exchange.getResponseBody()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } else {
            String notFound = "<h1>404 - Page Not Found</h1>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(404, notFound.getBytes().length);
            exchange.getResponseBody().write(notFound.getBytes());
            exchange.close();
        }
    }

    private static void redirectToLogin(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Location", "/login.html");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        return "text/plain";
    }
}