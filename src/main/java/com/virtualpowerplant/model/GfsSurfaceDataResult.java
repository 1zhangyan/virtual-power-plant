package com.virtualpowerplant.model;

import java.util.List;
import java.util.Map;

public class GfsSurfaceDataResult {
    private List<Double> location;
    private List<String> timestamps;
    private Map<String, List<Double>> metricValues;
    private List<String> metricVars;
    private List<String> metricUnits;
    private String timeFcst;

    public GfsSurfaceDataResult() {}

    public GfsSurfaceDataResult(List<Double> location, List<String> timestamps,
                                Map<String, List<Double>> metricValues, List<String> metricVars,
                                List<String> metricUnits, String timeFcst) {
        this.location = location;
        this.timestamps = timestamps;
        this.metricValues = metricValues;
        this.metricVars = metricVars;
        this.metricUnits = metricUnits;
        this.timeFcst = timeFcst;
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

    public Map<String, List<Double>> getMetricValues() {
        return metricValues;
    }

    public void setMetricValues(Map<String, List<Double>> metricValues) {
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

    @Override
    public String toString() {
        return String.format("WeatherData{location=%s, timestamps=%s, metrics=%s, units=%s, forecast_time='%s'}",
                location, timestamps, metricValues, metricUnits, timeFcst);
    }
}