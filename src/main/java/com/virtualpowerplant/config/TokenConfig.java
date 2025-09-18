package com.virtualpowerplant.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class TokenConfig {

    private static final String TOKEN_FILE_PATH = System.getProperty("user.home") + "/token.txt";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getWeatherToken() {
        try {
            File tokenFile = new File(TOKEN_FILE_PATH);
            if (!tokenFile.exists()) {
                throw new RuntimeException("Token file not found at: " + TOKEN_FILE_PATH);
            }

            JsonNode jsonNode = objectMapper.readTree(tokenFile);
            String token = jsonNode.get("weather_forecast").asText();

            if (token == null || token.isEmpty()) {
                throw new RuntimeException("Weather forecast token not found in token file");
            }

            return token;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read token file: " + e.getMessage(), e);
        }
    }
}