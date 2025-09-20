package com.virtualpowerplant.service;

import com.virtualpowerplant.mapper.DeviceMapper;
import com.virtualpowerplant.model.Device;
import com.virtualpowerplant.model.PowerStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private SunGrowDataService sunGrowDataService;

    /**
     * 同步SunGrow设备数据到数据库，包含经纬度信息
     */
    public void syncDevicesWithCoordinates() throws Exception {
        logger.info("开始同步SunGrow设备数据...");

        // 1. 使用缓存token（自动处理登录）
        logger.info("使用缓存token进行API调用...");

        // 2. 获取所有电站信息（包含经纬度）
        logger.info("开始获取电站信息...");
        List<PowerStation> powerStations = SunGrowDataService.getPowerStationsAndParse();
        Map<Long, PowerStation> psMap = powerStations.stream()
            .collect(Collectors.toMap(PowerStation::getPsId, ps -> ps));
        logger.info("获取到 {} 个电站信息", powerStations.size());

        // 3. 获取所有设备信息
        logger.info("开始获取设备信息...");
        List<Device> devices = SunGrowDataService.getDevicesAndParse();
        logger.info("获取到 {} 个设备信息", devices.size());

        // 4. 为设备添加经纬度信息并保存到数据库
        logger.info("开始处理设备数据并同步到数据库...");
        LocalDateTime now = LocalDateTime.now();
        int processedCount = 0;
        int updatedCount = 0;

        for (Device device : devices) {
            PowerStation ps = psMap.get(device.getPsId());
            if (ps != null) {
                device.setLatitude(ps.getLatitude());
                device.setLongitude(ps.getLongitude());
                device.setPsName(ps.getPsName());
                device.setPsType(ps.getPsType());
                device.setOnlineStatus(ps.getPsStatus()); // psStatus对应onlineStatus
                device.setProvinceName(ps.getProvinceName());
                device.setCityName(ps.getCityName());
                device.setDistrictName(ps.getDistrictName());
                device.setConnectType(ps.getConnectType());
                updatedCount++;
            }

            if (device.getCreatedAt() == null) {
                device.setCreatedAt(now);
            }
            device.setUpdatedAt(now);

            // 5. Upsert到数据库
            deviceMapper.insertOrUpdate(device);
            processedCount++;

            // 每处理100个设备记录一次日志
            if (processedCount % 100 == 0) {
                logger.info("已处理 {} / {} 个设备", processedCount, devices.size());
            }
        }

        logger.info("设备数据同步完成：总共处理 {} 个设备，其中 {} 个设备添加了电站信息(经纬度、电站名称、省市区等)",
                   processedCount, updatedCount);
    }

    /**
     * 查询所有带经纬度信息的逆变器
     */
    public List<Device> getInverters() {
        return deviceMapper.selectInverters();
    }
}