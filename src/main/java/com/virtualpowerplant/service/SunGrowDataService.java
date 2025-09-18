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
import com.virtualpowerplant.utils.AESEncryptUtils;
import com.virtualpowerplant.utils.RSAEncryptUtils;

@Service
public class SunGrowDataService {

    private static final Logger logger = LoggerFactory.getLogger(SunGrowDataService.class);

    public static String login() throws Exception{
        String appKey = SecretConfigManager.getSunGrowAppKey();
        String publicKey = SecretConfigManager.getString("sungrow.public_key");
        String accessKey = SecretConfigManager.getString("sungrow.access_key");
        String AESKey = SecretConfigManager.getString("sungrow.aes_key");
        String userAccount = SecretConfigManager.getSunGrowUsername();
        String userPassword = SecretConfigManager.getSunGrowPassword();

        if (appKey == null || publicKey == null || accessKey == null ||
            AESKey == null || userAccount == null || userPassword == null) {
            logger.error("SunGrow配置信息不完整，请检查secret.config文件");
            throw new RuntimeException("SunGrow配置信息不完整");
        }
        String xRandomSecretKey = RSAEncryptUtils.publicEncrypt(AESKey, publicKey);
        String xAccessKey = accessKey;
        String baseUrl = SecretConfigManager.getSunGrowBaseUrl();
        String apiUrl = baseUrl + "/openapi/login";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("x-random-secret-key",xRandomSecretKey );
        headers.set("x-access-key", xAccessKey);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("appkey", appKey);
        requestBody.put("user_account", userAccount);
        requestBody.put("user_password", userPassword);

        Map <String, Object> apiKeyParam = new HashMap<>();
        apiKeyParam.put("nonce", UUID.randomUUID().toString().replaceAll("-", ""));
        apiKeyParam.put("timestamp", System.currentTimeMillis());
        requestBody.put("api_key_param", apiKeyParam);

        String encryptedRequestBody = AESEncryptUtils.encrypt(objectMapper.writeValueAsString(requestBody), "A123456zA123456z") ;

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
        String decryptedResponseBody = AESEncryptUtils.decrypt(responseBody, AESKey) ;
        return decryptedResponseBody;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(login());

    }

}
