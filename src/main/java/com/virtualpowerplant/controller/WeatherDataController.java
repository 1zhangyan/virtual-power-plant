package com.virtualpowerplant.controller;

import com.virtualpowerplant.model.SimpleWeatherForestData;
import com.virtualpowerplant.service.DatasetMetaInfoService;
import com.virtualpowerplant.service.WeatherDataCollectService;
import com.virtualpowerplant.service.WeatherDataLindormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "天气接口管理", description = "天气接口管理")
public class WeatherDataController {
    @Autowired
    private DatasetMetaInfoService datasetMetaInfoService;

    @Autowired
    private WeatherDataLindormService weatherDataLindormService;

    @Autowired
    private WeatherDataCollectService weatherDataCollectService;

    @GetMapping("/dataset/getMetaVar")
    public List<String> selectMetaVarByMetaType(@RequestParam String metaType) {
        return datasetMetaInfoService.selectMetaVarByMetaType(metaType);
    }

    @GetMapping("/dataset/getMetaType")
    List<String> selectMetaTypeByDatasetType(@RequestParam String datasetType) {
        return datasetMetaInfoService.selectMetaTypeByDatasetType(datasetType);
    }

    @GetMapping("/collectWeatherData")
    @Operation(summary = "触发一次补开始时间后半个月的天气预测数据", description = "触发一次补开始时间后半个月的天气预测数据")
    void collectWeatherData(@RequestParam String time) {
        weatherDataCollectService.collectWeatherData(time);
    }


    @GetMapping("/selectWeatherData")
    @Operation(summary = "获取指定经纬度，小时范围，天气预报数据", description = "获取指定经纬度，小时范围，天气预报数据")
    List<SimpleWeatherForestData> selectWeatherData(@RequestParam Double longitude, @RequestParam Double latitude, @RequestParam Long timeStamp) {
       return weatherDataLindormService.selectWeatherData(longitude,latitude,timeStamp);
    }


}
