package com.virtualpowerplant.controller;

import com.virtualpowerplant.model.Device;
import com.virtualpowerplant.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@Tag(name = "设备管理", description = "设备同步和查询接口")
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

//    @PostMapping("/sync")
//    @Operation(summary = "同步SunGrow设备数据", description = "登录SunGrow账号，获取所有设备信息和对应电站的经纬度，同步到数据库")
//    public ResponseEntity<String> syncDevices() {
//        try {
//            logger.info("开始同步SunGrow设备数据...");
//            deviceService.syncDevicesWithCoordinates();
//            logger.info("设备数据同步完成");
//            return ResponseEntity.ok("设备数据同步成功");
//        } catch (Exception e) {
//            logger.error("设备数据同步失败: {}", e.getMessage(), e);
//            return ResponseEntity.internalServerError().body("设备数据同步失败: " + e.getMessage());
//        }
//    }

    @GetMapping("/inverters/coordinates")
    @Operation(summary = "查询带经纬度的逆变器", description = "根据vppId返回所有带经纬度信息的逆变器设备，如果不提供vppId则返回所有设备")
    public ResponseEntity<List<Device>> getInvertersWithCoordinates(@RequestParam(required = false) String vppId) {
        try {
            List<Device> inverters;
            if (vppId != null && !vppId.trim().isEmpty()) {
                inverters = deviceService.getInvertersByVppId(vppId.trim());
                logger.info("查询到 {} 个VPP {} 的带经纬度逆变器设备", inverters.size(), vppId);
            } else {
                inverters = deviceService.getInverters();
                logger.info("查询到 {} 个带经纬度的逆变器设备", inverters.size());
            }
            return ResponseEntity.ok(inverters);
        } catch (Exception e) {
            logger.error("查询逆变器失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
}