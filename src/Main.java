import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8080;

        System.out.println("🔧 Starting Portfolio Server...");
        System.out.println("📡 Port: " + port);
        System.out.println("📁 Serving from: src/Static/");

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

            // Обработчик для статических файлов
            server.createContext("/", exchange -> {
                String path = exchange.getRequestURI().getPath();

                // Если корневой путь - отдаем index.html
                if (path.equals("/")) {
                    path = "/index.html";
                }

                System.out.println("📨 Request: " + path);

                try {
                    // Читаем файл из папки Static
                    File file = new File("src/Static" + path);

                    if (file.exists() && !file.isDirectory()) {
                        // Определяем Content-Type
                        String contentType = getContentType(path);

                        exchange.getResponseHeaders().set("Content-Type", contentType);
                        exchange.sendResponseHeaders(200, file.length());

                        // Отправляем файл
                        FileInputStream fis = new FileInputStream(file);
                        OutputStream os = exchange.getResponseBody();
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = fis.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }

                        fis.close();
                        os.close();
                        System.out.println("✅ Sent: " + path);
                    } else {
                        // Файл не найден - 404
                        String notFound = "<h1>404 - Page Not Found</h1>";
                        exchange.getResponseHeaders().set("Content-Type", "text/html");
                        exchange.sendResponseHeaders(404, notFound.getBytes().length);
                        exchange.getResponseBody().write(notFound.getBytes());
                        exchange.close();
                        System.out.println("❌ Not found: " + path);
                    }
                } catch (Exception e) {
                    System.out.println("❌ Error serving: " + path + " - " + e.getMessage());
                    exchange.sendResponseHeaders(500, 0);
                    exchange.close();
                }
            });

            server.setExecutor(null);
            server.start();
            System.out.println("✅ PORTFOLIO SERVER STARTED!");
            System.out.println("🌐 Open: http://localhost:" + port);
            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println("❌ SERVER ERROR: " + e.getMessage());
            e.printStackTrace();
        }
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