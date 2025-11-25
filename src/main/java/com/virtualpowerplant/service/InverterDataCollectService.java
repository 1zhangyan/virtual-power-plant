package com.virtualpowerplant.service;

import com.virtualpowerplant.mapper.VppDeviceMapper;
import com.virtualpowerplant.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InverterDataCollectService {

    private static final Logger logger = LoggerFactory.getLogger(InverterDataCollectService.class);

    @Autowired
    private InverterDataLindormService inverterDataLindormService;

    @Autowired
    private VppDeviceMapper vppDeviceMapper;

    //    @Scheduled(fixedRate = 2*60*1000)
    public void collectInverterRealTimeData(Long vppId) {
        try {
            List<VppDevice> inverters = vppDeviceMapper.selectInvertersByVppId(vppId);
            if (inverters.isEmpty()) {
                logger.warn("未找到任何逆变器设备，跳过实时数据收集");
                return;
            }
            List<VppDevice> devices = inverters.stream()
                    .filter(it -> StringUtils.isNotBlank(it.getDeviceSn()) && it.getLatitude() > 0.0 && it.getLatitude() > 0.0)
                    .collect(Collectors.toList());
            if (devices.isEmpty()) {
                logger.warn("逆变器设备没有有效的序列号，跳过实时数据收集");
                return;
            }
            List<DeviceRealTimeData> realTimeDataList = SunGrowDataService.getRealTimeDataAndParse(devices)
                    .stream().map(it -> {
                        it.setVppId(vppId);
                        return it;
                    }).collect(Collectors.toList());

            if (realTimeDataList != null && !realTimeDataList.isEmpty()) {
                inverterDataLindormService.writeRealTimeData(realTimeDataList);
                logger.debug("成功保存 {} 条逆变器实时数据到Lindorm", realTimeDataList.size());
            } else {
                logger.warn("未获取到任何逆变器实时数据");
            }
        } catch (Exception e) {
            logger.error("定时收集逆变器实时数据失败: {}", e.getMessage(), e);
        }
    }


}