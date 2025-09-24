package com.virtualpowerplant.model;

import java.time.LocalDateTime;

public class Device {

    private Long id;
    private Long uuid;
    private Long psId;
    private String psName;
    private Integer psType;
    private Integer onlineStatus;
    private String provinceName;
    private String cityName;
    private String districtName;
    private Integer connectType;
    private String deviceName;
    private String deviceSn;
    private Integer deviceType;
    private Integer deviceCode;
    private String typeName;
    private String deviceModelCode;
    private Long deviceModelId;
    private String factoryName;
    private Integer channelId;
    private String psKey;
    private String communicationDevSn;
    private String devStatus;
    private Integer devFaultStatus;
    private Integer relState;
    private String relTime;
    private String gridConnectionDate;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Device() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public Long getPsId() {
        return psId;
    }

    public void setPsId(Long psId) {
        this.psId = psId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(Integer deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDeviceModelCode() {
        return deviceModelCode;
    }

    public void setDeviceModelCode(String deviceModelCode) {
        this.deviceModelCode = deviceModelCode;
    }

    public Long getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(Long deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getPsKey() {
        return psKey;
    }

    public void setPsKey(String psKey) {
        this.psKey = psKey;
    }

    public String getCommunicationDevSn() {
        return communicationDevSn;
    }

    public void setCommunicationDevSn(String communicationDevSn) {
        this.communicationDevSn = communicationDevSn;
    }

    public String getDevStatus() {
        return devStatus;
    }

    public void setDevStatus(String devStatus) {
        this.devStatus = devStatus;
    }

    public Integer getDevFaultStatus() {
        return devFaultStatus;
    }

    public void setDevFaultStatus(Integer devFaultStatus) {
        this.devFaultStatus = devFaultStatus;
    }

    public Integer getRelState() {
        return relState;
    }

    public void setRelState(Integer relState) {
        this.relState = relState;
    }

    public String getRelTime() {
        return relTime;
    }

    public void setRelTime(String relTime) {
        this.relTime = relTime;
    }

    public String getGridConnectionDate() {
        return gridConnectionDate;
    }

    public void setGridConnectionDate(String gridConnectionDate) {
        this.gridConnectionDate = gridConnectionDate;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 便捷方法
    public boolean isOnline() {
        return "1".equals(devStatus);
    }

    public boolean isConnected() {
        return relState != null && relState == 1;
    }

    public boolean isInverter() {
        return deviceType != null && deviceType == 1;
    }

    public boolean isCommunicationModule() {
        return deviceType != null && deviceType == 22;
    }

    public boolean hasFault() {
        return devFaultStatus != null && devFaultStatus != 4; // 4表示正常
    }

    @Override
    public String toString() {
        return "Device{" +
                "uuid=" + uuid +
                ", psId=" + psId +
                ", deviceName='" + deviceName + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", deviceType=" + deviceType +
                ", typeName='" + typeName + '\'' +
                ", deviceModelCode='" + deviceModelCode + '\'' +
                ", factoryName='" + factoryName + '\'' +
                ", devStatus='" + devStatus + '\'' +
                ", devFaultStatus=" + devFaultStatus +
                ", relState=" + relState +
                '}';
    }

    public String getPsName() {
        return psName;
    }

    public void setPsName(String psName) {
        this.psName = psName;
    }

    public Integer getPsType() {
        return psType;
    }

    public void setPsType(Integer psType) {
        this.psType = psType;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Integer getConnectType() {
        return connectType;
    }

    public void setConnectType(Integer connectType) {
        this.connectType = connectType;
    }
}