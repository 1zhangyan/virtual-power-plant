package com.virtualpowerplant.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DeviceRealTimeData {
    private Long vppId;
    private String deviceSn;
    private Double latitude;
    private Double longitude;
    private Double activePower;
    private LocalDateTime deviceTime;

}