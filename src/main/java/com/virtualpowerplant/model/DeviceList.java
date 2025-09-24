package com.virtualpowerplant.model;

import java.util.List;
import java.util.stream.Collectors;

public class DeviceList {

    private List<Device> pageList;
    private Integer rowCount;

    public DeviceList() {}

    public DeviceList(List<Device> pageList, Integer rowCount) {
        this.pageList = pageList;
        this.rowCount = rowCount;
    }

    public List<Device> getPageList() {
        return pageList;
    }

    public void setPageList(List<Device> pageList) {
        this.pageList = pageList;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public int getPageSize() {
        return pageList != null ? pageList.size() : 0;
    }

    public boolean isEmpty() {
        return pageList == null || pageList.isEmpty();
    }

    // 便捷方法
    public List<Device> getInverters() {
        if (pageList == null) return null;
        return pageList.stream()
                .filter(Device::isInverter)
                .collect(Collectors.toList());
    }

    public List<Device> getCommunicationModules() {
        if (pageList == null) return null;
        return pageList.stream()
                .filter(Device::isCommunicationModule)
                .collect(Collectors.toList());
    }

    public List<Device> getOnlineDevices() {
        if (pageList == null) return null;
        return pageList.stream()
                .filter(Device::isOnline)
                .collect(Collectors.toList());
    }

    public List<Device> getFaultyDevices() {
        if (pageList == null) return null;
        return pageList.stream()
                .filter(Device::hasFault)
                .collect(Collectors.toList());
    }

    public long getInverterCount() {
        return pageList != null ? pageList.stream().filter(Device::isInverter).count() : 0;
    }

    public long getCommunicationModuleCount() {
        return pageList != null ? pageList.stream().filter(Device::isCommunicationModule).count() : 0;
    }

    public long getOnlineDeviceCount() {
        return pageList != null ? pageList.stream().filter(Device::isOnline).count() : 0;
    }

    public long getFaultyDeviceCount() {
        return pageList != null ? pageList.stream().filter(Device::hasFault).count() : 0;
    }

    @Override
    public String toString() {
        return "DeviceList{" +
                "pageList=" + (pageList != null ? pageList.size() + " devices" : "null") +
                ", rowCount=" + rowCount +
                ", inverters=" + getInverterCount() +
                ", communicationModules=" + getCommunicationModuleCount() +
                ", onlineDevices=" + getOnlineDeviceCount() +
                ", faultyDevices=" + getFaultyDeviceCount() +
                '}';
    }
}