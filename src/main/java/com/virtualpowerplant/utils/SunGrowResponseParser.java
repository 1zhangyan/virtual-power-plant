package com.virtualpowerplant.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtualpowerplant.model.SunGrowResponse;
import com.virtualpowerplant.model.SunGrowUserInfo;
import com.virtualpowerplant.model.PowerStationList;
import com.virtualpowerplant.model.PowerStation;

import java.util.List;
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
}