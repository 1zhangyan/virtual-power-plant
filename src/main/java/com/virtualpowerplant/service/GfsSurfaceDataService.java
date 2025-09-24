

package com.virtualpowerplant.service;

import com.virtualpowerplant.config.SecretConfigManager;
import com.virtualpowerplant.constant.Constant;
import com.virtualpowerplant.mapper.DeviceMapper;
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

import java.time.LocalDateTime;
import java.util.*;

import static com.virtualpowerplant.constant.Constant.objectMapper;

@Service
public class GfsSurfaceDataService {

    private static final Logger logger = LoggerFactory.getLogger(GfsSurfaceDataService.class);
    private static final String BASE_API_URL = "https://api-pro-openet.terraqt.com/v1";
    private static final String dataType = "gfs_surface";

    private static final List<String> metaVars = Arrays.asList("tcc", "lcc", "mcc", "hcc", "dswrf", "dlwrf", "uswrf", "ulwrf");
    @Autowired
    private InverterGfsSurfaceLindormService inverterGfsSurfaceLindormService;

    @Autowired
    private DeviceMapper deviceMapper;

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

    @Scheduled(fixedRate = 6*60*60*1000)
    public void collectWeatherData() {
        try {
            logger.info("开始定时收集天气预报...");
            long startTime = System.currentTimeMillis();

            List<Device> inverters = deviceMapper.selectInverters();
            // 2. 根据逆变器坐标收集天气预报数据
                collectAndMergeWeatherData(inverters);
            long endTime = System.currentTimeMillis();
            logger.info("天气预报收集完成，耗时: {} ms", endTime - startTime);

        } catch (Exception e) {
            logger.error("定时收集天气预报失败: {}", e.getMessage(), e);
        }
    }
    /**
     * 收集并合并天气预报数据
     * 根据逆变器坐标查询天气预报，将预报结果和设备信息合并写入Lindorm
     */
    private void collectAndMergeWeatherData(List<Device> inverters) {
        try {
            logger.info("开始收集天气预报数据，设备数量: {}", inverters.size());

            // 按坐标分组，避免重复查询相同位置的天气数据
            Map<String, List<Device>> locationGroups = new HashMap<>();

            for (Device device : inverters) {
                if (device.getLatitude() != null && device.getLongitude() != null) {
                    String locationKey = String.format("%.4f,%.4f", device.getLatitude(), device.getLongitude());
                    locationGroups.computeIfAbsent(locationKey, k -> new ArrayList<>()).add(device);
                }
            }

            logger.info("去重后需要查询天气的位置数量: {}", locationGroups.size());

            List<InverterWeatherData> allInverterWeatherData = new ArrayList<>();
            LocalDateTime currentTime = LocalDateTime.now();

            for (Map.Entry<String, List<Device>> entry : locationGroups.entrySet()) {
                try {
                    List<Device> devicesAtLocation = entry.getValue();
                    Device firstDevice = devicesAtLocation.get(0);

                    logger.debug("查询位置 [{}, {}] 的天气预报，该位置有 {} 台设备",
                            firstDevice.getLatitude(), firstDevice.getLongitude(), devicesAtLocation.size());

                    // 模拟调用天气API - 这里需要根据实际的天气API来实现
                    List<GfsSurfaceData> weatherDataList = getWeatherDataForLocation(
                            firstDevice.getLatitude(), firstDevice.getLongitude()
                    );

                    if (weatherDataList != null && !weatherDataList.isEmpty()) {
                        // 取最近时间的天气数据
                        GfsSurfaceData currentWeather = weatherDataList.get(0);

                        // 为该位置的所有设备创建合并数据
                        for (Device device : devicesAtLocation) {
                            InverterWeatherData inverterWeather = new InverterWeatherData(
                                    device.getPsName(),
                                    device.getPsKey(),
                                    device.getDeviceSn(),
                                    currentTime,
                                    currentWeather.getTcc(),
                                    currentWeather.getLcc(),
                                    currentWeather.getMcc(),
                                    currentWeather.getHcc(),
                                    currentWeather.getDswrf(),
                                    currentWeather.getDlwrf(),
                                    currentWeather.getUswrf(),
                                    currentWeather.getUlwrf()
                            );
                            allInverterWeatherData.add(inverterWeather);
                        }

                        logger.debug("位置 [{}, {}] 成功获取天气数据并创建 {} 条合并记录",
                                firstDevice.getLatitude(), firstDevice.getLongitude(), devicesAtLocation.size());
                    } else {
                        logger.warn("位置 [{}, {}] 未获取到天气数据",
                                firstDevice.getLatitude(), firstDevice.getLongitude());
                    }

                } catch (Exception e) {
                    logger.error("处理位置 {} 的天气数据失败: {}", entry.getKey(), e.getMessage(), e);
                }
            }

            // 批量写入合并后的数据到Lindorm
            if (!allInverterWeatherData.isEmpty()) {
                inverterGfsSurfaceLindormService.writeInverterWeatherData(allInverterWeatherData);
                logger.info("成功写入 {} 条逆变器天气合并数据到Lindorm", allInverterWeatherData.size());
            } else {
                logger.warn("没有获取到任何天气数据可写入");
            }

        } catch (Exception e) {
            logger.error("收集和合并天气数据失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 根据坐标获取天气预报数据
     * 这里需要根据实际的天气API来实现，目前返回模拟数据
     */
    private List<GfsSurfaceData> getWeatherDataForLocation(Double latitude, Double longitude) {
        try {
            logger.debug("获取位置 [{}, {}] 的天气数据", latitude, longitude);


            GfsSurfaceDataResult gfsSurfaceDataResult = fetchGfsSurfaceData(longitude, latitude);

            if (gfsSurfaceDataResult == null || gfsSurfaceDataResult.getTimestamps() == null || gfsSurfaceDataResult.getTimestamps().isEmpty()) {
                logger.warn("位置 [{}, {}] 未获取到天气数据", latitude, longitude);
                return new ArrayList<>();
            }

            // 将GfsSurfaceDataResult转换为GfsSurfaceData列表
            List<GfsSurfaceData> result = new ArrayList<>();
            List<String> timestamps = gfsSurfaceDataResult.getTimestamps();
            Map<String, List<Double>> metricValues = gfsSurfaceDataResult.getMetricValues();

            for (int i = 0; i < timestamps.size(); i++) {
                try {
                    LocalDateTime forecastTime = LocalDateTime.parse(timestamps.get(i),
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    // 提取各个指标的值
                    Double tcc = getValueAtIndex(metricValues, "tcc", i);
                    Double lcc = getValueAtIndex(metricValues, "lcc", i);
                    Double mcc = getValueAtIndex(metricValues, "mcc", i);
                    Double hcc = getValueAtIndex(metricValues, "hcc", i);
                    Double dswrf = getValueAtIndex(metricValues, "dswrf", i);
                    Double dlwrf = getValueAtIndex(metricValues, "dlwrf", i);
                    Double uswrf = getValueAtIndex(metricValues, "uswrf", i);
                    Double ulwrf = getValueAtIndex(metricValues, "ulwrf", i);

                    GfsSurfaceData gfsData = new GfsSurfaceData(
                            latitude, longitude, forecastTime,
                            tcc, lcc, mcc, hcc, dswrf, dlwrf, uswrf, ulwrf
                    );

                    result.add(gfsData);
                } catch (Exception e) {
                    logger.warn("解析时间戳 {} 失败: {}", timestamps.get(i), e.getMessage());
                }
            }

            logger.debug("位置 [{}, {}] 成功获取 {} 条天气数据", latitude, longitude, result.size());
            return result;

        } catch (Exception e) {
            logger.error("获取位置 [{}, {}] 天气数据失败: {}", latitude, longitude, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private Double getValueAtIndex(Map<String, List<Double>> metricValues, String key, int index) {
        List<Double> values = metricValues.get(key);
        if (values != null && index < values.size()) {
            return values.get(index);
        }
        return 0.0; // 默认值
    }
}

