package com.virtualpowerplant.model;

import java.util.List;

public class PowerStationList {

    private List<PowerStation> pageList;
    private Integer rowCount;

    public PowerStationList() {}

    public PowerStationList(List<PowerStation> pageList, Integer rowCount) {
        this.pageList = pageList;
        this.rowCount = rowCount;
    }

    public List<PowerStation> getPageList() {
        return pageList;
    }

    public void setPageList(List<PowerStation> pageList) {
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

    @Override
    public String toString() {
        return "PowerStationList{" +
                "pageList=" + (pageList != null ? pageList.size() + " stations" : "null") +
                ", rowCount=" + rowCount +
                '}';
    }
}