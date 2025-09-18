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
import java.util.ArrayList;

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

    public static String getPowerStationList(String token, int page, int size) throws Exception {
        String apiUrl = baseUrl + "/openapi/getPowerStationList";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("curPage", page);
        requestBody.put("size", size);
        requestBody.put("token", token);
        return postHttpCall(apiUrl,requestBody);
    }

    public static String getDeviceList(String token, int page, int size) throws Exception {
        String apiUrl = baseUrl + "/openapi/getDeviceListByUser";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("curPage", page);
        requestBody.put("size", size);
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
            List<PowerStation> allPowerStations = new ArrayList<>();
            int page = 1;
            int pageSize = 200; // API每页最大200条

            while (true) {
                logger.info("获取电站列表第 {} 页，每页 {} 条", page, pageSize);
                String jsonResponse = getPowerStationList(token, page, pageSize);
                logger.debug("电站列表API响应 (第{}页): {}", page, jsonResponse);

                List<PowerStation> powerStations = SunGrowResponseParser.extractPowerStations(jsonResponse);
                if (powerStations == null || powerStations.isEmpty()) {
                    logger.info("第 {} 页没有更多电站数据，停止分页", page);
                    break;
                }

                allPowerStations.addAll(powerStations);
                logger.info("第 {} 页获取到 {} 个电站，累计 {} 个", page, powerStations.size(), allPowerStations.size());

                // 如果返回的数据少于页大小，说明这是最后一页
                if (powerStations.size() < pageSize) {
                    logger.info("第 {} 页数据量 {} 小于页大小 {}，已获取完所有数据", page, powerStations.size(), pageSize);
                    break;
                }

                page++;
            }

            logger.info("成功解析到总共 {} 个电站信息", allPowerStations.size());
            return allPowerStations;
        } catch (Exception e) {
            logger.error("获取并解析电站信息失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static List<Device> getDevicesAndParse(String token) throws Exception {
        try {
            List<Device> allDevices = new ArrayList<>();
            int page = 1;
            int pageSize = 200; // API每页最大200条

            while (true) {
                logger.info("获取设备列表第 {} 页，每页 {} 条", page, pageSize);
                String jsonResponse = getDeviceList(token, page, pageSize);
                logger.debug("设备列表API响应 (第{}页): {}", page, jsonResponse);

                List<Device> devices = SunGrowResponseParser.extractDevices(jsonResponse);
                if (devices == null || devices.isEmpty()) {
                    logger.info("第 {} 页没有更多设备数据，停止分页", page);
                    break;
                }

                allDevices.addAll(devices);
                logger.info("第 {} 页获取到 {} 个设备，累计 {} 个", page, devices.size(), allDevices.size());

                // 如果返回的数据少于页大小，说明这是最后一页
                if (devices.size() < pageSize) {
                    logger.info("第 {} 页数据量 {} 小于页大小 {}，已获取完所有数据", page, devices.size(), pageSize);
                    break;
                }

                page++;
            }

            logger.info("成功解析到总共 {} 个设备信息", allDevices.size());
            return allDevices;
        } catch (Exception e) {
            logger.error("获取并解析设备信息失败: {}", e.getMessage(), e);
            throw e;
        }
    }


}
