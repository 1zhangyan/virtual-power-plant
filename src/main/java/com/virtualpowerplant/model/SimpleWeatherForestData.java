package com.virtualpowerplant.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 气象数据模型，用于存储解析后的天气预报数据
 */
public class SimpleWeatherForestData {

    private String timestamp;
    private List<Double> metricValues;
    private List<String> metricVars;
    private String metaType;

    public SimpleWeatherForestData(String timestamp,String metaType){
        metricValues = new ArrayList<>();
        metricVars = new ArrayList<>();
        this.timestamp = timestamp;
        this.metaType = metaType;
    }

    public SimpleWeatherForestData(){

    }


    public String getMetaType() {
        return metaType;
    }

    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Double> getMetricValues() {
        return metricValues;
    }

    public void setMetricValues(List<Double> metricValues) {
        this.metricValues = metricValues;
    }

    public List<String> getMetricVars() {
        return metricVars;
    }

    public void setMetricVars(List<String> metricVars) {
        this.metricVars = metricVars;
    }
}