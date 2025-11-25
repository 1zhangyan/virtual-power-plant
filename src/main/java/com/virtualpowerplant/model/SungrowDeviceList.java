package com.virtualpowerplant.model;

import java.util.List;
import java.util.stream.Collectors;

public class SungrowDeviceList {

    private List<SungrowDevice> pageList;
    private Integer rowCount;

    public SungrowDeviceList() {}

    public SungrowDeviceList(List<SungrowDevice> pageList, Integer rowCount) {
        this.pageList = pageList;
        this.rowCount = rowCount;
    }

    public List<SungrowDevice> getPageList() {
        return pageList;
    }

    public void setPageList(List<SungrowDevice> pageList) {
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
    public List<SungrowDevice> getInverters() {
        if (pageList == null) return null;
        return pageList.stream()
                .filter(SungrowDevice::isInverter)
                .collect(Collectors.toList());
    }

    public List<SungrowDevice> getCommunicationModules() {
        if (pageList == null) return null;
        return pageList.stream()
                .filter(SungrowDevice::isCommunicationModule)
                .collect(Collectors.toList());
    }

    public List<SungrowDevice> getOnlineDevices() {
        if (pageList == null) return null;
        return pageList.stream()
                .filter(SungrowDevice::isOnline)
                .collect(Collectors.toList());
    }

    public List<SungrowDevice> getFaultyDevices() {
        if (pageList == null) return null;
        return pageList.stream()
                .filter(SungrowDevice::hasFault)
                .collect(Collectors.toList());
    }

    public long getInverterCount() {
        return pageList != null ? pageList.stream().filter(SungrowDevice::isInverter).count() : 0;
    }

    public long getCommunicationModuleCount() {
        return pageList != null ? pageList.stream().filter(SungrowDevice::isCommunicationModule).count() : 0;
    }

    public long getOnlineDeviceCount() {
        return pageList != null ? pageList.stream().filter(SungrowDevice::isOnline).count() : 0;
    }

    public long getFaultyDeviceCount() {
        return pageList != null ? pageList.stream().filter(SungrowDevice::hasFault).count() : 0;
    }

    @Override
    public String toString() {
        return "SungrowDeviceList{" +
                "pageList=" + (pageList != null ? pageList.size() + " devices" : "null") +
                ", rowCount=" + rowCount +
                ", inverters=" + getInverterCount() +
                ", communicationModules=" + getCommunicationModuleCount() +
                ", onlineDevices=" + getOnlineDeviceCount() +
                ", faultyDevices=" + getFaultyDeviceCount() +
                '}';
    }
}