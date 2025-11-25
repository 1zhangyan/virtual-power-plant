package com.virtualpowerplant.controller;

import com.virtualpowerplant.service.SungrowDeviceService;
import com.virtualpowerplant.service.WeatherDataCollectService;
import com.virtualpowerplant.service.WeatherDataLindormService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "admin", description = "admin")
public class AdminController {
    @Autowired
    private SungrowDeviceService sungrowDeviceService;

    @Autowired
    private WeatherDataLindormService weatherDataLindormService;

    @Autowired
    private WeatherDataCollectService weatherDataCollectService;

    @PostMapping("/syncDevicesWithCoordinates")
    public void syncDevicesWithCoordinates() {
        sungrowDeviceService.syncDevicesWithCoordinates();
    }

    @PostMapping("/collectWeatherData")
    public void collectWeatherData() {
        weatherDataCollectService.collectWeatherData("2025-11-25 00:00:00", "2025-11-26 00:00:00");
    }

    @PostMapping("/initWeatherTable")
    public void initWeatherTable() {
        weatherDataLindormService.initWeatherTable();
    }

}
