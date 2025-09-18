package com.virtualpowerplant.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 统一的密钥配置管理器
 * 从 ~/secret.config 文件中读取所有敏感配置信息
 */
@Component
public class SecretConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(SecretConfigManager.class);
    private static final String SECRET_CONFIG_PATH = System.getProperty("user.home") + "/secret.config";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, Object> configCache = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    @PostConstruct
    private void init() {
        loadConfiguration();
    }

    /**
     * 加载配置文件
     */
    private static synchronized void loadConfiguration() {
        if (initialized) {
            return;
        }

        try {
            File configFile = new File(SECRET_CONFIG_PATH);
            if (!configFile.exists()) {
                logger.warn("Secret config file not found at: {}. Creating default template.", SECRET_CONFIG_PATH);
                createDefaultConfigFile();
                return;
            }

            JsonNode rootNode = objectMapper.readTree(configFile);

            // 清空缓存并重新加载
            configCache.clear();

            // 递归解析JSON配置到缓存
            parseJsonNode("", rootNode, configCache);

            initialized = true;
            logger.info("Successfully loaded secret configuration from: {}", SECRET_CONFIG_PATH);
            logger.debug("Loaded configuration keys: {}", configCache.keySet());

        } catch (IOException e) {
            logger.error("Failed to load secret configuration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load secret configuration", e);
        }
    }

    /**
     * 递归解析JSON节点
     */
    private static void parseJsonNode(String prefix, JsonNode node, Map<String, Object> cache) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                parseJsonNode(key, entry.getValue(), cache);
            });
        } else if (node.isArray()) {
            // 数组暂时不支持，可以根据需要扩展
            cache.put(prefix, node.toString());
        } else {
            // 叶子节点，存储值
            if (node.isTextual()) {
                cache.put(prefix, node.asText());
            } else if (node.isNumber()) {
                cache.put(prefix, node.asDouble());
            } else if (node.isBoolean()) {
                cache.put(prefix, node.asBoolean());
            } else {
                cache.put(prefix, node.toString());
            }
        }
    }

    /**
     * 创建默认配置文件模板
     */
    private static void createDefaultConfigFile() {
        String defaultConfig = "{\n" +
            "  \"weather\": {\n" +
            "    \"api_token\": \"your-weather-api-token-here\"\n" +
            "  },\n" +
            "  \"sungrow\": {\n" +
            "    \"username\": \"your-sungrow-username\",\n" +
            "    \"password\": \"your-sungrow-password\",\n" +
            "    \"app_key\": \"your-sungrow-app-key\",\n" +
            "    \"base_url\": \"https://gateway.isolarcloud.com.hk\",\n" +
            "    \"public_key\": \"your-sungrow-public-key\",\n" +
            "    \"access_key\": \"your-sungrow-access-key\",\n" +
            "    \"aes_key\": \"your-sungrow-aes-key\"\n" +
            "  },\n" +
            "  \"database\": {\n" +
            "    \"url\": \"jdbc:mysql://localhost:3306/vpp\",\n" +
            "    \"username\": \"your-db-username\",\n" +
            "    \"password\": \"your-db-password\"\n" +
            "  }\n" +
            "}";

        try {
            File configFile = new File(SECRET_CONFIG_PATH);
            java.nio.file.Files.write(configFile.toPath(), defaultConfig.getBytes());
            logger.info("Created default secret config template at: {}", SECRET_CONFIG_PATH);
            logger.info("Please update the configuration file with your actual credentials.");
        } catch (IOException e) {
            logger.error("Failed to create default config file: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取字符串配置值
     */
    public static String getString(String key) {
        ensureInitialized();
        Object value = configCache.get(key);
        if (value == null) {
            logger.warn("Configuration key not found: {}", key);
            return null;
        }
        return value.toString();
    }

    /**
     * 获取字符串配置值，如果不存在则返回默认值
     */
    public static String getString(String key, String defaultValue) {
        String value = getString(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取整数配置值
     */
    public static Integer getInteger(String key) {
        ensureInitialized();
        Object value = configCache.get(key);
        if (value == null) {
            logger.warn("Configuration key not found: {}", key);
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            logger.error("Cannot parse integer value for key {}: {}", key, value);
            return null;
        }
    }

    /**
     * 获取布尔配置值
     */
    public static Boolean getBoolean(String key) {
        ensureInitialized();
        Object value = configCache.get(key);
        if (value == null) {
            logger.warn("Configuration key not found: {}", key);
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * 检查配置键是否存在
     */
    public static boolean hasKey(String key) {
        ensureInitialized();
        return configCache.containsKey(key);
    }

    /**
     * 重新加载配置文件
     */
    public static synchronized void reload() {
        initialized = false;
        configCache.clear();
        loadConfiguration();
        logger.info("Secret configuration reloaded");
    }

    /**
     * 确保配置已初始化
     */
    private static void ensureInitialized() {
        if (!initialized) {
            loadConfiguration();
        }
    }

    /**
     * 获取所有配置键（用于调试）
     */
    public static java.util.Set<String> getAllKeys() {
        ensureInitialized();
        return configCache.keySet();
    }

    // 便捷方法：获取常用配置

    /**
     * 获取天气API Token
     */
    public static String getWeatherApiToken() {
        return getString("weather.api_token");
    }

    /**
     * 获取SunGrow用户名
     */
    public static String getSunGrowUsername() {
        return getString("sungrow.username");
    }

    /**
     * 获取SunGrow密码
     */
    public static String getSunGrowPassword() {
        return getString("sungrow.password");
    }

    /**
     * 获取SunGrow App Key
     */
    public static String getSunGrowAppKey() {
        return getString("sungrow.app_key");
    }

    /**
     * 获取SunGrow Base URL
     */
    public static String getSunGrowBaseUrl() {
        return getString("sungrow.base_url", "https://gateway.isolarcloud.com.hk");
    }

    /**
     * 获取数据库URL
     */
    public static String getDatabaseUrl() {
        return getString("database.url");
    }

    /**
     * 获取数据库用户名
     */
    public static String getDatabaseUsername() {
        return getString("database.username");
    }

    /**
     * 获取数据库密码
     */
    public static String getDatabasePassword() {
        return getString("database.password");
    }
}