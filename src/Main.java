import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "10000"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Главная страница
        server.createContext("/", exchange -> {
            String html = """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Мой сервер</title>
                    <style>
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }
                        body {
                            font-family: 'Arial', sans-serif;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                        }
                        .container {
                            background: white;
                            padding: 40px;
                            border-radius: 20px;
                            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                            text-align: center;
                            max-width: 500px;
                            width: 90%;
                        }
                        h1 {
                            color: #333;
                            margin-bottom: 20px;
                            font-size: 2.5em;
                        }
                        p {
                            color: #666;
                            margin-bottom: 15px;
                            line-height: 1.6;
                        }
                        .endpoints {
                            background: #f8f9fa;
                            padding: 20px;
                            border-radius: 10px;
                            margin: 20px 0;
                        }
                        .endpoint {
                            font-family: 'Courier New', monospace;
                            background: #e9ecef;
                            padding: 8px 12px;
                            border-radius: 5px;
                            margin: 5px 0;
                            display: inline-block;
                        }
                        .status {
                            color: #28a745;
                            font-weight: bold;
                            margin-top: 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🚀 Мой сервер работает!</h1>
                        <p>Это базовая заготовка. Ты можешь изменить этот HTML как хочешь!</p>
                        
                        <div class="endpoints">
                            <p><strong>Доступные endpoints:</strong></p>
                            <div class="endpoint">GET /</div>
                            <div class="endpoint">GET /hello</div>
                            <div class="endpoint">GET /api/test</div>
                        </div>
                        
                        <p class="status">✅ Сервер запущен на порту: %s</p>
                        <p>🕒 Время запуска: %s</p>
                    </div>
                </body>
                </html>
                """.formatted(port, java.time.LocalDateTime.now());

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            exchange.getResponseBody().write(html.getBytes());
            exchange.close();
        });

        // Простой endpoint
        server.createContext("/hello", exchange -> {
            String response = """
                {
                    "message": "Привет! Сервер работает!",
                    "status": "success",
                    "timestamp": "%s"
                }
                """.formatted(java.time.LocalDateTime.now());

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        // API endpoint
        server.createContext("/api/test", exchange -> {
            String response = """
                {
                    "endpoint": "/api/test",
                    "description": "Это API endpoint",
                    "data": {
                        "version": "1.0",
                        "author": "Твое имя"
                    }
                }
                """;

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        server.start();
        System.out.println("✅ Сервер запущен на порту: " + port);
        System.out.println("🌐 Доступные endpoints:");
        System.out.println("   - GET /");
        System.out.println("   - GET /hello");
        System.out.println("   - GET /api/test");
    }
}