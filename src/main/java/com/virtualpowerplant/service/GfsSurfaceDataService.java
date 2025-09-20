

package com.virtualpowerplant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.virtualpowerplant.config.SecretConfigManager;
import com.virtualpowerplant.config.TokenConfig;
import com.virtualpowerplant.constant.Constant;
import com.virtualpowerplant.model.GfsSurfaceDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.virtualpowerplant.constant.Constant.objectMapper;

@Service
public class GfsSurfaceDataService {

    private static final Logger logger = LoggerFactory.getLogger(GfsSurfaceDataService.class);
    private static final String BASE_API_URL = "https://api-pro-openet.terraqt.com/v1";
    private static final String dataType = "gfs_surface";

    private static final List<String> metaVars = Arrays.asList("tcc", "lcc", "mcc", "hcc", "dswrf", "dlwrf", "uswrf", "ulwrf");


    /**
     * 获取气象数据的通用函数
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return WeatherDataResult 包含时间戳和指标值的结果
     */
    public static GfsSurfaceDataResult fetchGfsSurfaceData(double longitude, double latitude) {
        try {
            String token = SecretConfigManager.getWeatherApiToken();
            String apiUrl = BASE_API_URL + "/" + dataType + "/multi/point";

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("token", token);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
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

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                    && response.getBody().containsKey("data")
                    && response.getBody().get("data") != null) {
                return parseWeatherResponse((Map)response.getBody().get("data"), longitude, latitude);
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
    private static GfsSurfaceDataResult parseWeatherResponse(Map<String, Object> responseBody, double longitude, double latitude) {
        try {
            // 首先打印原始响应以便调试
            logger.info("原始API响应: {}", objectMapper.writeValueAsString(responseBody));

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

            return new GfsSurfaceDataResult(location, timestamps, metricValues, metricVars, metricUnits, timeFcst);

        } catch (Exception e) {
            logger.error("解析响应数据失败: {}", e.getMessage(), e);
            try {
                logger.error("响应体内容: {}", objectMapper.writeValueAsString(responseBody));
            } catch (Exception ex) {
                logger.error("无法序列化响应体: {}", ex.getMessage());
            }
            return null;
        }
    }
}

