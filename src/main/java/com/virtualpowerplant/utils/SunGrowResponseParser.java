package com.virtualpowerplant.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualpowerplant.model.SunGrowResponse;
import com.virtualpowerplant.model.SunGrowUserInfo;
import com.virtualpowerplant.model.PowerStationList;
import com.virtualpowerplant.model.PowerStation;
import com.virtualpowerplant.model.DeviceList;
import com.virtualpowerplant.model.Device;
import com.virtualpowerplant.model.InverterRealTimeData;
import com.virtualpowerplant.model.RealTimeDataResponse;
import com.virtualpowerplant.model.DevicePoint;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SunGrowResponseParser {

    private static final Logger logger = LoggerFactory.getLogger(SunGrowResponseParser.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> SunGrowResponse<T> parseResponse(String jsonResponse, Class<T> dataType) {
        try {
            TypeReference<SunGrowResponse<T>> typeRef = new TypeReference<SunGrowResponse<T>>() {};
            SunGrowResponse<T> response = objectMapper.readValue(jsonResponse, typeRef);

            if (response.getResultData() != null) {
                String dataJson = objectMapper.writeValueAsString(response.getResultData());
                T typedData = objectMapper.readValue(dataJson, dataType);
                response.setResultData(typedData);
            }

            return response;
        } catch (Exception e) {
            logger.error("解析SunGrow响应失败: {}", e.getMessage(), e);
            logger.debug("原始响应内容: {}", jsonResponse);
            throw new RuntimeException("解析SunGrow响应失败", e);
        }
    }

    public static SunGrowResponse<SunGrowUserInfo> parseLoginResponse(String jsonResponse) {
        return parseResponse(jsonResponse, SunGrowUserInfo.class);
    }

    public static void validateResponse(SunGrowResponse<?> response) {
        if (response == null) {
            throw new RuntimeException("响应为空");
        }

        if (!response.isSuccess()) {
            String errorMsg = String.format("API调用失败 - 错误码: %s, 错误消息: %s",
                response.getResultCode(), response.getResultMsg());
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    public static <T> T extractData(SunGrowResponse<T> response) {
        validateResponse(response);
        return response.getResultData();
    }

    public static SunGrowUserInfo extractUserInfo(String jsonResponse) {
        SunGrowResponse<SunGrowUserInfo> response = parseLoginResponse(jsonResponse);
        return extractData(response);
    }

    public static SunGrowResponse<PowerStationList> parsePowerStationListResponse(String jsonResponse) {
        return parseResponse(jsonResponse, PowerStationList.class);
    }

    public static PowerStationList extractPowerStationList(String jsonResponse) {
        SunGrowResponse<PowerStationList> response = parsePowerStationListResponse(jsonResponse);
        return extractData(response);
    }

    public static List<PowerStation> extractPowerStations(String jsonResponse) {
        PowerStationList powerStationList = extractPowerStationList(jsonResponse);
        return powerStationList != null ? powerStationList.getPageList() : null;
    }

    public static SunGrowResponse<DeviceList> parseDeviceListResponse(String jsonResponse) {
        return parseResponse(jsonResponse, DeviceList.class);
    }

    public static DeviceList extractDeviceList(String jsonResponse) {
        SunGrowResponse<DeviceList> response = parseDeviceListResponse(jsonResponse);
        return extractData(response);
    }

    public static List<Device> extractDevices(String jsonResponse) {
        DeviceList deviceList = extractDeviceList(jsonResponse);
        return deviceList != null ? deviceList.getPageList() : null;
    }

    public static List<InverterRealTimeData> extractRealTimeData(String jsonResponse) {
        try {
            TypeReference<SunGrowResponse<List<InverterRealTimeData>>> typeRef =
                new TypeReference<SunGrowResponse<List<InverterRealTimeData>>>() {};
            SunGrowResponse<List<InverterRealTimeData>> response = objectMapper.readValue(jsonResponse, typeRef);

            validateResponse(response);
            List<InverterRealTimeData> realTimeDataList = response.getResultData();

            LocalDateTime deviceTime = LocalDateTime.now();
            if (realTimeDataList != null) {
                for (InverterRealTimeData data : realTimeDataList) {
                    data.setDeviceTime(deviceTime);
                }
            }

            return realTimeDataList;
        } catch (Exception e) {
            logger.error("解析实时数据失败: {}", e.getMessage(), e);
            logger.debug("原始响应内容: {}", jsonResponse);
            return null;
        }
    }

    public static List<InverterRealTimeData> extractRealTimeDataWithDeviceInfo(String jsonResponse, List<Device> devices) {
        try {
            RealTimeDataResponse response = objectMapper.readValue(jsonResponse, RealTimeDataResponse.class);

            if (!"1".equals(response.getResultCode())) {
                logger.error("API调用失败 - 错误码: {}, 错误消息: {}", response.getResultCode(), response.getResultMsg());
                throw new RuntimeException("API调用失败: " + response.getResultMsg());
            }

            if (response.getResultData() == null || response.getResultData().getDevicePointList() == null) {
                logger.warn("API响应中没有设备数据");
                return new ArrayList<>();
            }

            Map<String, Device> deviceMap = devices != null ? devices.stream()
                    .collect(Collectors.toMap(Device::getDeviceSn, device -> device, (existing, replacement) -> existing))
                    : new java.util.HashMap<>();

            List<InverterRealTimeData> realTimeDataList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            for (RealTimeDataResponse.DevicePointWrapper wrapper : response.getResultData().getDevicePointList()) {
                DevicePoint devicePoint = wrapper.getDevicePoint();
                if (devicePoint == null) continue;

                InverterRealTimeData realTimeData = new InverterRealTimeData();
                realTimeData.setInverterSn(devicePoint.getDeviceSn());

                // 解析p24字段作为有功功率
                if (devicePoint.getP24() != null && !devicePoint.getP24().isEmpty()) {
                    try {
                        realTimeData.setActivePower(Double.parseDouble(devicePoint.getP24()));
                    } catch (NumberFormatException e) {
                        logger.warn("无法解析有功功率值: {}", devicePoint.getP24());
                        realTimeData.setActivePower(0.0);
                    }
                } else {
                    realTimeData.setActivePower(0.0);
                }

                // 解析设备时间 (YYYYmmDDHHmmss格式转换为LocalDateTime)
                if (devicePoint.getDeviceTime() != null && !devicePoint.getDeviceTime().isEmpty()) {
                    try {
                        realTimeData.setDeviceTime(LocalDateTime.parse(devicePoint.getDeviceTime(), formatter));
                    } catch (Exception e) {
                        logger.warn("无法解析设备时间: {}", devicePoint.getDeviceTime());
                        realTimeData.setDeviceTime(LocalDateTime.now());
                    }
                } else {
                    realTimeData.setDeviceTime(LocalDateTime.now());
                }

                // 从Device信息中填充电站信息
                Device device = deviceMap.get(devicePoint.getDeviceSn());
                if (device != null) {
                    realTimeData.setPsName(device.getPsName());
                    realTimeData.setPsKey(device.getPsKey());
                    realTimeData.setLatitude(device.getLatitude());
                    realTimeData.setLongitude(device.getLongitude());
                } else {
                    // 如果没有找到对应的Device，使用API返回的基本信息
                    realTimeData.setPsKey(devicePoint.getPsKey());
                    logger.debug("未找到设备 {} 的详细信息，使用API返回的基本信息", devicePoint.getDeviceSn());
                }

                realTimeDataList.add(realTimeData);
            }

            logger.info("成功解析 {} 条实时数据", realTimeDataList.size());
            return realTimeDataList;
        } catch (Exception e) {
            logger.error("解析带设备信息的实时数据失败: {}", e.getMessage(), e);
            logger.debug("原始响应内容: {}", jsonResponse);
            return new ArrayList<>();
        }
    }
}