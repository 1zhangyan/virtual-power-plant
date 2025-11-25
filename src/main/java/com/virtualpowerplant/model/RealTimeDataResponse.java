package com.virtualpowerplant.model;

import java.util.List;

public class RealTimeDataResponse {

    private String reqSerialNum;
    private String resultCode;
    private String resultMsg;
    private ResultData resultData;

    public String getReqSerialNum() {
        return reqSerialNum;
    }

    public void setReqSerialNum(String reqSerialNum) {
        this.reqSerialNum = reqSerialNum;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public ResultData getResultData() {
        return resultData;
    }

    public void setResultData(ResultData resultData) {
        this.resultData = resultData;
    }

    public static class ResultData {
        private List<String> failSnList;
        private List<DevicePointWrapper> devicePointList;

        public List<String> getFailSnList() {
            return failSnList;
        }

        public void setFailSnList(List<String> failSnList) {
            this.failSnList = failSnList;
        }

        public List<DevicePointWrapper> getDevicePointList() {
            return devicePointList;
        }

        public void setDevicePointList(List<DevicePointWrapper> devicePointList) {
            this.devicePointList = devicePointList;
        }
    }

    public static class DevicePointWrapper {
        private SungrowDevicePoint devicePoint;

        public SungrowDevicePoint getDevicePoint() {
            return devicePoint;
        }

        public void setDevicePoint(SungrowDevicePoint devicePoint) {
            this.devicePoint = devicePoint;
        }
    }

    @Override
    public String toString() {
        return "RealTimeDataResponse{" +
                "reqSerialNum='" + reqSerialNum + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", resultMsg='" + resultMsg + '\'' +
                ", resultData=" + resultData +
                '}';
    }
}