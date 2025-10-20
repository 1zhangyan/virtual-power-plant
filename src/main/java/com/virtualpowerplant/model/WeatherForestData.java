package com.virtualpowerplant.model;

import java.util.List;
import java.util.Map;

/**
 * 气象数据模型，用于存储解析后的天气预报数据
 * 对应 GfsSurfaceDataService.fetchWeatherData 的结果
 */
public class WeatherForestData {

    private List<Double> location;
    private List<String> timestamps;
    private List<List<Double>> metricValues;
    private List<String> metricVars;
    private List<String> metricUnits;
    private String timeFcst;
    private String metaType;

    public WeatherForestData(List<Double> location, List<String> timestamps,
                                List<List<Double>> metricValues, List<String> metricVars,
                                List<String> metricUnits, String timeFcst, String metaType) {
        this.location = location;
        this.timestamps = timestamps;
        this.metricValues = metricValues;
        this.metricVars = metricVars;
        this.metricUnits = metricUnits;
        this.timeFcst = timeFcst;
        this.metaType = metaType;
    }

    // Getters and Setters
    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public List<String> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<String> timestamps) {
        this.timestamps = timestamps;
    }

    public List<List<Double>> getMetricValues() {
        return metricValues;
    }

    public void setMetricValues(List<List<Double>> metricValues) {
        this.metricValues = metricValues;
    }

    public List<String> getMetricVars() {
        return metricVars;
    }

    public void setMetricVars(List<String> metricVars) {
        this.metricVars = metricVars;
    }

    public List<String> getMetricUnits() {
        return metricUnits;
    }

    public void setMetricUnits(List<String> metricUnits) {
        this.metricUnits = metricUnits;
    }

    public String getTimeFcst() {
        return timeFcst;
    }

    public void setTimeFcst(String timeFcst) {
        this.timeFcst = timeFcst;
    }

    public String getMetaType() {
        return metaType;
    }

    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }
}