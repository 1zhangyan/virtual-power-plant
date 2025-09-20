package com.virtualpowerplant.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RealTimeDataResponse {

    @JsonProperty("req_serial_num")
    private String reqSerialNum;

    @JsonProperty("result_code")
    private String resultCode;

    @JsonProperty("result_msg")
    private String resultMsg;

    @JsonProperty("result_data")
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultData {
        @JsonProperty("fail_sn_list")
        private List<String> failSnList;

        @JsonProperty("device_point_list")
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DevicePointWrapper {
        @JsonProperty("device_point")
        private DevicePoint devicePoint;

        public DevicePoint getDevicePoint() {
            return devicePoint;
        }

        public void setDevicePoint(DevicePoint devicePoint) {
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