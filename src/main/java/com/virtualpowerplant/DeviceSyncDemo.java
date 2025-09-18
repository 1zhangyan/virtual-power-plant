package com.virtualpowerplant;

import com.virtualpowerplant.service.SunGrowDataService;
import com.virtualpowerplant.model.PowerStation;
import com.virtualpowerplant.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 设备同步演示程序
 * 演示分页获取SunGrow API数据的功能
 */
public class DeviceSyncDemo {

    private static final Logger logger = LoggerFactory.getLogger(DeviceSyncDemo.class);

    public static void main(String[] args) {
        try {
            logger.info("=== SunGrow分页数据获取演示 ===");

            // 1. 登录获取token
            logger.info("步骤1: 登录SunGrow账号...");
            String token = SunGrowDataService.loginAndGetUserInfo().getToken();
            logger.info("登录成功，Token: {}...", token.substring(0, Math.min(20, token.length())));

            // 2. 分页获取所有电站信息
            logger.info("\n步骤2: 分页获取电站信息...");
            List<PowerStation> powerStations = SunGrowDataService.getPowerStationsAndParse(token);
            logger.info("电站信息获取完成:");
            logger.info("  - 总电站数: {}", powerStations.size());

            int stationsWithCoordinates = 0;
            for (PowerStation ps : powerStations) {
                if (ps.getLatitude() != null && ps.getLongitude() != null) {
                    stationsWithCoordinates++;
                }
            }
            logger.info("  - 有经纬度的电站: {}", stationsWithCoordinates);

            // 显示前5个电站信息
            logger.info("  - 前5个电站信息:");
            for (int i = 0; i < Math.min(5, powerStations.size()); i++) {
                PowerStation ps = powerStations.get(i);
                logger.info("    {}. {} (ID: {}) - 经纬度: {}, {}",
                    i + 1, ps.getPsName(), ps.getPsId(),
                    ps.getLatitude(), ps.getLongitude());
            }

            // 3. 分页获取所有设备信息
            logger.info("\n步骤3: 分页获取设备信息...");
            List<Device> devices = SunGrowDataService.getDevicesAndParse(token);
            logger.info("设备信息获取完成:");
            logger.info("  - 总设备数: {}", devices.size());

            int inverterCount = 0;
            int communicationModuleCount = 0;
            int onlineCount = 0;
            for (Device device : devices) {
                if (device.isInverter()) {
                    inverterCount++;
                }
                if (device.isCommunicationModule()) {
                    communicationModuleCount++;
                }
                if (device.isOnline()) {
                    onlineCount++;
                }
            }
            logger.info("  - 逆变器数量: {}", inverterCount);
            logger.info("  - 通信模块数量: {}", communicationModuleCount);
            logger.info("  - 在线设备数量: {}", onlineCount);

            // 显示前5个设备信息
            logger.info("  - 前5个设备信息:");
            for (int i = 0; i < Math.min(5, devices.size()); i++) {
                Device device = devices.get(i);
                logger.info("    {}. {} (UUID: {}) - 类型: {} - 状态: {} - 电站ID: {}",
                    i + 1, device.getDeviceName(), device.getUuid(),
                    device.getTypeName(), device.getDevStatus(), device.getPsId());
            }

            logger.info("\n=== 演示完成 ===");

        } catch (Exception e) {
            logger.error("演示过程中发生错误: {}", e.getMessage(), e);
        }
    }
}