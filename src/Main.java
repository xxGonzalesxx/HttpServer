import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8080;

        System.out.println("🔧 Starting Portfolio Server...");
        System.out.println("📡 Port: " + port);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

            // Добавь этот контекст ПЕРЕД статическим хендлером
            server.createContext("/debug", exchange -> {
                String debugInfo = """
                        Current dir: %s
                        Files in current dir: %s
                        """.formatted(
                        System.getProperty("user.dir"),
                        String.join(", ", new File(".").list())
                );

                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(200, debugInfo.length());
                exchange.getResponseBody().write(debugInfo.getBytes());
                exchange.close();
            });

            // Раздача статических файлов
            server.createContext("/", new StaticFileHandler());

            server.setExecutor(null);
            server.start();

            System.out.println("✅ PORTFOLIO SERVER STARTED!");
            System.out.println("🌐 Available at: http://0.0.0.0:" + port);

            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println("❌ SERVER ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            try {
                // ПРАВИЛЬНЫЙ ПУТЬ для текущего Dockerfile
                String fullPath = "src/static" + path;
                System.out.println("🔍 Searching: " + fullPath);

                byte[] fileBytes = Files.readAllBytes(Paths.get(fullPath));
                System.out.println("✅ File found! Size: " + fileBytes.length + " bytes");

                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, fileBytes.length);
                exchange.getResponseBody().write(fileBytes);

            } catch (IOException e) {
                System.out.println("❌ File NOT found: " + e.getMessage());
                String response = "404 - File: " + path;
                exchange.sendResponseHeaders(404, response.length());
                exchange.getResponseBody().write(response.getBytes());
            } finally {
                exchange.close();
            }
        }
    }
}