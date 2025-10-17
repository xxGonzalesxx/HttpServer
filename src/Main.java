import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Берём порт из переменной окружения Render
        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8080;

        System.out.println("🔧 Starting server...");
        System.out.println("📡 Port: " + port);

        try {
            // Создаем сервер на всех интерфейсах
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

            // Простейший endpoint для теста
            server.createContext("/", exchange -> {
                System.out.println("📨 Received request from: " + exchange.getRemoteAddress());

                String response = """
    <html>
      <body bgcolor="gold">
        <center><h1>Нежелательные люди!</h1></center>
        <br>
        <center><img src="https://i.ibb.co/bg2MvFSv/image.jpg" width="500"></center>
        <p>Порт: %s | Время: %s</p>
      </body>
    </html>
    """;

                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();

                System.out.println("✅ Sent response");
            });

            server.setExecutor(null);
            server.start();

            System.out.println("✅ SERVER STARTED SUCCESSFULLY!");
            System.out.println("🌐 Available at: http://0.0.0.0:" + port);

            // Держим поток alive
            Thread.currentThread().join();

        } catch (Exception e) {
            System.out.println("❌ SERVER ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}