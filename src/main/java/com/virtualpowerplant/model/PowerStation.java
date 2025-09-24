package com.virtualpowerplant.model;

public class PowerStation {

    private Long psId;
    private String psName;
    private String psLocation;
    private String provinceName;
    private String cityName;
    private String districtName;
    private Double latitude;
    private Double longitude;

    private PowerStationValue totalEnergy;
    private PowerStationValue todayEnergy;
    private PowerStationValue currPower;
    private PowerStationValue totalIncome;
    private PowerStationValue todayIncome;
    private PowerStationValue monthIncome;
    private PowerStationValue yearIncome;
    private PowerStationValue totalCapacity;
    private PowerStationValue co2Reduce;
    private PowerStationValue co2ReduceTotal;
    private PowerStationValue equivalentHour;

    private Integer psStatus;
    private Integer psFaultStatus;
    private Integer gridConnectionStatus;
    private Integer buildStatus;
    private Integer psType;
    private Integer connectType;
    private Integer validFlag;
    private Integer alarmCount;
    private Integer faultCount;
    private String installDate;
    private Long gridConnectionTime;
    private String shareType;
    private String psCurrentTimeZone;
    private String description;

    private String totalEnergyUpdateTime;
    private String todayEnergyUpdateTime;
    private String currPowerUpdateTime;
    private String totalIncomeUpdateTime;
    private String todayIncomeUpdateTime;
    private String monthIncomeUpdateTime;
    private String yearIncomeUpdateTime;
    private String totalCapacityUpdateTime;
    private String co2ReduceUpdateTime;
    private String co2ReduceTotalUpdateTime;
    private String equivalentHourUpdateTime;

    public PowerStation() {}

    // Getters and Setters
    public Long getPsId() {
        return psId;
    }

    public void setPsId(Long psId) {
        this.psId = psId;
    }

    public String getPsName() {
        return psName;
    }

    public void setPsName(String psName) {
        this.psName = psName;
    }

    public String getPsLocation() {
        return psLocation;
    }

