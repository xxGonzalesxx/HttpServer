import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8080;

        System.out.println("🔧 Starting Portfolio Server...");
        System.out.println("📡 Port: " + port);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

            server.createContext("/", exchange -> {
                System.out.println("📨 Received request for portfolio");

                String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>SGProducts</title>
                        <style>
                            body {
                                margin: 0;
                                background: linear-gradient(135deg, #0a0a0a 0%, #1a0000 100%);
                                min-height: 100vh;
                                font-family: 'Arial', sans-serif;
                            }
                            .logo {
                                position: absolute;
                                top: 20px;
                                left: 20px;
                            }
                            .logo-img {
                                width: 100px;
                                height: auto;
                            }
                            .container {
                                text-align: center;
                                padding-top: 250px;
                            }
                            .main-title {
                                color: #00ff00;
                                text-shadow: 0 0 10px #00ff00, 0 0 20px #00ff00, 0 0 40px #00ff00, 0 0 80px #00ff00;
                                font-size: 3em;
                                margin: 0;
                            }
                            .slogan-text {
                                color: #ffffff;
                                text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
                                font-weight: 300;
                                margin-top: 20px;
                                font-size: 1.2em;
                            }
                            .nav-top {
                                position: absolute;
                                top: 60px;
                                left: 50%;
                                transform: translateX(-50%);
                                display: flex;
                                gap: 60px;
                            }
                            .nav-link {
                                color: #f5f103;
                                text-shadow: 0 0 10px rgba(245, 241, 3, 0.5);
                                margin: 0;
                                text-decoration: none;
                            }
                            .footer {
                                position: absolute;
                                bottom: 20px;
                                width: 100%;
                                text-align: center;
                                color: #666;
                                font-size: 0.9em;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="logo">
                            <img class="logo-img" src="https://i.ibb.co/nq9050H1/logo.png" alt="Logo">
                        </div>
                        <div class="nav-top">
                            <a class="nav-link">Projects</a>
                            <a class="nav-link">Info</a>
                            <a class="nav-link">Contacts</a>
                        </div>
                        <div class="container">
                            <h1 class="main-title">Super Chack</h1>
                            <h2 class="slogan-text">"From Wild Ideas to Working Code"</h2>
                        </div>
                        <div class="footer">
                            © 2025 SGProducts. Turning ideas into reality.
                        </div>
                    </body>
                    </html>
                """;

                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, html.getBytes().length);
                exchange.getResponseBody().write(html.getBytes());
                exchange.close();
                System.out.println("✅ Portfolio HTML sent");
            });

            server.setExecutor(null);
            server.start();
            System.out.println("✅ PORTFOLIO SERVER STARTED!");
            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println("❌ SERVER ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}