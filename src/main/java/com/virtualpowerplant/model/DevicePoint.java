package com.virtualpowerplant.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DevicePoint {

    @JsonProperty("device_sn")
    private String deviceSn;

    @JsonProperty("ps_key")
    private String psKey;

    @JsonProperty("device_name")
    private String deviceName;

    @JsonProperty("device_time")
    private String deviceTime;

    @JsonProperty("p24")
    private String p24;

    @JsonProperty("dev_status")
    private Integer devStatus;


    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getPsKey() {
        return psKey;
    }

    public void setPsKey(String psKey) {
        this.psKey = psKey;
    }


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(String deviceTime) {
        this.deviceTime = deviceTime;
    }

    public String getP24() {
        return p24;
    }

    public void setP24(String p24) {
        this.p24 = p24;
    }

    public Integer getDevStatus() {
        return devStatus;
    }

    public void setDevStatus(Integer devStatus) {
        this.devStatus = devStatus;
    }


}