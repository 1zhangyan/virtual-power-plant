package com.virtualpowerplant.model;

public class SungrowDevicePoint {

    private String deviceSn;
    private String psKey;
    private String deviceName;
    private String deviceTime;
    private String p24;
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