    public void setPsLocation(String psLocation) {
        this.psLocation = psLocation;
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

    public PowerStationValue getTotalEnergy() {
        return totalEnergy;
    }

    public void setTotalEnergy(PowerStationValue totalEnergy) {
        this.totalEnergy = totalEnergy;
    }

    public PowerStationValue getTodayEnergy() {
        return todayEnergy;
    }

    public void setTodayEnergy(PowerStationValue todayEnergy) {
        this.todayEnergy = todayEnergy;
    }

    public PowerStationValue getCurrPower() {
        return currPower;
    }

    public void setCurrPower(PowerStationValue currPower) {
        this.currPower = currPower;
    }

    public PowerStationValue getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(PowerStationValue totalIncome) {
        this.totalIncome = totalIncome;
    }

    public PowerStationValue getTodayIncome() {
        return todayIncome;
    }

    public void setTodayIncome(PowerStationValue todayIncome) {
        this.todayIncome = todayIncome;
    }

    public PowerStationValue getMonthIncome() {
        return monthIncome;
    }

    public void setMonthIncome(PowerStationValue monthIncome) {
        this.monthIncome = monthIncome;
    }

    public PowerStationValue getYearIncome() {
        return yearIncome;
    }

    public void setYearIncome(PowerStationValue yearIncome) {
        this.yearIncome = yearIncome;
    }

    public PowerStationValue getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(PowerStationValue totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public PowerStationValue getCo2Reduce() {
        return co2Reduce;
    }

    public void setCo2Reduce(PowerStationValue co2Reduce) {
        this.co2Reduce = co2Reduce;
    }

    public PowerStationValue getCo2ReduceTotal() {
        return co2ReduceTotal;
    }

    public void setCo2ReduceTotal(PowerStationValue co2ReduceTotal) {
        this.co2ReduceTotal = co2ReduceTotal;
    }

    public PowerStationValue getEquivalentHour() {
        return equivalentHour;
    }

    public void setEquivalentHour(PowerStationValue equivalentHour) {
        this.equivalentHour = equivalentHour;
    }

    public Integer getPsStatus() {
        return psStatus;
    }

    public void setPsStatus(Integer psStatus) {
        this.psStatus = psStatus;
    }

    public Integer getPsFaultStatus() {
        return psFaultStatus;
    }

    public void setPsFaultStatus(Integer psFaultStatus) {
        this.psFaultStatus = psFaultStatus;
    }

    public Integer getGridConnectionStatus() {
        return gridConnectionStatus;
    }

    public void setGridConnectionStatus(Integer gridConnectionStatus) {
        this.gridConnectionStatus = gridConnectionStatus;
    }

    public Integer getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(Integer buildStatus) {
        this.buildStatus = buildStatus;
    }

    public Integer getPsType() {
        return psType;
    }

    public void setPsType(Integer psType) {
        this.psType = psType;
    }

    public Integer getConnectType() {
        return connectType;
    }

    public void setConnectType(Integer connectType) {
        this.connectType = connectType;
    }

    public Integer getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(Integer validFlag) {
        this.validFlag = validFlag;
    }

    public Integer getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(Integer alarmCount) {
        this.alarmCount = alarmCount;
    }

    public Integer getFaultCount() {
        return faultCount;
    }

    public void setFaultCount(Integer faultCount) {
        this.faultCount = faultCount;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

    public Long getGridConnectionTime() {
        return gridConnectionTime;
    }

    public void setGridConnectionTime(Long gridConnectionTime) {
        this.gridConnectionTime = gridConnectionTime;
    }

    public String getShareType() {
        return shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public String getPsCurrentTimeZone() {
        return psCurrentTimeZone;
    }

    public void setPsCurrentTimeZone(String psCurrentTimeZone) {
        this.psCurrentTimeZone = psCurrentTimeZone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTotalEnergyUpdateTime() {
        return totalEnergyUpdateTime;
    }

    public void setTotalEnergyUpdateTime(String totalEnergyUpdateTime) {
        this.totalEnergyUpdateTime = totalEnergyUpdateTime;
    }

    public String getTodayEnergyUpdateTime() {
        return todayEnergyUpdateTime;
    }

    public void setTodayEnergyUpdateTime(String todayEnergyUpdateTime) {
        this.todayEnergyUpdateTime = todayEnergyUpdateTime;
    }

    public String getCurrPowerUpdateTime() {
        return currPowerUpdateTime;
    }

    public void setCurrPowerUpdateTime(String currPowerUpdateTime) {
        this.currPowerUpdateTime = currPowerUpdateTime;
    }

    public String getTotalIncomeUpdateTime() {
        return totalIncomeUpdateTime;
    }

    public void setTotalIncomeUpdateTime(String totalIncomeUpdateTime) {
        this.totalIncomeUpdateTime = totalIncomeUpdateTime;
    }

    public String getTodayIncomeUpdateTime() {
        return todayIncomeUpdateTime;
    }

    public void setTodayIncomeUpdateTime(String todayIncomeUpdateTime) {
        this.todayIncomeUpdateTime = todayIncomeUpdateTime;
    }

    public String getMonthIncomeUpdateTime() {
        return monthIncomeUpdateTime;
    }

    public void setMonthIncomeUpdateTime(String monthIncomeUpdateTime) {
        this.monthIncomeUpdateTime = monthIncomeUpdateTime;
    }

    public String getYearIncomeUpdateTime() {
        return yearIncomeUpdateTime;
    }

    public void setYearIncomeUpdateTime(String yearIncomeUpdateTime) {
        this.yearIncomeUpdateTime = yearIncomeUpdateTime;
    }

    public String getTotalCapacityUpdateTime() {
        return totalCapacityUpdateTime;
    }

    public void setTotalCapacityUpdateTime(String totalCapacityUpdateTime) {
        this.totalCapacityUpdateTime = totalCapacityUpdateTime;
    }

    public String getCo2ReduceUpdateTime() {
        return co2ReduceUpdateTime;
    }

    public void setCo2ReduceUpdateTime(String co2ReduceUpdateTime) {
        this.co2ReduceUpdateTime = co2ReduceUpdateTime;
    }

    public String getCo2ReduceTotalUpdateTime() {
        return co2ReduceTotalUpdateTime;
    }

    public void setCo2ReduceTotalUpdateTime(String co2ReduceTotalUpdateTime) {
        this.co2ReduceTotalUpdateTime = co2ReduceTotalUpdateTime;
    }

    public String getEquivalentHourUpdateTime() {
        return equivalentHourUpdateTime;
    }

    public void setEquivalentHourUpdateTime(String equivalentHourUpdateTime) {
        this.equivalentHourUpdateTime = equivalentHourUpdateTime;
    }

    // 便捷方法
    public boolean isOnline() {
        return psStatus != null && psStatus == 1;
    }

    public boolean hasAlarms() {
        return alarmCount != null && alarmCount > 0;
    }

    public boolean hasFaults() {
        return faultCount != null && faultCount > 0;
    }

    public boolean isGridConnected() {
        return gridConnectionStatus != null && gridConnectionStatus == 1;
    }

    @Override
    public String toString() {
        return "PowerStation{" +
                "psId=" + psId +
                ", psName='" + psName + '\'' +
                ", psLocation='" + psLocation + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", districtName='" + districtName + '\'' +
                ", totalEnergy=" + totalEnergy +
                ", todayEnergy=" + todayEnergy +
                ", currPower=" + currPower +
                ", totalIncome=" + totalIncome +
                ", todayIncome=" + todayIncome +
                ", psStatus=" + psStatus +
                ", gridConnectionStatus=" + gridConnectionStatus +
                '}';
    }
}