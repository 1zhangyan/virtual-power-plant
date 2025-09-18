package com.virtualpowerplant.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SunGrowResponse<T> {

    @JsonProperty("req_serial_num")
    private String reqSerialNum;

    @JsonProperty("result_code")
    private String resultCode;

    @JsonProperty("result_msg")
    private String resultMsg;

    @JsonProperty("result_data")
    private T resultData;

    public SunGrowResponse() {}

    public SunGrowResponse(String reqSerialNum, String resultCode, String resultMsg, T resultData) {
        this.reqSerialNum = reqSerialNum;
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.resultData = resultData;
    }

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

    public T getResultData() {
        return resultData;
    }

    public void setResultData(T resultData) {
        this.resultData = resultData;
    }

    public boolean isSuccess() {
        return "1".equals(resultCode);
    }

    @Override
    public String toString() {
        return "SunGrowResponse{" +
                "reqSerialNum='" + reqSerialNum + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", resultMsg='" + resultMsg + '\'' +
                ", resultData=" + resultData +
                '}';
    }
}