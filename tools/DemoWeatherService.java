import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DemoWeatherService {
    private static final String TOKEN_FILE = System.getProperty("user.home") + "/token.txt";
    private static final String API_URL = "https://api-pro-openet.terraqt.com/v1/gfs_surface/multi/point";

    private static String readToken() {
        try {
            File tokenFile = new File(TOKEN_FILE);
            if (!tokenFile.exists()) {
                return "iBDMwMGN4gjZlBDMwYWZwMGZlJDNwQzN"; // 默认token
            }

            Scanner scanner = new Scanner(tokenFile);
            StringBuilder content = new StringBuilder();
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine());
            }
            scanner.close();

            // 简单解析JSON获取token
            String jsonContent = content.toString();
            if (jsonContent.contains("weather_forecast")) {
                int start = jsonContent.indexOf("weather_forecast") + "weather_forecast".length();
                start = jsonContent.indexOf("\"", start) + 1;
                int end = jsonContent.indexOf("\"", start);
                return jsonContent.substring(start, end);
            }

            return "iBDMwMGN4gjZlBDMwYWZwMGZlJDNwQzN"; // 默认token
        } catch (Exception e) {
            System.err.println("Failed to read token: " + e.getMessage());
            return "iBDMwMGN4gjZlBDMwYWZwMGZlJDNwQzN"; // 默认token
        }
    }

    private static void fetchWeatherData() {
        try {
            String token = readToken();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            System.out.println("=== Weather Data Fetch [" + timestamp + "] ===");
            System.out.println("Token: " + token.substring(0, Math.min(10, token.length())) + "...");
            System.out.println("API URL: " + API_URL);
            System.out.println("Request Points: [103.1693835, 30.5398753], [104.0693835, 30.5398753]");
            System.out.println("Meteorological Variables: [t2m]");
            System.out.println("Query Time: 2024-01-15 00:00:00 (UTC)");

            // 模拟HTTP请求
            System.out.println("Status: Sending HTTP POST request...");
            Thread.sleep(500); // 模拟网络延迟

            // 模拟响应
            System.out.println("Status: SUCCESS");
            System.out.println("Response Data: {");
            System.out.println("  \"point_1\": { \"t2m\": 15.2, \"coordinates\": [103.1693835, 30.5398753] },");
            System.out.println("  \"point_2\": { \"t2m\": 16.1, \"coordinates\": [104.0693835, 30.5398753] }");
            System.out.println("}");
            System.out.println("=== Fetch Complete ===\\n");

        } catch (Exception e) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.err.println("=== Weather Data Fetch Error [" + timestamp + "] ===");
            System.err.println("Error: " + e.getMessage());
            System.err.println("=== Error End ===\\n");
        }
    }

    public static void main(String[] args) {
        System.out.println("🌤️  Virtual Power Plant Weather Service Demo");
        System.out.println("📡 Starting periodic weather data fetching...");
        System.out.println("⏰ Interval: Every 10 seconds");
        System.out.println("🗂️  Token file: " + TOKEN_FILE);
        System.out.println("───────────────────────────────────────────────\\n");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 立即执行一次
        fetchWeatherData();

        // 每10秒执行一次
        scheduler.scheduleAtFixedRate(() -> {
            fetchWeatherData();
        }, 10, 10, TimeUnit.SECONDS);

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\\n🛑 Shutting down weather service...");
            scheduler.shutdown();
        }));

        // 保持程序运行
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            System.out.println("Service interrupted");
        }
    }
}