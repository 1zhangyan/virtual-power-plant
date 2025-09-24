package com.virtualpowerplant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class VirtualPowerPlant {

    @JsonProperty("vpp_id")
    private Long vppId;

    @JsonProperty("mobile_tel")
    private String mobileTel;

    @JsonProperty("user_name")
    private String userName;

    private String language;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("user_account")
    private String userAccount;

    @JsonProperty("user_master_org_name")
    private String userMasterOrgName;

    private String email;

    @JsonProperty("country_id")
    private String countryId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public VirtualPowerPlant() {}

    // Getters and Setters
    public Long getVppId() {
        return vppId;
    }

    public void setVppId(Long vppId) {
        this.vppId = vppId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "VirtualPowerPlant{" +
                "vppId=" + vppId +
                ", mobileTel='" + mobileTel + '\'' +
                ", userName='" + userName + '\'' +
                ", language='" + language + '\'' +
                ", userId='" + userId + '\'' +
                ", countryName='" + countryName + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", userMasterOrgName='" + userMasterOrgName + '\'' +
                ", email='" + email + '\'' +
                ", countryId='" + countryId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}