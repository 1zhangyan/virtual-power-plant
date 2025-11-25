package com.virtualpowerplant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class InverterRealTimeData {


    private String psName;

    private String psKey;

    private Double latitude;

    private Double longitude;

    private String inverterSn;

    private Double activePower;

    private LocalDateTime deviceTime;

    private LocalDateTime createTime;

    public InverterRealTimeData() {
        this.createTime = LocalDateTime.now();
    }


    public String getPsName() {
        return psName;
    }

    public void setPsName(String psName) {
        this.psName = psName;
    }


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

    public String getInverterSn() {
        return inverterSn;
    }

    public void setInverterSn(String inverterSn) {
        this.inverterSn = inverterSn;
    }

    public Double getActivePower() {
        return activePower;
    }

    public void setActivePower(Double activePower) {
        this.activePower = activePower;
    }

    public LocalDateTime getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(LocalDateTime deviceTime) {
        this.deviceTime = deviceTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getPsKey() {
        return psKey;
    }

    public void setPsKey(String psKey) {
        this.psKey = psKey;
    }
}