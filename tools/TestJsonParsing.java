import java.util.*;

public class TestJsonParsing {

    // 模拟WeatherDataResult类
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

        @Override
        public String toString() {
            return String.format("WeatherData{location=%s, timestamps=%s, metrics=%s, units=%s, forecast_time='%s'}",
                    location, timestamps, metricValues, metricUnits, timeFcst);
        }
    }

    /**
     * 模拟JSON解析方法
     */
    public static WeatherDataResult parseWeatherResponse(Map<String, Object> responseBody, double longitude, double latitude) {
        try {
            System.out.println("=== 开始解析JSON响应 ===");
            System.out.println("响应数据结构:");
            printJsonStructure(responseBody, 0);

            // 解析位置信息
            List<Double> location = Arrays.asList(longitude, latitude);

            // 安全解析各个字段
            List<String> timestamps = null;
            List<String> metricVars = null;
            List<String> metricUnits = null;
            String timeFcst = null;
            Map<String, List<Double>> metricValues = new HashMap<>();

            // 解析时间戳
            if (responseBody.containsKey("timestamp")) {
                Object timestampObj = responseBody.get("timestamp");
                if (timestampObj instanceof List) {
                    timestamps = (List<String>) timestampObj;
                    System.out.println("✅ 解析时间戳: " + timestamps);
                }
            }

            // 解析指标变量
            if (responseBody.containsKey("mete_var")) {
                Object meteVarObj = responseBody.get("mete_var");
                if (meteVarObj instanceof List) {
                    metricVars = (List<String>) meteVarObj;
                    System.out.println("✅ 解析指标变量: " + metricVars);
                }
            }

            // 解析指标单位
            if (responseBody.containsKey("mete_unit")) {
                Object meteUnitObj = responseBody.get("mete_unit");
                if (meteUnitObj instanceof List) {
                    metricUnits = (List<String>) meteUnitObj;
                    System.out.println("✅ 解析指标单位: " + metricUnits);
                }
            }

            // 解析起报时刻
            if (responseBody.containsKey("time_fcst")) {
                timeFcst = (String) responseBody.get("time_fcst");
                System.out.println("✅ 解析起报时刻: " + timeFcst);
            }

            // 解析数据部分
            if (responseBody.containsKey("data")) {
                Object dataObj = responseBody.get("data");
                if (dataObj instanceof List) {
                    List<?> dataList = (List<?>) dataObj;
                    System.out.println("✅ data字段是List类型，长度: " + dataList.size());

                    if (!dataList.isEmpty() && dataList.get(0) instanceof Map) {
                        Map<String, Object> firstDataPoint = (Map<String, Object>) dataList.get(0);
                        System.out.println("✅ 第一个数据点: " + firstDataPoint.keySet());

                        // 解析values字段
                        if (firstDataPoint.containsKey("values")) {
                            Object valuesObj = firstDataPoint.get("values");
                            if (valuesObj instanceof List) {
                                List<?> valuesList = (List<?>) valuesObj;
                                System.out.println("✅ values字段是List类型，长度: " + valuesList.size());

                                if (metricVars != null && !valuesList.isEmpty()) {
                                    // 转置数据：从按时间点组织转为按指标组织
                                    for (int varIndex = 0; varIndex < metricVars.size(); varIndex++) {
                                        List<Double> varValues = new ArrayList<>();
                                        String varName = metricVars.get(varIndex);

                                        for (Object timePointObj : valuesList) {
                                            if (timePointObj instanceof List) {
                                                List<?> timePointValues = (List<?>) timePointObj;
                                                if (varIndex < timePointValues.size()) {
                                                    Object value = timePointValues.get(varIndex);
                                                    if (value instanceof Number) {
                                                        varValues.add(((Number) value).doubleValue());
                                                    }
                                                }
                                            }
                                        }

                                        if (!varValues.isEmpty()) {
                                            metricValues.put(varName, varValues);
                                            System.out.println("✅ 解析指标 " + varName + ": " + varValues);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("=== 解析完成 ===");
            return new WeatherDataResult(location, timestamps, metricValues, metricVars, metricUnits, timeFcst);

        } catch (Exception e) {
            System.err.println("❌ 解析失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 递归打印JSON结构
     */
    public static void printJsonStructure(Object obj, int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        String indentStr = sb.toString();

        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                System.out.println(indentStr + entry.getKey() + " (" + entry.getValue().getClass().getSimpleName() + "):");
                if (entry.getValue() instanceof Map || entry.getValue() instanceof List) {
                    printJsonStructure(entry.getValue(), indent + 1);
                } else {
                    System.out.println(indentStr + "  " + entry.getValue());
                }
            }
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            System.out.println(indentStr + "Array[" + list.size() + "]");
            if (!list.isEmpty()) {
                System.out.println(indentStr + "  [0] (" + list.get(0).getClass().getSimpleName() + "):");
                printJsonStructure(list.get(0), indent + 2);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("🧪 JSON解析测试");
        System.out.println("===================================");

        // 创建模拟的API响应数据 (基于readme中的格式)
        Map<String, Object> mockResponse = new HashMap<>();

        // 创建data数组
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> dataPoint = new HashMap<>();
        dataPoint.put("location", Arrays.asList(120.25, 23.5));

        // 创建values二维数组 [[303.533, 298.1826], [304.4319, 298.5295], [305.4548, 297.7632]]
        List<List<Double>> values = Arrays.asList(
            Arrays.asList(303.533, 298.1826),
            Arrays.asList(304.4319, 298.5295),
            Arrays.asList(305.4548, 297.7632)
        );
        dataPoint.put("values", values);
        dataList.add(dataPoint);

        mockResponse.put("data", dataList);
        mockResponse.put("mete_var", Arrays.asList("t2m", "d2m"));
        mockResponse.put("mete_unit", Arrays.asList("K", "K"));
        mockResponse.put("time_fcst", "2024-01-15 00:00:00");
        mockResponse.put("timestamp", Arrays.asList(
            "2024-01-15 01:00:00",
            "2024-01-15 02:00:00",
            "2024-01-15 03:00:00"
        ));

        // 测试解析
        WeatherDataResult result = parseWeatherResponse(mockResponse, 120.25, 23.5);

        if (result != null) {
            System.out.println("\\n🎉 解析成功!");
            System.out.println(result);
        } else {
            System.out.println("\\n❌ 解析失败!");
        }
    }
}