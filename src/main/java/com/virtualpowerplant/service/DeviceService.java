package com.virtualpowerplant.service;

import com.virtualpowerplant.mapper.DeviceMapper;
import com.virtualpowerplant.model.Device;
import com.virtualpowerplant.model.PowerStation;
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

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private SunGrowDataService sunGrowDataService;

    /**
     * 同步SunGrow设备数据到数据库，包含经纬度信息
     */
    public void syncDevicesWithCoordinates() throws Exception {
        // 1. 登录SunGrow获取token
        String token = sunGrowDataService.loginAndGetToken();

        // 2. 获取所有电站信息（包含经纬度）
        List<PowerStation> powerStations = sunGrowDataService.getPowerStationsAndParse(token);
        Map<Long, PowerStation> psMap = powerStations.stream()
            .collect(Collectors.toMap(PowerStation::getPsId, ps -> ps));

        // 3. 获取所有设备信息
        List<Device> devices = sunGrowDataService.getDevicesAndParse(token);

        // 4. 为设备添加经纬度信息
        LocalDateTime now = LocalDateTime.now();
        for (Device device : devices) {
            PowerStation ps = psMap.get(device.getPsId());
            if (ps != null) {
                device.setLatitude(ps.getLatitude());
                device.setLongitude(ps.getLongitude());
            }

            if (device.getCreatedAt() == null) {
                device.setCreatedAt(now);
            }
            device.setUpdatedAt(now);

            // 5. Upsert到数据库
            deviceMapper.insertOrUpdate(device);
        }
    }

    /**
     * 查询所有带经纬度信息的逆变器
     */
    public List<Device> getInvertersWithCoordinates() {
        return deviceMapper.selectInvertersWithCoordinates();
    }
}