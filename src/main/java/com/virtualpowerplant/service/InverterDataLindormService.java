package com.virtualpowerplant.service;


import com.virtualpowerplant.config.LindormConfig;
import com.virtualpowerplant.model.DeviceRealTimeData;
import com.virtualpowerplant.utils.PositionStandard;
import com.virtualpowerplant.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@DependsOn(value = "DatasetMetaInfoService")
public class InverterDataLindormService {
    private static final Logger logger = LoggerFactory.getLogger(InverterDataLindormService.class);
    private static final String TABLE_NAME = "devcie_wheather_active_power_info";

    @Autowired
    DatasetMetaInfoService datasetMetaInfoService;

    @Autowired
    WeatherDataService weatherDataService;

    public void writeRealTimeData(List<DeviceRealTimeData> dataList) {
        dataList = dataList.stream().filter(it -> null != it.getLongitude() &&
                        null != it.getLatitude()
                        && it.getLongitude() > 0.0
                        && it.getLatitude() > 0.0)
                .collect(Collectors.toList());
        writeRealTimePowerData(dataList);
        writeRealTimeWeatherData(dataList);
    }

    private void writeRealTimePowerData(List<DeviceRealTimeData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            logger.warn("实时数据列表为空，跳过写入");
            return;
        }

        String insertPowerSQL = String.format(
                "INSERT INTO %s(vpp_id, device_sn, time, latitude, longitude, active_power) VALUES(?, ?, ?, ?, ?, ?)",
                TABLE_NAME
        );
        try (PreparedStatement pstmt = LindormConfig.connection.prepareStatement(insertPowerSQL)) {
            int batchCount = 0;

            for (DeviceRealTimeData data : dataList) {
                if (data.getDeviceSn() == null || data.getDeviceSn().isEmpty()) {
                    logger.warn("逆变器序列号为空，跳过该条记录: {}", data);
                    continue;
                }
                Timestamp timestamp = data.getDeviceTime() != null ?
                        Timestamp.valueOf(data.getDeviceTime()) :
                        new Timestamp(System.currentTimeMillis());

                pstmt.setString(1, data.getVppId().toString());
                pstmt.setString(2, data.getDeviceSn());
                pstmt.setTimestamp(3, timestamp);
                pstmt.setString(4, PositionStandard.positionStandardStr(data.getLatitude()));
                pstmt.setString(5, PositionStandard.positionStandardStr(data.getLongitude()));
                pstmt.setDouble(6, data.getActivePower() != null ? data.getActivePower() : 0.0);
                pstmt.addBatch();
                batchCount++;
            }
            if (batchCount > 0) {
                pstmt.executeBatch();
                logger.info("成功写入 {} 条实时数据到Lindorm", batchCount);
            } else {
                logger.warn("没有有效的记录可写入");
            }
        } catch (SQLException e) {
            logger.error("写入实时数据到Lindorm失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to write real-time data to Lindorm", e);
        }
    }

    private void writeRealTimeWeatherData(List<DeviceRealTimeData> dataList) {
        try (Statement stmt = LindormConfig.connection.createStatement()) {
            for (DeviceRealTimeData data : dataList) {
                try {
                    if (PositionStandard.positionStandard(data.getLongitude()) == 0.0
                            || PositionStandard.positionStandard(data.getLatitude()) == 0.0) {
                        continue;
                    }
                    String sql = updateWeatherTimeDataSql(PositionStandard.positionStandard(data.getLongitude())
                            , PositionStandard.positionStandard(data.getLatitude())
                            , TimeUtils.localTimeToStr(data.getDeviceTime())
                            ,data.getDeviceSn()
                            ,data.getVppId()
                    );
                    if (null != sql) {
                        logger.info(sql);
                        stmt.execute(sql);
                    }
                } catch (Exception e) {
                    logger.error("fail");
                }
            }
            stmt.executeBatch();
            stmt.clearBatch();
        } catch (SQLException e) {
            logger.error("写入实时数据到Lindorm失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to write real-time data to Lindorm", e);
        }

    }

    private String updateWeatherTimeDataSql(Double longitude, Double latitude, String time ,String deviceSn, Long vppId) {
        List<String> metaVarsName = new ArrayList<>();
        List<String> metaVarsValue = new ArrayList<>();
        List<String> metaTypes = datasetMetaInfoService.selectAllValidMetaType();
        for (String metaType : metaTypes) {
            List<String> metaVars = datasetMetaInfoService.selectMetaVarByMetaType(metaType);
            List<Double> metaInfo = weatherDataService.getWeatherDataByKey(longitude, latitude, time, metaType);
            if (null == metaInfo) {
                return null;
            }
            metaVarsName.addAll(metaVars.stream().map(it -> String.format("%s_%s",metaType,it)).collect(Collectors.toList()));
            metaVarsValue.addAll(metaInfo.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        String key = " longitude, latitude ,time ,device_sn, vpp_id ";
        String keyVal = String.format(" '%s', '%s' , '%s' ,'%s' , '%s' ",longitude,latitude,time,deviceSn ,vppId);

        String updateSQL = String.format(
                "insert into %s (%s, %s) values (%s ,%s)",
                TABLE_NAME, String.join(",", metaVarsName), key, String.join(",",metaVarsValue) , keyVal
        );
        return updateSQL;
    }

    public List<DeviceRealTimeData> queryBySNTimeRange(String inverterSn, long startTime, long endTime) {
        // 按照lindorm.md的示例，使用绑定参数和时间范围查询
        String sql = String.format(
                "SELECT device_sn, time, latitude, longitude, active_power FROM %s WHERE device_sn = ? and time >= ? AND time <= ? ORDER BY time DESC",
                TABLE_NAME
        );

        try (PreparedStatement pstmt = LindormConfig.connection.prepareStatement(sql)) {
            pstmt.setString(1, inverterSn);
            pstmt.setTimestamp(2, new Timestamp(startTime));
            pstmt.setTimestamp(3, new Timestamp(endTime));
            try (ResultSet rs = pstmt.executeQuery()) {
                return parseResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("根据时间范围查询数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private List<DeviceRealTimeData> parseResultSet(ResultSet rs) throws SQLException {
        List<DeviceRealTimeData> results = new ArrayList<>();

        while (rs.next()) {
            DeviceRealTimeData data = new DeviceRealTimeData();
            data.setDeviceSn(rs.getString("inverter_sn"));
            Timestamp timestamp = rs.getTimestamp("time");
            if (timestamp != null) {
                data.setDeviceTime(timestamp.toLocalDateTime());
            }
            data.setLatitude(rs.getDouble("latitude"));
            data.setLongitude(rs.getDouble("longitude"));
            data.setActivePower(rs.getDouble("active_power"));
            results.add(data);
        }

        return results;
    }

}
