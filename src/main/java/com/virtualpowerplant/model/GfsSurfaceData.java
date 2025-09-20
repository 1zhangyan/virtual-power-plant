package com.virtualpowerplant.model;

import java.time.LocalDateTime;

/**
 * 气象数据模型，用于存储解析后的天气预报数据
 * 对应 GfsSurfaceDataService.fetchWeatherData 的结果
 */
public class GfsSurfaceData {

    // 位置信息
    private Double latitude;  // 纬度
    private Double longitude; // 经度

    // 时间信息
    private LocalDateTime forecastTime; // 预报时间戳

    // 气象指标 (来自 mete_var: ["tcc","lcc","mcc","hcc","dswrf","dlwrf","uswrf","ulwrf"])
    private Double tcc;    // Total Cloud Coverage (总云量) - %
    private Double lcc;    // Low Cloud Coverage (低云量) - %
    private Double mcc;    // Middle Cloud Coverage (中云量) - %
    private Double hcc;    // High Cloud Coverage (高云量) - %
    private Double dswrf;  // Downward Short Wave Radiation Flux (向下短波辐射通量) - W/m^2
    private Double dlwrf;  // Downward Long Wave Radiation Flux (向下长波辐射通量) - W/m^2
    private Double uswrf;  // Upward Short Wave Radiation Flux (向上短波辐射通量) - W/m^2
    private Double ulwrf;  // Upward Long Wave Radiation Flux (向上长波辐射通量) - W/m^2

    // 构造函数
    public GfsSurfaceData() {}

    public GfsSurfaceData(Double latitude, Double longitude, LocalDateTime forecastTime,
                          Double tcc, Double lcc, Double mcc, Double hcc,
                          Double dswrf, Double dlwrf, Double uswrf, Double ulwrf) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.forecastTime = forecastTime;
        this.tcc = tcc;
        this.lcc = lcc;
        this.mcc = mcc;
        this.hcc = hcc;
        this.dswrf = dswrf;
        this.dlwrf = dlwrf;
        this.uswrf = uswrf;
        this.ulwrf = ulwrf;
    }

    // Getters and Setters
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getForecastTime() {
        return forecastTime;
    }

    public void setForecastTime(LocalDateTime forecastTime) {
        this.forecastTime = forecastTime;
    }

    public Double getTcc() {
        return tcc;
    }

    public void setTcc(Double tcc) {
        this.tcc = tcc;
    }

    public Double getLcc() {
        return lcc;
    }

    public void setLcc(Double lcc) {
        this.lcc = lcc;
    }

    public Double getMcc() {
        return mcc;
    }

    public void setMcc(Double mcc) {
        this.mcc = mcc;
    }

    public Double getHcc() {
        return hcc;
    }

    public void setHcc(Double hcc) {
        this.hcc = hcc;
    }

    public Double getDswrf() {
        return dswrf;
    }

    public void setDswrf(Double dswrf) {
        this.dswrf = dswrf;
    }

    public Double getDlwrf() {
        return dlwrf;
    }

    public void setDlwrf(Double dlwrf) {
        this.dlwrf = dlwrf;
    }

    public Double getUswrf() {
        return uswrf;
    }

    public void setUswrf(Double uswrf) {
        this.uswrf = uswrf;
    }

    public Double getUlwrf() {
        return ulwrf;
    }

    public void setUlwrf(Double ulwrf) {
        this.ulwrf = ulwrf;
    }

    @Override
    public String toString() {
        return String.format("WeatherData{lat=%.4f, lng=%.4f, time=%s, tcc=%.1f%%, lcc=%.1f%%, mcc=%.1f%%, hcc=%.1f%%, dswrf=%.1fW/m², dlwrf=%.1fW/m², uswrf=%.1fW/m², ulwrf=%.1fW/m²}",
                latitude, longitude, forecastTime, tcc, lcc, mcc, hcc, dswrf, dlwrf, uswrf, ulwrf);
    }
}