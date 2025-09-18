package com.virtualpowerplant.controller;

import com.virtualpowerplant.model.WeatherDataResult;
import com.virtualpowerplant.service.WeatherDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "Weather", description = "Weather Data API")
public class WeatherController {

    @Autowired
    private WeatherDataService weatherDataService;

    @GetMapping("/trigger")
    @Operation(
            summary = "手动触发定时任务",
            description = "立即执行一次定时气象数据获取任务"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功触发定时任务")
    })
    public String triggerScheduledTask() {
        try {
            weatherDataService.scheduledWeatherDataFetch();
            return "Scheduled weather data fetch triggered successfully! Check console logs for results.";
        } catch (Exception e) {
            return "Failed to trigger scheduled task: " + e.getMessage();
        }
    }

    @GetMapping("/fetch")
    @Operation(
            summary = "获取指定位置气象数据",
            description = "根据经纬度、数据类型和指标列表获取气象数据"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取气象数据")
    })
    public WeatherDataResult fetchWeatherData(
            @Parameter(description = "经度", example = "103.1693835")
            @RequestParam(defaultValue = "103.1693835") double longitude,

            @Parameter(description = "纬度", example = "30.5398753")
            @RequestParam(defaultValue = "30.5398753") double latitude,

            @Parameter(description = "数据类型", example = "gfs_surface")
            @RequestParam(defaultValue = "gfs_surface") String dataType,

            @Parameter(description = "指标列表，用逗号分隔", example = "t2m,d2m")
            @RequestParam(defaultValue = "t2m") String metaVars
    ) {
        try {
            String[] vars = metaVars.split(",");
            return weatherDataService.fetchWeatherData(longitude, latitude, dataType, Arrays.asList(vars),  "2025-01-15 00:00:00");
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch weather data: " , e);
        }
    }
}