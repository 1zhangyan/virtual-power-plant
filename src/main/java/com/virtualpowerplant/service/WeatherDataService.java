

package com.virtualpowerplant.service;

import com.virtualpowerplant.config.SecretConfigManager;
import com.virtualpowerplant.constant.Constant;
import com.virtualpowerplant.model.*;
import com.virtualpowerplant.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.virtualpowerplant.constant.Constant.objectMapper;
import static com.virtualpowerplant.utils.TimeUtils.getCurrentDayStart;
import static com.virtualpowerplant.utils.TimeUtils.getNextDayStart;

@Service
public class WeatherDataService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherDataService.class);
    private static final String BASE_API_URL = "https://api-pro-openet.terraqt.com/v1";
    @Autowired
    private WeatherDataLindormService weatherDataLindormService;
    @Autowired
    private DatasetMetaInfoService datasetMetaInfoService;
    @Autowired
    private VppDeviceService vppDeviceService;

    @Autowired
    private RedisTemplate<String, List<Double>> redisTemplate;

    //    @Scheduled(fixedRate = 4*60*60*1000)
    public void collectWeatherData(Long vppId) {
        try {
            logger.info("开始定时收集天气预报...");
            List<String> invertersLocations = vppDeviceService.getInverterLocations(vppId);
            List<String> metaTypes = datasetMetaInfoService.selectAllValidMetaType();
            collectAndMergeWeatherData(invertersLocations, metaTypes);
        } catch (Exception e) {
            logger.error("定时收集天气预报失败: {}", e.getMessage(), e);
        }
    }
    public List<Double> getWeatherDataByKey(Double longitude, Double latitude, String timeStr, String metaType) {
        timeStr = TimeUtils.getHourStart(timeStr);
        List<Double> result = redisTemplate.opsForValue().get(getRedisKey(longitude, latitude, timeStr, metaType));
        if (null == result || CollectionUtils.isEmpty(result)) {
            List<SimpleWeatherForestData> simpleWeatherForestData = weatherDataLindormService.selectWeatherData(longitude, latitude, timeStr, metaType);
            if (simpleWeatherForestData == null || CollectionUtils.isEmpty(simpleWeatherForestData)) {
                throw new RuntimeException(String.format("无法获取 longitude %s, latitude %s, timeStr %s, metaType %s", longitude, latitude, timeStr, metaType));
            }
            result = simpleWeatherForestData.get(0).getMetricValues();
            redisTemplate.opsForValue().set(getRedisKey(longitude, latitude, timeStr, metaType),result);
        }
        return result;
    }

    private String getRedisKey(Double longitude, Double latitude, String timeStr, String metaType){
        return String.format("%s_%s_%s_%s", longitude, latitude, timeStr, metaType).replace(" ","");
    }
    private TerraqtForestData fetchWeatherDataFromApi(double longitude, double latitude, String metaType) {
        try {
            String token = SecretConfigManager.getWeatherApiToken();
            String apiUrl = BASE_API_URL + "/" + metaType + "/point";

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("token", token);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("lon", longitude);
            requestBody.put("lat", latitude);
            List<String> metaVars = datasetMetaInfoService.selectMetaVarByMetaType(metaType);
            requestBody.put("mete_vars", metaVars);

            if (metaType.equals("gdas_surface")) {
                requestBody.put("start_time", getCurrentDayStart());
                requestBody.put("end_time", getNextDayStart());
            }

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
                return parseWeatherResponse((Map) response.getBody().get("data"), longitude, latitude, metaType);
            } else {
                logger.warn("API请求失败 - HTTP {}", objectMapper.writeValueAsString(response));
                return null;
            }

        } catch (Exception e) {
            logger.error("获取气象数据失败: {}", e.getMessage(), e);
            return null;
        }
    }
    private static TerraqtForestData parseWeatherResponse(Map<String, Object> responseBody, double longitude, double latitude, String metaType) {
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
            List<List<Double>> metricValues = new ArrayList<>();

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
                                                metricValues.add((List<Double>) timePointObj);
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }

            return new TerraqtForestData(location, timestamps, metricValues, metricVars, metricUnits, timeFcst, metaType);

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
    private void collectAndMergeWeatherData(List<String> invertersLocations, List<String> metaTypes) {
        try {
            for (String invertersLocation : invertersLocations) {
                for (String metaType : metaTypes) {
                    Double longitude = Double.valueOf(invertersLocation.split("#")[0]);
                    Double latitude = Double.valueOf(invertersLocation.split("#")[1]);
                    TerraqtForestData weatherForestData = fetchWeatherDataFromApi(longitude, latitude, metaType);
                    if (weatherForestData == null || weatherForestData.getTimestamps() == null || weatherForestData.getTimestamps().isEmpty()) {
                        logger.error("数据集{} 未获取到天气数据", metaType );
                        continue;
                    }
                    weatherDataLindormService.writeWeatherData(weatherForestData);
                }
            }
        } catch (Exception e) {
            logger.error("收集和合并天气数据失败: {}", e.getMessage(), e);
        }
    }
}

