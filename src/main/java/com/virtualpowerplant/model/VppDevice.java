package com.virtualpowerplant.model;
import lombok.Data;

@Data
public class VppDevice {
   private Long deviceId;
   private Long vppId;
   private String deviceSn;
   private String deviceName;
   private String deviceType;
   private Double longitude;
   private Double latitude;
   private Double longitudeStandard;
   private Double latitudeStandard;
   private String province;
   private String city;
   private String createTime;
   private String updateTime;
}