package com.virtualpowerplant.service;


import static com.virtualpowerplant.constant.Constant.objectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.virtualpowerplant.constant.Constant;
import com.virtualpowerplant.utils.AESEncryptUtils;
import com.virtualpowerplant.utils.RSAEncryptUtils;

public class SunGrowDataService {
    public static String login() throws Exception{
        String appKey = "CCAA8A077B013FA889940AA05B4AE421";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvY7PCF1il4_9nMbTjpqbOTZfuu_YnsSzhFLCIFWgcspm7gTjQDlETwoU8J3EDbeckGm9u9mWwxUOi1nIgC-_F-nix6g1efunwcBc5QOdlZ4Mje93DfaJLhdwJaCjqW8ALAyZX83PTyf0kjQRA7fYoLkrCLlD7ypfp48aedp3kkQIDAQAB";
        String accessKey = "dgf9hud76au85j4wc8kcjn76z9enfcrm";
        String AESKey = "A123456zA123456z";
        String userAccount = "13883358710";
        String userPassword = "@jn888888";
        String xRandomSecretKey = RSAEncryptUtils.publicEncrypt(AESKey, publicKey);
        String xAccessKey = accessKey;
        String apiUrl = "https://gateway.isolarcloud.com/openapi/login";
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
