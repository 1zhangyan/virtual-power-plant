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

    /*TODO:
    login 返回的结果为，写一段代码解析这段json，result_data、result_msg、result_code 的解析需要抽出公共代码，用来解析其他类似请求，login 的结果需要返回一个描述用户信息的类
    {
	"req_serial_num":"20250918d84e4bb3b1cee0fabba56c78",
	"result_code":"1",
	"result_msg":"success",
	"result_data":{
		"user_master_org_id":"1104432",
		"mobile_tel":"13883358710",
		"user_name":"基能能源—内蒙",
		"language":"Chinese",
		"token":"956680_7054b43ad9db4f81ac8134502076f02b",
		"err_times":"0",
		"user_id":"956680",
		"login_state":"1",
		"disable_time":null,
		"country_name":"中国",
		"user_account":"jc666",
		"user_master_org_name":"品贡",
		"email":null,
		"country_id":"1"
	}
}
    *
    * */


    public static String getPowerStationList() throws Exception {
        String apiUrl = baseUrl + "/openapi/getPowerStationList";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("curPage", 1);
        requestBody.put("size", 9999);
        requestBody.put("token", "956680_4fcd6fbb3e114b7faef29e54a3787947");
        return postHttpCall(apiUrl,requestBody);
    }

    public static void main(String[] args) throws Exception {
//        System.out.println(getPowerStationList());
        System.out.println(login());
    }

}
