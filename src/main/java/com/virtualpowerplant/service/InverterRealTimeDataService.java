package com.virtualpowerplant.service;

import com.virtualpowerplant.mapper.DeviceMapper;
import com.virtualpowerplant.model.Device;
import com.virtualpowerplant.model.InverterRealTimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InverterRealTimeDataService {

    private static final Logger logger = LoggerFactory.getLogger(InverterRealTimeDataService.class);

    @Autowired
    private LindormTSDBService lindormTSDBService;

    @Autowired
    private DeviceMapper deviceMapper;

    @Scheduled(fixedRate = 60000)
    public void collectInverterRealTimeData() {
        try {
            logger.info("开始定时收集逆变器实时数据...");
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

            logger.info("准备收集 {} 台逆变器的实时数据，序列号: {}", snList.size(), snList);

            List<InverterRealTimeData> realTimeDataList = SunGrowDataService.getRealTimeDataAndParse(snList, inverters);

            if (realTimeDataList != null && !realTimeDataList.isEmpty()) {
                lindormTSDBService.writeRealTimeData(realTimeDataList);
                logger.info("成功保存 {} 条实时数据到Lindorm", realTimeDataList.size());
            } else {
                logger.warn("未获取到任何实时数据");
            }

            long endTime = System.currentTimeMillis();
            logger.info("实时数据收集完成，耗时: {} ms", endTime - startTime);

        } catch (Exception e) {
            logger.error("定时收集逆变器实时数据失败: {}", e.getMessage(), e);
        }
    }

    public List<InverterRealTimeData> getLatestData(int limit) {
        try {
            return lindormTSDBService.queryLatestData(limit);
        } catch (Exception e) {
            logger.error("获取最新实时数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> getDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            long startTimestamp = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endTimestamp = endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return lindormTSDBService.queryByTimeRange(startTimestamp, endTimestamp);
        } catch (Exception e) {
            logger.error("根据时间范围获取实时数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> getInverterDataByTimeRange(String inverterSn, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 可以通过先查询指定逆变器的数据，然后过滤时间范围，或者直接在Lindorm中查询
            List<InverterRealTimeData> allData = lindormTSDBService.queryByInverterSn(inverterSn, 1000);
            return allData.stream()
                    .filter(data -> data.getDeviceTime() != null &&
                            !data.getDeviceTime().isBefore(startTime) &&
                            !data.getDeviceTime().isAfter(endTime))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("根据逆变器和时间范围获取实时数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> getLatestInverterData(String inverterSn, int limit) {
        try {
            return lindormTSDBService.queryByInverterSn(inverterSn, limit);
        } catch (Exception e) {
            logger.error("获取逆变器最新实时数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> getLatestPowerStationData(String psKey, int limit) {
        try {
            return lindormTSDBService.queryByPowerStation(psKey, limit);
        } catch (Exception e) {
            logger.error("获取电站最新实时数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}