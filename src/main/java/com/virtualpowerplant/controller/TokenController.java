package com.virtualpowerplant.controller;

import com.virtualpowerplant.service.SunGrowDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/token")
@Tag(name = "Token管理", description = "SunGrow API Token缓存管理")
public class TokenController {

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @GetMapping("/cached")
    @Operation(summary = "获取缓存的Token", description = "获取缓存的Token，如果缓存过期会自动重新登录")
    public ResponseEntity<Map<String, Object>> getCachedToken() {
        try {
            long startTime = System.currentTimeMillis();
            String token = SunGrowDataService.getCachedToken();
            long endTime = System.currentTimeMillis();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token_preview", token.substring(0, Math.min(20, token.length())) + "...");
            response.put("full_token_length", token.length());
            response.put("fetch_time_ms", endTime - startTime);
            response.put("message", "Token获取成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取缓存Token失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "清除缓存并重新登录获取新Token")
    public ResponseEntity<Map<String, Object>> refreshToken() {
        try {
            // 清除缓存
            SunGrowDataService.clearTokenCache();
            logger.info("Token缓存已清除");

            // 重新获取Token
            long startTime = System.currentTimeMillis();
            String newToken = SunGrowDataService.getCachedToken();
            long endTime = System.currentTimeMillis();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token_preview", newToken.substring(0, Math.min(20, newToken.length())) + "...");
            response.put("full_token_length", newToken.length());
            response.put("refresh_time_ms", endTime - startTime);
            response.put("message", "Token刷新成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("刷新Token失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/cache")
    @Operation(summary = "清除Token缓存", description = "清除Token缓存，下次API调用时会重新登录")
    public ResponseEntity<Map<String, Object>> clearTokenCache() {
        try {
            SunGrowDataService.clearTokenCache();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token缓存已清除");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("清除Token缓存失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/status")
    @Operation(summary = "获取Token缓存状态", description = "获取Token缓存的状态信息")
    public ResponseEntity<Map<String, Object>> getTokenCacheStatus() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cache_expiration_hours", 3);
            response.put("cache_description", "Token在3小时后自动过期，过期后会自动重新登录获取新Token");
            response.put("usage_tip", "使用无参数的API方法（如getPowerStationsAndParse()）会自动使用缓存Token");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取Token缓存状态失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}