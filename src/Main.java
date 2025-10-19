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
                // Читаем файл из папки static
                byte[] fileBytes = Files.readAllBytes(Paths.get("src/static" + path));

                // Определяем Content-Type
                String contentType = "text/html";
                if (path.endsWith(".css")) contentType = "text/css";
                if (path.endsWith(".js")) contentType = "application/javascript";
                if (path.endsWith(".png")) contentType = "image/png";
                if (path.endsWith(".jpg")) contentType = "image/jpeg";

                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, fileBytes.length);
                exchange.getResponseBody().write(fileBytes);

            } catch (IOException e) {
                // Если файл не найден - 404
                String response = "404 - File Not Found";
                exchange.sendResponseHeaders(404, response.length());
                exchange.getResponseBody().write(response.getBytes());
            } finally {
                exchange.close();
            }
        }
    }
}