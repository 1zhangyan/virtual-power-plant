package com.virtualpowerplant.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SunGrowUserInfo {

    @JsonProperty("user_master_org_id")
    private String userMasterOrgId;

    @JsonProperty("mobile_tel")
    private String mobileTel;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("language")
    private String language;

    @JsonProperty("token")
    private String token;

    @JsonProperty("err_times")
    private String errTimes;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("login_state")
    private String loginState;

    @JsonProperty("disable_time")
    private String disableTime;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("user_account")
    private String userAccount;

    @JsonProperty("user_master_org_name")
    private String userMasterOrgName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("country_id")
    private String countryId;

    public SunGrowUserInfo() {}

    public String getUserMasterOrgId() {
        return userMasterOrgId;
    }

    public void setUserMasterOrgId(String userMasterOrgId) {
        this.userMasterOrgId = userMasterOrgId;
    }

    public String getMobileTel() {
        return mobileTel;
    }

    public void setMobileTel(String mobileTel) {
        this.mobileTel = mobileTel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getErrTimes() {
        return errTimes;
    }

    public void setErrTimes(String errTimes) {
        this.errTimes = errTimes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginState() {
        return loginState;
    }

    public void setLoginState(String loginState) {
        this.loginState = loginState;
    }

    public String getDisableTime() {
        return disableTime;
    }

    public void setDisableTime(String disableTime) {
        this.disableTime = disableTime;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserMasterOrgName() {
        return userMasterOrgName;
    }

    public void setUserMasterOrgName(String userMasterOrgName) {
        this.userMasterOrgName = userMasterOrgName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public boolean isLoginSuccessful() {
        return "1".equals(loginState);
    }

    @Override
    public String toString() {
        return "SunGrowUserInfo{" +
                "userMasterOrgId='" + userMasterOrgId + '\'' +
                ", mobileTel='" + mobileTel + '\'' +
                ", userName='" + userName + '\'' +
                ", language='" + language + '\'' +
                ", token='" + token + '\'' +
                ", errTimes='" + errTimes + '\'' +
                ", userId='" + userId + '\'' +
                ", loginState='" + loginState + '\'' +
                ", disableTime='" + disableTime + '\'' +
                ", countryName='" + countryName + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", userMasterOrgName='" + userMasterOrgName + '\'' +
                ", email='" + email + '\'' +
                ", countryId='" + countryId + '\'' +
                '}';
    }
}