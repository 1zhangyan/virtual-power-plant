package com.virtualpowerplant.controller;

import com.virtualpowerplant.service.InverterDataCollectService;
import com.virtualpowerplant.service.SungrowDeviceService;
import com.virtualpowerplant.service.WeatherDataLindormService;
import com.virtualpowerplant.service.WeatherDataService;
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
    private InverterDataCollectService inverterDataCollectService;

    @Autowired
    private WeatherDataService weatherDataService;

    @PostMapping("/syncDevicesWithCoordinates")
    public void syncDevicesWithCoordinates() {
        sungrowDeviceService.syncDevicesWithCoordinates();
    }

    @PostMapping("/collectInverterRealTimeData")
    public void collectInverterRealTimeData() {
        inverterDataCollectService.collectInverterRealTimeData(1L);
    }

    @PostMapping("/getWeatherDataBy")
    public void selectWeatherData() {
        weatherDataService.collectWeatherData(1L);
    }

}
