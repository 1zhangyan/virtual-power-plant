package com.virtualpowerplant.service;

import com.virtualpowerplant.mapper.DeviceMapper;
import com.virtualpowerplant.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InverterDataCollectService {

    private static final Logger logger = LoggerFactory.getLogger(InverterDataCollectService.class);

    @Autowired
    private InverterDataLindormService inverterDataLindormService;

    @Autowired
    private DeviceMapper deviceMapper;

    @Scheduled(fixedRate = 2*60*1000)
    public void collectInverterRealTimeData() {
        try {
            logger.info("开始定时收集逆变器实时功率数据...");
            long startTime = System.currentTimeMillis();
            List<Device> inverters = deviceMapper.selectInverters();
            if (inverters.isEmpty()) {
                logger.warn("未找到任何逆变器设备，跳过实时数据收集");
                return;
            }
            List<String> snList = inverters.stream()
                    .map(Device::getDeviceSn)
                    .filter(sn -> sn != null && !sn.isEmpty())
                    .collect(Collectors.toList());
            if (snList.isEmpty()) {
                logger.warn("逆变器设备没有有效的序列号，跳过实时数据收集");
                return;
            }
            List<InverterRealTimeData> realTimeDataList = SunGrowDataService.getRealTimeDataAndParse(snList, inverters);

            if (realTimeDataList != null && !realTimeDataList.isEmpty()) {
                inverterDataLindormService.writeRealTimeData(realTimeDataList);
                logger.debug("成功保存 {} 条逆变器实时数据到Lindorm", realTimeDataList.size());
            } else {
                logger.warn("未获取到任何逆变器实时数据");
            }

            long endTime = System.currentTimeMillis();
            logger.debug("逆变器实时数据收集完成，耗时: {} ms", endTime - startTime);

        } catch (Exception e) {
            logger.error("定时收集逆变器实时数据失败: {}", e.getMessage(), e);
        }
    }


}