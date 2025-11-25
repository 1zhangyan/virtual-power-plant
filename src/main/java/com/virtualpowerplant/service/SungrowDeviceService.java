package com.virtualpowerplant.service;

import static com.virtualpowerplant.utils.PositionStandard.positionStandard;

import com.virtualpowerplant.mapper.VppDeviceMapper;
import com.virtualpowerplant.model.SungrowDevice;
import com.virtualpowerplant.model.PowerStation;
import com.virtualpowerplant.model.SunGrowUserInfo;
import com.virtualpowerplant.model.VirtualPowerPlant;
import com.virtualpowerplant.model.VppDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SungrowDeviceService {

    private static final Logger logger = LoggerFactory.getLogger(SungrowDeviceService.class);

    @Autowired
    private VppDeviceMapper vppDeviceMapper;

    @Autowired
    private VppService vppService;

    /**
     * 同步SunGrow设备数据到数据库，包含经纬度信息
     */
    public void syncDevicesWithCoordinates() {
        try {
            logger.info("开始同步SunGrow设备数据...");
            SunGrowUserInfo sunGrowUserInfo = SunGrowDataService.getCachedUserInfo();
            VirtualPowerPlant virtualPowerPlant = new VirtualPowerPlant();
            BeanUtils.copyProperties(sunGrowUserInfo, virtualPowerPlant);
            Long vppId = vppService.findOrInsert(virtualPowerPlant);
            List<PowerStation> powerStations = SunGrowDataService.getPowerStationsAndParse();
            Map<Long, PowerStation> psMap = powerStations.stream()
                    .collect(Collectors.toMap(PowerStation::getPsId, ps -> ps));
            List<SungrowDevice> devices = SunGrowDataService.getDevicesAndParse();
            logger.info("获取到 {} 个设备信息", devices.size());
            logger.info("开始处理设备数据并同步到数据库...");
            for (SungrowDevice device : devices) {
                VppDevice vppDevice = new VppDevice();
                PowerStation ps = psMap.get(device.getPsId());
                if (ps != null && device.getTypeName().equals("逆变器")) {
                    vppDevice.setDeviceSn(device.getDeviceSn());
                    vppDevice.setVppId(vppId);
                    vppDevice.setDeviceName(device.getDeviceName());
                    vppDevice.setDeviceType(device.getTypeName());
                    vppDevice.setLongitude(ps.getLongitude());
                    vppDevice.setLatitude(ps.getLatitude());
                    vppDevice.setLongitudeStandard(positionStandard(ps.getLongitude()));
                    vppDevice.setLatitudeStandard(positionStandard(ps.getLatitude()));
                    vppDevice.setProvince(ps.getProvinceName());
                    vppDevice.setCity(ps.getCityName());
                    vppDeviceMapper.upsert(vppDevice);
                }
            }
            logger.info("设备数据同步完成");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 根据vppId查询逆变器设备
     */
    public List<VppDevice> getInvertersByVppId(Long vppId) {
        return vppDeviceMapper.selectInvertersByVppId(vppId);
    }
}