package com.virtualpowerplant.controller;

import com.virtualpowerplant.service.DatasetMetaInfoService;
import com.virtualpowerplant.service.WeatherDataService;
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
public class DatasetMetaInfoController {
    @Autowired
    private DatasetMetaInfoService datasetMetaInfoService;

    @Autowired
    private WeatherDataService weatherDataService;

    @GetMapping("/dataset/getMetaVar")
    public List<String> selectMetaVarByMetaType(@RequestParam String metaType) {
        return datasetMetaInfoService.selectMetaVarByMetaType(metaType);
    }

    @GetMapping("/dataset/getMetaType")
    List<String> selectMetaTypeByDatasetType(@RequestParam String datasetType) {
        return datasetMetaInfoService.selectMetaTypeByDatasetType(datasetType);
    }

    @GetMapping("/dataset/collectWeatherData")
    void collectWeatherData(@RequestParam String time) {
        weatherDataService.collectWeatherData(time);
    }


    @GetMapping("/dataset/getWeatherByTimeAndLocation")
    void collectWeatherData(@RequestParam String time, @RequestParam String lat, @RequestParam String logi) {
        weatherDataService.collectWeatherData(time);
    }




}
