package com.virtualpowerplant.service;

import com.virtualpowerplant.mapper.DeviceMapper;
import com.virtualpowerplant.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InverterRealTimeDataService {

    private static final Logger logger = LoggerFactory.getLogger(InverterRealTimeDataService.class);

    @Autowired
    private LindormTSDBService lindormTSDBService;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private InverterGfsSurfaceLindormService inverterGfsSurfaceLindormService;

    @Autowired
    private GfsSurfaceDataService gfsSurfaceDataService;

    @Scheduled(fixedRate = 60000)
    public void collectInverterRealTimeData() {
        try {
            logger.info("开始定时收集逆变器实时数据和天气预报...");
            long startTime = System.currentTimeMillis();

            List<Device> inverters = deviceMapper.selectInverters();
            if (inverters.isEmpty()) {
                logger.warn("未找到任何逆变器设备，跳过实时数据收集");
                return;
            }

            List<String> snList = inverters.stream()
                    .map(Device::getDeviceSn)
                    .filter(sn -> sn != null && !sn.isEmpty())
                    .collect(Collectors.toList());

            if (snList.isEmpty()) {
                logger.warn("逆变器设备没有有效的序列号，跳过实时数据收集");
                return;
            }

            logger.info("准备收集 {} 台逆变器的实时数据，序列号: {}", snList.size(), snList);

            // 1. 收集逆变器实时数据
            List<InverterRealTimeData> realTimeDataList = SunGrowDataService.getRealTimeDataAndParse(snList, inverters);

            if (realTimeDataList != null && !realTimeDataList.isEmpty()) {
                lindormTSDBService.writeRealTimeData(realTimeDataList);
                logger.info("成功保存 {} 条逆变器实时数据到Lindorm", realTimeDataList.size());
            } else {
                logger.warn("未获取到任何逆变器实时数据");
            }

            // 2. 根据逆变器坐标收集天气预报数据
            try {
                collectAndMergeWeatherData(inverters);
            } catch (Exception e) {
                logger.error("收集天气预报数据失败: {}", e.getMessage(), e);
            }

            long endTime = System.currentTimeMillis();
            logger.info("逆变器实时数据和天气预报收集完成，耗时: {} ms", endTime - startTime);

        } catch (Exception e) {
            logger.error("定时收集逆变器实时数据失败: {}", e.getMessage(), e);
        }
    }

    public List<InverterRealTimeData> getLatestData(int limit) {
        try {
            return lindormTSDBService.queryLatestData(limit);
        } catch (Exception e) {
            logger.error("获取最新实时数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> getDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            long startTimestamp = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endTimestamp = endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return lindormTSDBService.queryByTimeRange(startTimestamp, endTimestamp);
        } catch (Exception e) {
            logger.error("根据时间范围获取实时数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> getInverterDataByTimeRange(String inverterSn, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 可以通过先查询指定逆变器的数据，然后过滤时间范围，或者直接在Lindorm中查询
            List<InverterRealTimeData> allData = lindormTSDBService.queryByInverterSn(inverterSn, 1000);
            return allData.stream()
                    .filter(data -> data.getDeviceTime() != null &&
                            !data.getDeviceTime().isBefore(startTime) &&
                            !data.getDeviceTime().isAfter(endTime))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("根据逆变器和时间范围获取实时数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> getLatestInverterData(String inverterSn, int limit) {
        try {
            return lindormTSDBService.queryByInverterSn(inverterSn, limit);
        } catch (Exception e) {
            logger.error("获取逆变器最新实时数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> getLatestPowerStationData(String psKey, int limit) {
        try {
            return lindormTSDBService.queryByPowerStation(psKey, limit);
        } catch (Exception e) {
            logger.error("获取电站最新实时数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
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


            GfsSurfaceDataResult gfsSurfaceDataResult = gfsSurfaceDataService.fetchGfsSurfaceData(longitude, latitude);

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