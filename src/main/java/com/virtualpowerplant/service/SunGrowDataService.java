package com.virtualpowerplant.service;

import static com.virtualpowerplant.constant.Constant.objectMapper;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import com.virtualpowerplant.model.InverterRealTimeData;
import com.virtualpowerplant.utils.AESEncryptUtils;
import com.virtualpowerplant.utils.RSAEncryptUtils;
import com.virtualpowerplant.utils.SunGrowResponseParser;

@Service
public class SunGrowDataService {

    private static final Logger logger = LoggerFactory.getLogger(SunGrowDataService.class);

    // Token缓存，3小时过期
    private static final Cache<String, String> tokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.HOURS)
            .maximumSize(1)
            .build();

    private static final String TOKEN_CACHE_KEY = "sungrow_token";

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

    public static String getPowerStationList(int page, int size) throws Exception {
        String apiUrl = baseUrl + "/openapi/getPowerStationList";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("curPage", page);
        requestBody.put("size", size);
        requestBody.put("token", getCachedToken());
        return postHttpCall(apiUrl,requestBody);
    }

    public static String getRealTimeData(List<String> snList) throws Exception {
        String apiUrl = baseUrl + "/openapi/getPVInverterRealTimeData";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("sn_list", snList);
        requestBody.put("token", getCachedToken());
        return postHttpCall(apiUrl,requestBody);
    }

    public static String getDeviceList(int page, int size) throws Exception {
        String apiUrl = baseUrl + "/openapi/getDeviceListByUser";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("curPage", page);
        requestBody.put("size", size);
        requestBody.put("token", getCachedToken());
        return postHttpCall(apiUrl,requestBody);
    }

    public static String openDeviceRealtimeUpdate(List<String> snList, Integer second) throws Exception {
        String apiUrl = baseUrl + "/openapi/datasubscribe/start";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("second", second);
        requestBody.put("sn_list", snList);
        requestBody.put("token", getCachedToken());
        return postHttpCall(apiUrl,requestBody);
    }

    public static SunGrowUserInfo loginAndGetUserInfo() throws Exception {
        try {
            String jsonResponse = login();
//            logger.info("登录API响应: {}", jsonResponse);
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

    /**
     * 获取缓存的token，如果缓存过期则重新登录获取
     */
    public static String getCachedToken() throws Exception {
        try {
            // 尝试从缓存获取token
            String cachedToken = tokenCache.getIfPresent(TOKEN_CACHE_KEY);

            if (cachedToken != null) {
                logger.debug("使用缓存的token");
                return cachedToken;
            }

            // 缓存中没有token，重新登录获取
            logger.info("Token缓存已过期，重新登录获取token...");
            SunGrowUserInfo userInfo = loginAndGetUserInfo();
            String newToken = userInfo.getToken();

            // 将新token放入缓存
            tokenCache.put(TOKEN_CACHE_KEY, newToken);
            logger.info("新token已缓存，有效期3小时");

            return newToken;
        } catch (Exception e) {
            logger.error("获取缓存token失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 清除token缓存（用于强制重新登录）
     */
    public static void clearTokenCache() {
        tokenCache.invalidateAll();
        logger.info("Token缓存已清除");
    }

    public static List<PowerStation> getPowerStationsAndParse() throws Exception {
        try {
            List<PowerStation> allPowerStations = new ArrayList<>();
            int page = 1;
            int pageSize = 200; // API每页最大200条

            while (true) {
                logger.info("获取电站列表第 {} 页，每页 {} 条", page, pageSize);
                String jsonResponse = getPowerStationList(page, pageSize);
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

    public static List<Device> getDevicesAndParse() throws Exception {
        try {
            List<Device> allDevices = new ArrayList<>();
            int page = 1;
            int pageSize = 200; // API每页最大200条

            while (true) {
                logger.info("获取设备列表第 {} 页，每页 {} 条", page, pageSize);
                String jsonResponse = getDeviceList(page, pageSize);
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

    public static List<InverterRealTimeData> getRealTimeDataAndParse(List<String> snList) throws Exception {
        try {
            if (snList == null || snList.isEmpty()) {
                logger.warn("设备序列号列表为空，无法获取实时数据");
                return new ArrayList<>();
            }

            logger.debug("获取设备实时数据，设备数量: {}", snList.size());
            String jsonResponse = getRealTimeData(snList);
            logger.debug("实时数据API响应: {}", jsonResponse);

            List<InverterRealTimeData> realTimeDataList = SunGrowResponseParser.extractRealTimeData(jsonResponse);
            if (realTimeDataList != null) {
                logger.info("成功解析到 {} 条实时数据", realTimeDataList.size());
            } else {
                logger.warn("未获取到任何实时数据");
                realTimeDataList = new ArrayList<>();
            }

            return realTimeDataList;
        } catch (Exception e) {
            logger.error("获取并解析实时数据失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static List<InverterRealTimeData> getRealTimeDataAndParse(List<String> snList, List<Device> devices) throws Exception {
        try {
            if (snList == null || snList.isEmpty()) {
                logger.warn("设备序列号列表为空，无法获取实时数据");
                return new ArrayList<>();
            }

            logger.debug("获取设备实时数据，设备数量: {}", snList.size());
            String jsonResponse = getRealTimeData(snList);
            logger.debug("实时数据API响应: {}", jsonResponse);

            List<InverterRealTimeData> realTimeDataList = SunGrowResponseParser.extractRealTimeDataWithDeviceInfo(jsonResponse, devices);
            if (realTimeDataList != null) {
                logger.info("成功解析到 {} 条实时数据", realTimeDataList.size());
            } else {
                logger.warn("未获取到任何实时数据");
                realTimeDataList = new ArrayList<>();
            }

            return realTimeDataList;
        } catch (Exception e) {
            logger.error("获取并解析实时数据失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
       System.out.println(getRealTimeData(Collections.singletonList("A2552608698")));
    }
}
