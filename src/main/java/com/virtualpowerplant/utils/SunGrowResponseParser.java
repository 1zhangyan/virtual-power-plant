package com.virtualpowerplant.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualpowerplant.model.SunGrowResponse;
import com.virtualpowerplant.model.SunGrowUserInfo;
import com.virtualpowerplant.model.PowerStationList;
import com.virtualpowerplant.model.PowerStation;
import com.virtualpowerplant.model.PowerStationValue;
import com.virtualpowerplant.model.SungrowDeviceList;
import com.virtualpowerplant.model.SungrowDevice;
import com.virtualpowerplant.model.DeviceRealTimeData;
import com.virtualpowerplant.model.RealTimeDataResponse;
import com.virtualpowerplant.model.SungrowDevicePoint;

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
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            SunGrowResponse<T> response = new SunGrowResponse<>();

            response.setReqSerialNum(getTextValue(rootNode, "req_serial_num"));
            response.setResultCode(getTextValue(rootNode, "result_code"));
            response.setResultMsg(getTextValue(rootNode, "result_msg"));

            JsonNode resultDataNode = rootNode.get("result_data");
            if (resultDataNode != null && !resultDataNode.isNull()) {
                T typedData = parseDataByType(resultDataNode, dataType);
                response.setResultData(typedData);
            }

            return response;
        } catch (Exception e) {
            logger.error("解析SunGrow响应失败: {}", e.getMessage(), e);
            logger.debug("原始响应内容: {}", jsonResponse);
            throw new RuntimeException("解析SunGrow响应失败", e);
        }
    }

    private static String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }

    private static Integer getIntValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asInt() : null;
    }

    private static Long getLongValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asLong() : null;
    }

    private static Double getDoubleValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asDouble() : null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseDataByType(JsonNode dataNode, Class<T> dataType) {
        if (dataType == SunGrowUserInfo.class) {
            return (T) parseSunGrowUserInfo(dataNode);
        } else if (dataType == PowerStationList.class) {
            return (T) parsePowerStationList(dataNode);
        } else if (dataType == SungrowDeviceList.class) {
            return (T) parseDeviceList(dataNode);
        } else {
            try {
                return objectMapper.treeToValue(dataNode, dataType);
            } catch (Exception e) {
                logger.error("无法解析数据类型: {}", dataType.getName(), e);
                return null;
            }
        }
    }

    private static SunGrowUserInfo parseSunGrowUserInfo(JsonNode node) {
        SunGrowUserInfo userInfo = new SunGrowUserInfo();
        userInfo.setUserMasterOrgId(getTextValue(node, "user_master_org_id"));
        userInfo.setMobileTel(getTextValue(node, "mobile_tel"));
        userInfo.setUserName(getTextValue(node, "user_name"));
        userInfo.setLanguage(getTextValue(node, "language"));
        userInfo.setToken(getTextValue(node, "token"));
        userInfo.setErrTimes(getTextValue(node, "err_times"));
        userInfo.setUserId(getTextValue(node, "user_id"));
        userInfo.setLoginState(getTextValue(node, "login_state"));
        userInfo.setDisableTime(getTextValue(node, "disable_time"));
        userInfo.setCountryName(getTextValue(node, "country_name"));
        userInfo.setUserAccount(getTextValue(node, "user_account"));
        userInfo.setUserMasterOrgName(getTextValue(node, "user_master_org_name"));
        userInfo.setEmail(getTextValue(node, "email"));
        userInfo.setCountryId(getTextValue(node, "country_id"));
        return userInfo;
    }

    private static PowerStationList parsePowerStationList(JsonNode node) {
        PowerStationList list = new PowerStationList();
        list.setRowCount(getIntValue(node, "rowCount"));

        JsonNode pageListNode = node.get("pageList");
        if (pageListNode != null && pageListNode.isArray()) {
            List<PowerStation> powerStations = new ArrayList<>();
            for (JsonNode stationNode : pageListNode) {
                powerStations.add(parsePowerStation(stationNode));
            }
            list.setPageList(powerStations);
        }
        return list;
    }

    private static PowerStation parsePowerStation(JsonNode node) {
        PowerStation station = new PowerStation();
        station.setPsId(getLongValue(node, "ps_id"));
        station.setPsName(getTextValue(node, "ps_name"));
        station.setPsLocation(getTextValue(node, "ps_location"));
        station.setProvinceName(getTextValue(node, "province_name"));
        station.setCityName(getTextValue(node, "city_name"));
        station.setDistrictName(getTextValue(node, "district_name"));
        station.setLatitude(getDoubleValue(node, "latitude"));
        station.setLongitude(getDoubleValue(node, "longitude"));

        station.setTotalEnergy(parsePowerStationValue(node.get("total_energy")));
        station.setTodayEnergy(parsePowerStationValue(node.get("today_energy")));
        station.setCurrPower(parsePowerStationValue(node.get("curr_power")));
        station.setTotalIncome(parsePowerStationValue(node.get("total_income")));
        station.setTodayIncome(parsePowerStationValue(node.get("today_income")));
        station.setMonthIncome(parsePowerStationValue(node.get("month_income")));
        station.setYearIncome(parsePowerStationValue(node.get("year_income")));
        station.setTotalCapacity(parsePowerStationValue(node.get("total_capcity")));
        station.setCo2Reduce(parsePowerStationValue(node.get("co2_reduce")));
        station.setCo2ReduceTotal(parsePowerStationValue(node.get("co2_reduce_total")));
        station.setEquivalentHour(parsePowerStationValue(node.get("equivalent_hour")));

        station.setPsStatus(getIntValue(node, "ps_status"));
        station.setPsFaultStatus(getIntValue(node, "ps_fault_status"));
        station.setGridConnectionStatus(getIntValue(node, "grid_connection_status"));
        station.setBuildStatus(getIntValue(node, "build_status"));
        station.setPsType(getIntValue(node, "ps_type"));
        station.setConnectType(getIntValue(node, "connect_type"));
        station.setValidFlag(getIntValue(node, "valid_flag"));
        station.setAlarmCount(getIntValue(node, "alarm_count"));
        station.setFaultCount(getIntValue(node, "fault_count"));
        station.setInstallDate(getTextValue(node, "install_date"));
        station.setGridConnectionTime(getLongValue(node, "grid_connection_time"));
        station.setShareType(getTextValue(node, "share_type"));
        station.setPsCurrentTimeZone(getTextValue(node, "ps_current_time_zone"));
        station.setDescription(getTextValue(node, "description"));
        station.setTotalEnergyUpdateTime(getTextValue(node, "total_energy_update_time"));
        station.setTodayEnergyUpdateTime(getTextValue(node, "today_energy_update_time"));
        station.setCurrPowerUpdateTime(getTextValue(node, "curr_power_update_time"));
        station.setTotalIncomeUpdateTime(getTextValue(node, "total_income_update_time"));
        station.setTodayIncomeUpdateTime(getTextValue(node, "today_income_update_time"));
        station.setMonthIncomeUpdateTime(getTextValue(node, "month_income_update_time"));
        station.setYearIncomeUpdateTime(getTextValue(node, "year_income_update_time"));
        station.setTotalCapacityUpdateTime(getTextValue(node, "total_capcity_update_time"));
        station.setCo2ReduceUpdateTime(getTextValue(node, "co2_reduce_update_time"));
        station.setCo2ReduceTotalUpdateTime(getTextValue(node, "co2_reduce_total_update_time"));
        station.setEquivalentHourUpdateTime(getTextValue(node, "equivalent_hour_update_time"));

        return station;
    }

    private static PowerStationValue parsePowerStationValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        PowerStationValue value = new PowerStationValue();
        value.setUnit(getTextValue(node, "unit"));
        value.setValue(getTextValue(node, "value"));
        return value;
    }

    private static SungrowDeviceList parseDeviceList(JsonNode node) {
        SungrowDeviceList list = new SungrowDeviceList();
        list.setRowCount(getIntValue(node, "rowCount"));

        JsonNode pageListNode = node.get("pageList");
        if (pageListNode != null && pageListNode.isArray()) {
            List<SungrowDevice> devices = new ArrayList<>();
            for (JsonNode deviceNode : pageListNode) {
                devices.add(parseDevice(deviceNode));
            }
            list.setPageList(devices);
        }
        return list;
    }

    private static SungrowDevice parseDevice(JsonNode node) {
        SungrowDevice device = new SungrowDevice();
        device.setUuid(getLongValue(node, "uuid"));
        device.setPsId(getLongValue(node, "ps_id"));
        device.setPsName(getTextValue(node, "ps_name"));
        device.setPsType(getIntValue(node, "ps_type"));
        device.setOnlineStatus(getIntValue(node, "online_status"));
        device.setProvinceName(getTextValue(node, "province_name"));
        device.setCityName(getTextValue(node, "city_name"));
        device.setDistrictName(getTextValue(node, "district_name"));
        device.setConnectType(getIntValue(node, "connect_type"));
        device.setDeviceName(getTextValue(node, "device_name"));
        device.setDeviceSn(getTextValue(node, "device_sn"));
        device.setDeviceType(getIntValue(node, "device_type"));
        device.setDeviceCode(getIntValue(node, "device_code"));
        device.setTypeName(getTextValue(node, "type_name"));
        device.setDeviceModelCode(getTextValue(node, "device_model_code"));
        device.setDeviceModelId(getLongValue(node, "device_model_id"));
        device.setFactoryName(getTextValue(node, "factory_name"));
        device.setChannelId(getIntValue(node, "chnnl_id"));
        device.setPsKey(getTextValue(node, "ps_key"));
        device.setCommunicationDevSn(getTextValue(node, "communication_dev_sn"));
        device.setDevStatus(getTextValue(node, "dev_status"));
        device.setDevFaultStatus(getIntValue(node, "dev_fault_status"));
        device.setRelState(getIntValue(node, "rel_state"));
        device.setRelTime(getTextValue(node, "rel_time"));
        device.setGridConnectionDate(getTextValue(node, "grid_connection_date"));
        device.setLatitude(getDoubleValue(node, "latitude"));
        device.setLongitude(getDoubleValue(node, "longitude"));
        device.setVppId(getLongValue(node, "vpp_id"));
        return device;
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

    public static SunGrowResponse<SungrowDeviceList> parseDeviceListResponse(String jsonResponse) {
        return parseResponse(jsonResponse, SungrowDeviceList.class);
    }

    public static SungrowDeviceList extractDeviceList(String jsonResponse) {
        SunGrowResponse<SungrowDeviceList> response = parseDeviceListResponse(jsonResponse);
        return extractData(response);
    }

    public static List<SungrowDevice> extractDevices(String jsonResponse) {
        SungrowDeviceList deviceList = extractDeviceList(jsonResponse);
        return deviceList != null ? deviceList.getPageList() : null;
    }

    public static List<DeviceRealTimeData> extractRealTimeData(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            SunGrowResponse<List<DeviceRealTimeData>> response = new SunGrowResponse<>();

            response.setReqSerialNum(getTextValue(rootNode, "req_serial_num"));
            response.setResultCode(getTextValue(rootNode, "result_code"));
            response.setResultMsg(getTextValue(rootNode, "result_msg"));

            JsonNode resultDataNode = rootNode.get("result_data");
            List<DeviceRealTimeData> realTimeDataList = new ArrayList<>();
            if (resultDataNode != null && resultDataNode.isArray()) {
                for (JsonNode dataNode : resultDataNode) {
                    DeviceRealTimeData data = objectMapper.treeToValue(dataNode, DeviceRealTimeData.class);
                    if (data != null) {
                        realTimeDataList.add(data);
                    }
                }
            }
            response.setResultData(realTimeDataList);

            validateResponse(response);

            LocalDateTime deviceTime = LocalDateTime.now();
            if (realTimeDataList != null) {
                for (DeviceRealTimeData data : realTimeDataList) {
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

    public static List<DeviceRealTimeData> extractRealTimeDataWithDeviceInfo(String jsonResponse, List<SungrowDevice> devices) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            RealTimeDataResponse response = parseRealTimeDataResponse(rootNode);

            if (!"1".equals(response.getResultCode())) {
                logger.error("API调用失败 - 错误码: {}, 错误消息: {}", response.getResultCode(), response.getResultMsg());
                throw new RuntimeException("API调用失败: " + response.getResultMsg());
            }

            if (response.getResultData() == null || response.getResultData().getDevicePointList() == null) {
                logger.warn("API响应中没有设备数据");
                return new ArrayList<>();
            }

            Map<String, SungrowDevice> deviceMap = devices != null ? devices.stream()
                    .collect(Collectors.toMap(SungrowDevice::getDeviceSn, device -> device, (existing, replacement) -> existing))
                    : new java.util.HashMap<>();

            List<DeviceRealTimeData> realTimeDataList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            for (RealTimeDataResponse.DevicePointWrapper wrapper : response.getResultData().getDevicePointList()) {
                SungrowDevicePoint devicePoint = wrapper.getDevicePoint();
                if (devicePoint == null) continue;

                DeviceRealTimeData realTimeData = new DeviceRealTimeData();
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
                SungrowDevice device = deviceMap.get(devicePoint.getDeviceSn());
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

    private static RealTimeDataResponse parseRealTimeDataResponse(JsonNode rootNode) {
        RealTimeDataResponse response = new RealTimeDataResponse();
        response.setReqSerialNum(getTextValue(rootNode, "req_serial_num"));
        response.setResultCode(getTextValue(rootNode, "result_code"));
        response.setResultMsg(getTextValue(rootNode, "result_msg"));

        JsonNode resultDataNode = rootNode.get("result_data");
        if (resultDataNode != null && !resultDataNode.isNull()) {
            RealTimeDataResponse.ResultData resultData = new RealTimeDataResponse.ResultData();

            JsonNode failSnListNode = resultDataNode.get("fail_sn_list");
            if (failSnListNode != null && failSnListNode.isArray()) {
                List<String> failSnList = new ArrayList<>();
                for (JsonNode snNode : failSnListNode) {
                    failSnList.add(snNode.asText());
                }
                resultData.setFailSnList(failSnList);
            }

            JsonNode devicePointListNode = resultDataNode.get("device_point_list");
            if (devicePointListNode != null && devicePointListNode.isArray()) {
                List<RealTimeDataResponse.DevicePointWrapper> devicePointList = new ArrayList<>();
                for (JsonNode wrapperNode : devicePointListNode) {
                    RealTimeDataResponse.DevicePointWrapper wrapper = new RealTimeDataResponse.DevicePointWrapper();
                    JsonNode devicePointNode = wrapperNode.get("device_point");
                    if (devicePointNode != null && !devicePointNode.isNull()) {
                        SungrowDevicePoint devicePoint = parseDevicePoint(devicePointNode);
                        wrapper.setDevicePoint(devicePoint);
                    }
                    devicePointList.add(wrapper);
                }
                resultData.setDevicePointList(devicePointList);
            }

            response.setResultData(resultData);
        }

        return response;
    }

    private static SungrowDevicePoint parseDevicePoint(JsonNode node) {
        SungrowDevicePoint devicePoint = new SungrowDevicePoint();
        devicePoint.setDeviceSn(getTextValue(node, "device_sn"));
        devicePoint.setPsKey(getTextValue(node, "ps_key"));
        devicePoint.setDeviceName(getTextValue(node, "device_name"));
        devicePoint.setDeviceTime(getTextValue(node, "device_time"));
        devicePoint.setP24(getTextValue(node, "p24"));
        devicePoint.setDevStatus(getIntValue(node, "dev_status"));
        return devicePoint;
    }
}