package com.virtualpowerplant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualpowerplant.config.TokenConfig;
import com.virtualpowerplant.constant.Constant;
import com.virtualpowerplant.model.WeatherDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherDataService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherDataService.class);
    private static final String BASE_API_URL = "https://api-pro-openet.terraqt.com/v1";

    @Autowired
    private TokenConfig tokenConfig;

    /**
     * 获取气象数据的通用函数
     * @param longitude 经度
     * @param latitude 纬度
     * @param dataType 数据类型 (如: gfs_surface)
     * @param metaVars 指标列表 (如: ["t2m", "d2m"])
     * @return WeatherDataResult 包含时间戳和指标值的结果
     */
    public WeatherDataResult fetchWeatherData(double longitude, double latitude, String dataType, List<String> metaVars, String timestamp) {
        try {
            String token = tokenConfig.getWeatherToken();
            String apiUrl = BASE_API_URL + "/" + dataType + "/multi/point";

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("token", token);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("time", timestamp);
            requestBody.put("points", Arrays.asList(Arrays.asList(longitude, latitude)));
            requestBody.put("mete_vars", metaVars);
            requestBody.put("avg", false);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<Map> response = Constant.restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseWeatherResponse(response.getBody(), longitude, latitude);
            } else {
                logger.warn("API请求失败 - HTTP {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            logger.error("获取气象数据失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析API响应数据
     */
    private WeatherDataResult parseWeatherResponse(Map<String, Object> responseBody, double longitude, double latitude) {
        try {
            // 首先打印原始响应以便调试
            logger.info("原始API响应: {}", Constant.objectMapper.writeValueAsString(responseBody));

            // 解析位置信息
            List<Double> location = Arrays.asList(longitude, latitude);

            // 安全解析各个字段，处理可能的null值和类型转换
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
                }
            }

            // 解析指标变量
            if (responseBody.containsKey("mete_var")) {
                Object meteVarObj = responseBody.get("mete_var");
                if (meteVarObj instanceof List) {
                    metricVars = (List<String>) meteVarObj;
                }
            }

            // 解析指标单位
            if (responseBody.containsKey("mete_unit")) {
                Object meteUnitObj = responseBody.get("mete_unit");
                if (meteUnitObj instanceof List) {
                    metricUnits = (List<String>) meteUnitObj;
                }
            }

            // 解析起报时刻
            if (responseBody.containsKey("time_fcst")) {
                timeFcst = (String) responseBody.get("time_fcst");
            }

            // 解析数据部分
            if (responseBody.containsKey("data")) {
                Object dataObj = responseBody.get("data");
                if (dataObj instanceof List) {
                    List<?> dataList = (List<?>) dataObj;
                    if (!dataList.isEmpty() && dataList.get(0) instanceof Map) {
                        Map<String, Object> firstDataPoint = (Map<String, Object>) dataList.get(0);

                        // 解析values字段
                        if (firstDataPoint.containsKey("values")) {
                            Object valuesObj = firstDataPoint.get("values");
                            if (valuesObj instanceof List) {
                                List<?> valuesList = (List<?>) valuesObj;

                                // 根据readme格式，values是一个二维数组 [[303.533, 298.1826], [304.4319, 298.5295], ...]
                                // 每一列对应一个指标变量
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
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            logger.info("解析结果 - 位置: {}, 时间戳数量: {}, 指标数量: {}, 指标值: {}",
                    location,
                    timestamps != null ? timestamps.size() : 0,
                    metricVars != null ? metricVars.size() : 0,
                    metricValues.size());

            return new WeatherDataResult(location, timestamps, metricValues, metricVars, metricUnits, timeFcst);

        } catch (Exception e) {
            logger.error("解析响应数据失败: {}", e.getMessage(), e);
            try {
                logger.error("响应体内容: {}", Constant.objectMapper.writeValueAsString(responseBody));
            } catch (Exception ex) {
                logger.error("无法序列化响应体: {}", ex.getMessage());
            }
            return null;
        }
    }

//    @Scheduled(fixedDelay = 10000) // 每10秒执行一次
    public void scheduledWeatherDataFetch() {
        String timestamp = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("=== 定时气象数据获取 [{}] ===", timestamp);

        try {
            // 第一个点位
            WeatherDataResult result1 = fetchWeatherData(103.1693835, 30.5398753, "gfs_surface", Arrays.asList("t2m", "d2m"), timestamp);
            if (result1 != null) {
                logger.info("点位1 [103.17, 30.54] 数据获取成功:");
                logger.info("  位置: {}", result1.getLocation());
                logger.info("  时间戳: {}", result1.getTimestamps());
                logger.info("  指标值: {}", result1.getMetricValues());
                logger.info("  指标单位: {}", result1.getMetricUnits());
                logger.info("  起报时刻: {}", result1.getTimeFcst());
            } else {
                logger.warn("点位1数据获取失败");
            }

            // 第二个点位
            WeatherDataResult result2 = fetchWeatherData(104.0693835, 30.5398753, "gfs_surface", Arrays.asList("t2m"), timestamp);
            if (result2 != null) {
                logger.info("点位2 [104.07, 30.54] 数据获取成功:");
                logger.info("  位置: {}", result2.getLocation());
                logger.info("  时间戳: {}", result2.getTimestamps());
                logger.info("  指标值: {}", result2.getMetricValues());
            } else {
                logger.warn("点位2数据获取失败");
            }

        } catch (Exception e) {
            logger.error("定时任务执行失败: {}", e.getMessage(), e);
        }

        logger.info("=== 定时任务完成 ===");
    }
}