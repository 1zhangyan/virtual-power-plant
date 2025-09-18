package com.virtualpowerplant.config;

import org.springframework.stereotype.Component;

@Component
public class TokenConfig {

    public static String getWeatherToken() {
        return SecretConfigManager.getWeatherApiToken();
    }
}