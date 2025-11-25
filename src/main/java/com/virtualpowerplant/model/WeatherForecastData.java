package com.virtualpowerplant.model;

import java.time.LocalDateTime;

/**
 * 逆变器天气数据合并模型
 * 包含设备信息和对应位置的天气预报数据
 */
public class WeatherForecastData {

    // 设备信息
    private String psName;      // 电站名称
    private String psKey;       // 电站标识
    private String deviceSn;    // 设备序列号
    private LocalDateTime time; // 时间戳

    // 天气预报数据 (对应 mete_var: ["tcc","lcc","mcc","hcc","dswrf","dlwrf","uswrf","ulwrf"])
    private Double tcc;    // Total Cloud Coverage (总云量) - %
    private Double lcc;    // Low Cloud Coverage (低云量) - %
    private Double mcc;    // Middle Cloud Coverage (中云量) - %
    private Double hcc;    // High Cloud Coverage (高云量) - %
    private Double dswrf;  // Downward Short Wave Radiation Flux (向下短波辐射通量) - W/m^2
    private Double dlwrf;  // Downward Long Wave Radiation Flux (向下长波辐射通量) - W/m^2
    private Double uswrf;  // Upward Short Wave Radiation Flux (向上短波辐射通量) - W/m^2
    private Double ulwrf;  // Upward Long Wave Radiation Flux (向上长波辐射通量) - W/m^2

    // 构造函数
    public WeatherForecastData() {}

    public WeatherForecastData(String psName, String psKey, String deviceSn, LocalDateTime timestamp,
                               Double tcc, Double lcc, Double mcc, Double hcc,
                               Double dswrf, Double dlwrf, Double uswrf, Double ulwrf) {
        this.psName = psName;
        this.psKey = psKey;
        this.deviceSn = deviceSn;
        this.time = timestamp;
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
    public String getPsName() {
        return psName;
    }

    public void setPsName(String psName) {
        this.psName = psName;
    }

    public String getPsKey() {
        return psKey;
    }

    public void setPsKey(String psKey) {
        this.psKey = psKey;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime timestamp) {
        this.time = timestamp;
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
        return String.format("WeatherForecastData{ps='%s'/%s, device='%s', time=%s, tcc=%.1f%%, dswrf=%.1fW/m²}",
                psName, psKey, deviceSn, time, tcc, dswrf);
    }
}