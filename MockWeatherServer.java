import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;

public class MockWeatherServer {

    public static void main(String[] args) throws IOException {
        System.out.println("🌤️  启动模拟气象API服务器");
        System.out.println("📡 端口: 8081");
        System.out.println("🔗 接口: http://localhost:8081/v1/gfs_surface/multi/point");
        System.out.println("───────────────────────────────────────────────");

        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        // 模拟气象API接口
        server.createContext("/v1/gfs_surface/multi/point", new WeatherHandler());

        // 健康检查接口
        server.createContext("/health", new HealthHandler());

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("✅ 模拟服务器启动成功!");
        System.out.println("💡 现在可以测试WeatherDataService的HTTP请求");
        System.out.println("🛑 按 Ctrl+C 停止服务器");
    }

    static class WeatherHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // 读取请求体
                StringBuilder requestBody = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        requestBody.append(line);
                    }
                }

                System.out.println("📥 收到API请求: " + requestBody.toString());

                // 返回模拟的气象数据 (按照readme格式)
                String mockResponse = "{"
                    + "\"data\": [{"
                    + "\"location\": [120.25, 23.5],"
                    + "\"values\": ["
                    + "[303.533, 298.1826],"
                    + "[304.4319, 298.5295],"
                    + "[305.4548, 297.7632]"
                    + "]"
                    + "}],"
                    + "\"mete_var\": [\"t2m\", \"d2m\"],"
                    + "\"mete_unit\": [\"K\", \"K\"],"
                    + "\"time_fcst\": \"2024-01-15 00:00:00\","
                    + "\"timestamp\": ["
                    + "\"2024-01-15 01:00:00\","
                    + "\"2024-01-15 02:00:00\","
                    + "\"2024-01-15 03:00:00\""
                    + "]"
                    + "}";

                // 设置响应头
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, mockResponse.length());

                // 发送响应
                OutputStream os = exchange.getResponseBody();
                os.write(mockResponse.getBytes());
                os.close();

                System.out.println("📤 返回模拟数据: " + mockResponse);
            } else {
                // 处理非POST请求
                String response = "{\"error\": \"Only POST method allowed\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class HealthHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\": \"OK\", \"service\": \"Mock Weather API\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}