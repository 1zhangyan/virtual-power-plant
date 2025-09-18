package com.virtualpowerplant.service;

import static com.virtualpowerplant.constant.Constant.objectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.virtualpowerplant.config.SecretConfigManager;
import com.virtualpowerplant.constant.Constant;
import com.virtualpowerplant.model.SunGrowUserInfo;
import com.virtualpowerplant.model.PowerStation;
import com.virtualpowerplant.model.Device;
import com.virtualpowerplant.model.DeviceList;
import com.virtualpowerplant.utils.AESEncryptUtils;
import com.virtualpowerplant.utils.RSAEncryptUtils;
import com.virtualpowerplant.utils.SunGrowResponseParser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SunGrowDataService {

    private static final Logger logger = LoggerFactory.getLogger(SunGrowDataService.class);

    private static final String appKey = SecretConfigManager.getSunGrowAppKey();
    private static final String publicKey = SecretConfigManager.getSunGrowPublicKey();
    private static final String accessKey = SecretConfigManager.getSunGrowAccessKey();
    private static final String AESKey = SecretConfigManager.getSunGrowAESKey();
    private static final String userAccount = SecretConfigManager.getSunGrowUsername();
    private static final String userPassword = SecretConfigManager.getSunGrowPassword();
    private static final String baseUrl = SecretConfigManager.getSunGrowBaseUrl();
    private static final String xRandomSecretKey = RSAEncryptUtils.publicEncrypt(AESKey, publicKey);
    private static HttpHeaders headers = new HttpHeaders();

    static {
        if (appKey == null || publicKey == null || accessKey == null ||
                AESKey == null || userAccount == null || userPassword == null) {
            logger.error("SunGrow配置信息不完整，请检查secret.config文件");
            throw new RuntimeException("SunGrow配置信息不完整");
        }
        headers.set("Content-Type", "application/json");
        headers.set("x-random-secret-key",xRandomSecretKey);
        headers.set("x-access-key", accessKey);
    }

    public static String postHttpCall(String apiUrl, Map<String, Object> requestBody) throws Exception{
        if (null == requestBody) {
            requestBody = new HashMap<>();
        }
        requestBody.put("appkey", appKey);
        Map <String, Object> apiKeyParam = new HashMap<>();
        apiKeyParam.put("nonce", UUID.randomUUID().toString().replaceAll("-", ""));
        apiKeyParam.put("timestamp", System.currentTimeMillis());
        requestBody.put("api_key_param", apiKeyParam);
        String encryptedRequestBody = AESEncryptUtils.encrypt(objectMapper.writeValueAsString(requestBody), AESKey) ;
        HttpEntity<String> request = new HttpEntity<>(encryptedRequestBody, headers);
        // 发送请求
        ResponseEntity<String> response = Constant.restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                String.class
        );
        // 解密响应体
        String responseBody = response.getBody();
        return AESEncryptUtils.decrypt(responseBody, AESKey);
    }

    public static String login() throws Exception {
        String apiUrl = baseUrl + "/openapi/login";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_account", userAccount);
        requestBody.put("user_password", userPassword);
        return postHttpCall(apiUrl,requestBody);
    }

    public static String getPowerStationList(String token) throws Exception {
        String apiUrl = baseUrl + "/openapi/getPowerStationList";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("curPage", 1);
        requestBody.put("size", 999);
        requestBody.put("token", token);
        return postHttpCall(apiUrl,requestBody);
    }

    public static String getDeviceList(String token) throws Exception {
        String apiUrl = baseUrl + "/openapi/getDeviceListByUser";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("curPage", 1);
        requestBody.put("size", 999);
        requestBody.put("token", token);
        return postHttpCall(apiUrl,requestBody);
    }

    public static SunGrowUserInfo loginAndGetUserInfo() throws Exception {
        try {
            String jsonResponse = login();
            logger.info("登录API响应: {}", jsonResponse);

            SunGrowUserInfo userInfo = SunGrowResponseParser.extractUserInfo(jsonResponse);
            logger.info("登录成功，用户信息: {}", userInfo);

            return userInfo;
        } catch (Exception e) {
            logger.error("登录失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String loginAndGetToken() throws Exception {
        try {
            SunGrowUserInfo userInfo = loginAndGetUserInfo();
            return userInfo.getToken();
        } catch (Exception e) {
            logger.error("获取token失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static List<PowerStation> getPowerStationsAndParse(String token) throws Exception {
        try {
            String jsonResponse = getPowerStationList(token);
            logger.info("电站列表API响应: {}", jsonResponse);

            List<PowerStation> powerStations = SunGrowResponseParser.extractPowerStations(jsonResponse);
            logger.info("成功解析到 {} 个电站信息", powerStations != null ? powerStations.size() : 0);

            return powerStations;
        } catch (Exception e) {
            logger.error("获取并解析电站信息失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static List<Device> getDevicesAndParse(String token) throws Exception {
        try {
            String jsonResponse = getDeviceList(token);
            logger.info("设备列表API响应: {}", jsonResponse);

            List<Device> devices = SunGrowResponseParser.extractDevices(jsonResponse);
            logger.info("成功解析到 {} 个设备信息", devices != null ? devices.size() : 0);

            return devices;
        } catch (Exception e) {
            logger.error("获取并解析设备信息失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static DeviceList getDeviceListAndParse(String token) throws Exception {
        try {
            String jsonResponse = getDeviceList(token);
            logger.info("设备列表API响应: {}", jsonResponse);

            DeviceList deviceList = SunGrowResponseParser.extractDeviceList(jsonResponse);
            logger.info("成功解析设备列表，总数: {}, 当前页: {}, 逆变器: {}, 通信模块: {}, 在线设备: {}, 故障设备: {}",
                deviceList.getRowCount(), deviceList.getPageSize(),
                deviceList.getInverterCount(), deviceList.getCommunicationModuleCount(),
                deviceList.getOnlineDeviceCount(), deviceList.getFaultyDeviceCount());

            return deviceList;
        } catch (Exception e) {
            logger.error("获取并解析设备列表失败: {}", e.getMessage(), e);
            throw e;
        }
    }

}
