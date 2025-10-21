package com.virtualpowerplant.service;


import com.virtualpowerplant.config.LindormConfig;
import com.virtualpowerplant.model.InverterRealTimeData;
import com.virtualpowerplant.model.SimpleWeatherForestData;
import com.virtualpowerplant.model.WeatherForestData;
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


    private static final String TABLE_NAME = "inverter_realtime";


    public void writeRealTimeData(List<InverterRealTimeData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            logger.warn("实时数据列表为空，跳过写入");
            return;
        }
        // 按照lindorm.md的示例，使用批量插入
        String insertSQL = String.format(
                "INSERT INTO %s(ps_name, ps_key, inverter_sn, time, latitude, longitude, active_power) VALUES(?, ?, ?, ?, ?, ?, ?)",
                TABLE_NAME
        );

        try (PreparedStatement pstmt = LindormConfig.connection.prepareStatement(insertSQL)) {
            int batchCount = 0;

            for (InverterRealTimeData data : dataList) {
                if (data.getInverterSn() == null || data.getInverterSn().isEmpty()) {
                    logger.warn("逆变器序列号为空，跳过该条记录: {}", data);
                    continue;
                }

                // 将LocalDateTime转换为Timestamp，遵循lindorm.md的时间格式
                Timestamp timestamp = data.getDeviceTime() != null ?
                        Timestamp.valueOf(data.getDeviceTime()) :
                        new Timestamp(System.currentTimeMillis());

                pstmt.setString(1, data.getPsName() != null ? data.getPsName() : "unknown");
                pstmt.setString(2, data.getPsKey() != null ? data.getPsKey() : "unknown");
                pstmt.setString(3, data.getInverterSn());
                pstmt.setTimestamp(4, timestamp);
                pstmt.setDouble(5, data.getLatitude() != null ? data.getLatitude() : 0.0);
                pstmt.setDouble(6, data.getLongitude() != null ? data.getLongitude() : 0.0);
                pstmt.setDouble(7, data.getActivePower() != null ? data.getActivePower() : 0.0);

                pstmt.addBatch();
                batchCount++;
            }

            if (batchCount > 0) {
                // 执行批量插入，遵循lindorm.md的批量操作方式
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

    public List<InverterRealTimeData> queryBySNTimeRange(String inverterSn, long startTime, long endTime) {
        // 按照lindorm.md的示例，使用绑定参数和时间范围查询
        String sql = String.format(
                "SELECT ps_name, ps_key, inverter_sn, time, latitude, longitude, active_power FROM %s WHERE inverter_sn = ? and time >= ? AND time <= ? ORDER BY time DESC",
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

    private List<InverterRealTimeData> parseResultSet(ResultSet rs) throws SQLException {
        List<InverterRealTimeData> results = new ArrayList<>();

        while (rs.next()) {
            InverterRealTimeData data = new InverterRealTimeData();

            data.setPsName(rs.getString("ps_name"));
            data.setPsKey(rs.getString("ps_key"));
            data.setInverterSn(rs.getString("inverter_sn"));

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
