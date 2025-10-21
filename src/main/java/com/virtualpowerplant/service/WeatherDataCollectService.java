

package com.virtualpowerplant.service;

import com.mysql.cj.util.StringUtils;
import com.virtualpowerplant.config.SecretConfigManager;
import com.virtualpowerplant.constant.Constant;
import com.virtualpowerplant.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.virtualpowerplant.constant.Constant.objectMapper;

@Service
public class WeatherDataCollectService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherDataCollectService.class);
    private static final String BASE_API_URL = "https://api-pro-openet.terraqt.com/v1";
    private static final String dataType = "gfs_surface";

    @Autowired
    private WeatherDataLindormService weatherDataLindormService;

    @Autowired
    private DatasetMetaInfoService datasetMetaInfoService;

    @Autowired
    private DeviceService deviceService;

    /**
     * 定期拿到库所有坐标，构造APi参数访问天气数据
     */
    public void collectWeatherData(String time) {
        try {
            logger.info("开始定时收集天气预报...");
            List<String> invertersLocations = deviceService.getInverterLocations();
            List<String> metaTypes = datasetMetaInfoService.selectAllValidMetaType();
            collectAndMergeWeatherData(invertersLocations, metaTypes, time);
        } catch (Exception e) {
            logger.error("定时收集天气预报失败: {}", e.getMessage(), e);
        }
    }

    @Scheduled(fixedRate = 4*60*60*1000)
    public void collectWeatherData() {
        collectWeatherData("");
    }

    /**
     * 构造APi参数访问天气数据
     */
    private WeatherForestData fetchWeatherDataFromApi(double longitude, double latitude, String metaType, String time) {
        try {
            String token = SecretConfigManager.getWeatherApiToken();
            String apiUrl = BASE_API_URL + "/" + dataType + "/point";

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
            if (!StringUtils.isNullOrEmpty(time)) {
                requestBody.put("time", time);
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

    /**
     * 解析API响应数据
     */
    private static WeatherForestData parseWeatherResponse(Map<String, Object> responseBody, double longitude, double latitude, String metaType) {
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

            return new WeatherForestData(location, timestamps, metricValues, metricVars, metricUnits, timeFcst, metaType);

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

    /**
     * 将预报结果和设备信息合并写入Lindorm
     */
    private void collectAndMergeWeatherData(List<String> invertersLocations, List<String> metaTypes, String time) {
        try {
            for (String invertersLocation : invertersLocations) {
                for (String metaType : metaTypes) {
                    Double longitude = Double.valueOf(invertersLocation.split("#")[0]);
                    Double latitude = Double.valueOf(invertersLocation.split("#")[1]);
                    WeatherForestData weatherForestData = fetchWeatherDataFromApi(longitude, latitude, metaType, time);
                    if (weatherForestData == null || weatherForestData.getTimestamps() == null || weatherForestData.getTimestamps().isEmpty()) {
                        logger.error("位置 [{}, {}] 未获取到天气数据", latitude, longitude);
                        return;
                    }
                    weatherDataLindormService.writeWeatherData(weatherForestData);
                }
            }
        } catch (Exception e) {
            logger.error("收集和合并天气数据失败: {}", e.getMessage(), e);
        }
    }
}

