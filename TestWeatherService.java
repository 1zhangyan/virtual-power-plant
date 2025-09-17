import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestWeatherService {

    // 模拟解析后的数据结构
    static class WeatherDataResult {
        private List<Double> location;
        private List<String> timestamps;
        private Map<String, List<Double>> metricValues;
        private List<String> metricVars;
        private List<String> metricUnits;
        private String timeFcst;

        public WeatherDataResult(List<Double> location, List<String> timestamps,
                               Map<String, List<Double>> metricValues, List<String> metricVars,
                               List<String> metricUnits, String timeFcst) {
            this.location = location;
            this.timestamps = timestamps;
            this.metricValues = metricValues;
            this.metricVars = metricVars;
            this.metricUnits = metricUnits;
            this.timeFcst = timeFcst;
        }

        // Getters
        public List<Double> getLocation() { return location; }
        public List<String> getTimestamps() { return timestamps; }
        public Map<String, List<Double>> getMetricValues() { return metricValues; }
        public List<String> getMetricVars() { return metricVars; }
        public List<String> getMetricUnits() { return metricUnits; }
        public String getTimeFcst() { return timeFcst; }
    }

    /**
     * 获取气象数据的通用函数
     * @param longitude 经度
     * @param latitude 纬度
     * @param dataType 数据类型 (如: gfs_surface)
     * @param metaVars 指标列表 (如: ["t2m", "d2m"])
     * @return WeatherDataResult 包含时间戳和指标值的结果
     */
    public static WeatherDataResult fetchWeatherData(double longitude, double latitude, String dataType, List<String> metaVars) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.out.println("=== 气象数据获取函数调用 [" + timestamp + "] ===");
            System.out.println("参数:");
            System.out.println("  经度: " + longitude);
            System.out.println("  纬度: " + latitude);
            System.out.println("  数据类型: " + dataType);
            System.out.println("  指标列表: " + metaVars);

            String apiUrl = "https://api-pro-openet.terraqt.com/v1/" + dataType + "/multi/point";
            System.out.println("  API URL: " + apiUrl);

            // 模拟HTTP请求延迟
            Thread.sleep(300);

            // 模拟API响应数据解析 (基于readme中的返回值格式)
            List<Double> location = Arrays.asList(longitude, latitude);
            List<String> timestamps = Arrays.asList(
                "2024-01-15 01:00:00",
                "2024-01-15 02:00:00",
                "2024-01-15 03:00:00"
            );

            Map<String, List<Double>> metricValues = new HashMap<>();
            List<String> metricUnits = new ArrayList<>();

            // 根据请求的指标生成模拟数据
            for (String var : metaVars) {
                if ("t2m".equals(var)) {
                    metricValues.put("t2m", Arrays.asList(303.533, 304.4319, 305.4548));
                    metricUnits.add("K");
                } else if ("d2m".equals(var)) {
                    metricValues.put("d2m", Arrays.asList(298.1826, 298.5295, 297.7632));
                    metricUnits.add("K");
                } else {
                    // 其他指标的模拟数据
                    metricValues.put(var, Arrays.asList(
                        Math.random() * 100,
                        Math.random() * 100,
                        Math.random() * 100
                    ));
                    metricUnits.add("unit");
                }
            }

            String timeFcst = "2024-01-15 00:00:00";

            WeatherDataResult result = new WeatherDataResult(location, timestamps, metricValues, metaVars, metricUnits, timeFcst);

            System.out.println("响应解析结果:");
            System.out.println("  位置: " + result.getLocation());
            System.out.println("  时间戳: " + result.getTimestamps());
            System.out.println("  指标值: " + result.getMetricValues());
            System.out.println("  指标单位: " + result.getMetricUnits());
            System.out.println("  起报时刻: " + result.getTimeFcst());
            System.out.println("=== 函数调用完成 ===\n");

            return result;

        } catch (Exception e) {
            System.err.println("获取气象数据失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 定时任务方法
     */
    public static void scheduledWeatherDataFetch() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("=== 定时气象数据获取 [" + timestamp + "] ===");

        try {
            // 第一个点位
            WeatherDataResult result1 = fetchWeatherData(103.1693835, 30.5398753, "gfs_surface", Arrays.asList("t2m", "d2m"));
            if (result1 != null) {
                System.out.println("✅ 点位1 [103.17, 30.54] 数据获取成功");
            } else {
                System.out.println("❌ 点位1数据获取失败");
            }

            // 第二个点位
            WeatherDataResult result2 = fetchWeatherData(104.0693835, 30.5398753, "gfs_surface", Arrays.asList("t2m"));
            if (result2 != null) {
                System.out.println("✅ 点位2 [104.07, 30.54] 数据获取成功");
            } else {
                System.out.println("❌ 点位2数据获取失败");
            }

        } catch (Exception e) {
            System.err.println("定时任务执行失败: " + e.getMessage());
        }

        System.out.println("=== 定时任务完成 ===\n");
    }

    public static void main(String[] args) {
        System.out.println("🌤️  重构后的气象数据服务测试");
        System.out.println("📋 功能展示:");
        System.out.println("   1. HTTP访问包装成独立函数");
        System.out.println("   2. 支持自定义经纬度、数据类型、指标列表");
        System.out.println("   3. 解析时间戳和指标值");
        System.out.println("   4. 定时任务调用封装函数");
        System.out.println("───────────────────────────────────────────────\n");

        // 演示单次调用
        System.out.println("🔍 演示单次函数调用:");
        WeatherDataResult singleResult = fetchWeatherData(120.25, 23.5, "gfs_surface", Arrays.asList("t2m", "d2m"));

        // 演示定时任务
        System.out.println("⏰ 演示定时任务执行:");
        scheduledWeatherDataFetch();

        System.out.println("✨ 测试完成! 实际应用中:");
        System.out.println("   - 函数每10秒自动执行");
        System.out.println("   - 通过REST API手动触发");
        System.out.println("   - 支持多种数据类型和指标组合");
    }
}