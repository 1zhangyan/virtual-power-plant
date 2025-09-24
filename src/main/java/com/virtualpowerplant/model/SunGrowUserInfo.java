package com.virtualpowerplant.model;

public class SunGrowUserInfo {

    private String userMasterOrgId;
    private String mobileTel;
    private String userName;
    private String language;
    private String token;
    private String errTimes;
    private String userId;
    private String loginState;
    private String disableTime;
    private String countryName;
    private String userAccount;
    private String userMasterOrgName;
    private String email;
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