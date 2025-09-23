package com.virtualpowerplant.service;

import com.virtualpowerplant.config.SecretConfigManager;
import com.virtualpowerplant.model.InverterWeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 逆变器天气数据的 Lindorm TSDB 服务
 * 按照 lindorm.md 规范使用 JDBC 方式操作时序数据库
 */
@Service
public class InverterGfsSurfaceLindormService {

    private static final Logger logger = LoggerFactory.getLogger(InverterGfsSurfaceLindormService.class);

    private Connection connection;
    private static final String TABLE_NAME = "gfs_surface_forecast";

    @PostConstruct
    public void init() {
        try {
            String url = SecretConfigManager.getLindormUrl();
            if (url == null || url.isEmpty()) {
                logger.warn("Lindorm URL not configured, InverterWeatherLindormService will be disabled");
                return;
            }

            logger.info("初始化逆变器天气数据 Lindorm TSDB JDBC连接，URL: {}", url);
            connection = DriverManager.getConnection(url);

            // 创建逆变器天气数据表
            initInverterWeatherTable();
            logger.info("逆变器天气数据 Lindorm TSDB JDBC连接初始化完成");
        } catch (Exception e) {
            logger.error("初始化逆变器天气数据 Lindorm TSDB JDBC连接失败: {}", e.getMessage(), e);
            // 不抛出异常，允许系统在没有 Lindorm 的情况下运行
        }
    }

    private void initInverterWeatherTable() {
        try {
            // 创建逆变器天气数据表
            String createTableSQL = String.format(
                    "CREATE TABLE IF NOT EXISTS %s (" +
                            "ps_name VARCHAR TAG, " +
                            "ps_key VARCHAR TAG, " +
                            "device_sn VARCHAR TAG, " +
                            "time TIMESTAMP, " +
                            "tcc DOUBLE, " +           // 总云量 %
                            "lcc DOUBLE, " +           // 低云量 %
                            "mcc DOUBLE, " +           // 中云量 %
                            "hcc DOUBLE, " +           // 高云量 %
                            "dswrf DOUBLE, " +         // 向下短波辐射 W/m^2
                            "dlwrf DOUBLE, " +         // 向下长波辐射 W/m^2
                            "uswrf DOUBLE, " +         // 向上短波辐射 W/m^2
                            "ulwrf DOUBLE, " +         // 向上长波辐射 W/m^2
                            "PRIMARY KEY(device_sn)" +
                            ")",
                    TABLE_NAME
            );

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL);
                logger.info("逆变器天气数据表 {} 创建或确认存在", TABLE_NAME);
            }
        } catch (Exception e) {
            logger.error("初始化逆变器天气数据表失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize inverter weather table", e);
        }
    }

    /**
     * 批量写入逆变器天气数据到 Lindorm
     */
    public void writeInverterWeatherData(List<InverterWeatherData> dataList) {
        if (connection == null) {
            logger.warn("Lindorm连接未初始化，跳过逆变器天气数据写入");
            return;
        }

        if (dataList == null || dataList.isEmpty()) {
            logger.warn("逆变器天气数据列表为空，跳过写入");
            return;
        }

        // 按照 lindorm.md 的批量插入模式
        String insertSQL = String.format(
            "INSERT INTO %s(ps_name, ps_key, device_sn, time, tcc, lcc, mcc, hcc, dswrf, dlwrf, uswrf, ulwrf) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            TABLE_NAME
        );

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            int batchCount = 0;

            for (InverterWeatherData data : dataList) {
                if (data.getDeviceSn() == null || data.getDeviceSn().isEmpty() || data.getTime() == null) {
                    logger.warn("逆变器天气数据缺少必要字段，跳过该条记录: {}", data);
                    continue;
                }

                // 设置参数，遵循 lindorm.md 的时间戳格式
                pstmt.setString(1, data.getPsName() != null ? data.getPsName() : "unknown");
                pstmt.setString(2, data.getPsKey() != null ? data.getPsKey() : "unknown");
                pstmt.setString(3, data.getDeviceSn());
                pstmt.setTimestamp(4, Timestamp.valueOf(data.getTime()));
                pstmt.setDouble(5, data.getTcc() != null ? data.getTcc() : 0.0);
                pstmt.setDouble(6, data.getLcc() != null ? data.getLcc() : 0.0);
                pstmt.setDouble(7, data.getMcc() != null ? data.getMcc() : 0.0);
                pstmt.setDouble(8, data.getHcc() != null ? data.getHcc() : 0.0);
                pstmt.setDouble(9, data.getDswrf() != null ? data.getDswrf() : 0.0);
                pstmt.setDouble(10, data.getDlwrf() != null ? data.getDlwrf() : 0.0);
                pstmt.setDouble(11, data.getUswrf() != null ? data.getUswrf() : 0.0);
                pstmt.setDouble(12, data.getUlwrf() != null ? data.getUlwrf() : 0.0);

                pstmt.addBatch();
                batchCount++;
            }

            if (batchCount > 0) {
                // 执行批量插入
                pstmt.executeBatch();
                logger.info("成功写入 {} 条逆变器天气数据到Lindorm", batchCount);
            } else {
                logger.warn("没有有效的逆变器天气数据记录可写入");
            }

        } catch (SQLException e) {
            logger.error("写入逆变器天气数据到Lindorm失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to write inverter weather data to Lindorm", e);
        }
    }

    /**
     * 根据逆变器SN和时间范围查询天气预报数据
     */
    public List<InverterWeatherData> getInverterWeatherByTimeRange(String inverterSn, LocalDateTime startTime, LocalDateTime endTime) {
        if (connection == null) {
            logger.warn("Lindorm连接未初始化，无法查询逆变器天气数据");
            return new ArrayList<>();
        }

        List<InverterWeatherData> result = new ArrayList<>();
        String querySQL = String.format(
            "SELECT ps_name, ps_key, device_sn, time, tcc, lcc, mcc, hcc, dswrf, dlwrf, uswrf, ulwrf " +
            "FROM %s WHERE device_sn = ? AND time >= ? AND time <= ? ORDER BY time",
            TABLE_NAME
        );

        try (PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
            pstmt.setString(1, inverterSn);
            pstmt.setTimestamp(2, Timestamp.valueOf(startTime));
            pstmt.setTimestamp(3, Timestamp.valueOf(endTime));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                InverterWeatherData data = new InverterWeatherData();
                data.setPsName(rs.getString("ps_name"));
                data.setPsKey(rs.getString("ps_key"));
                data.setDeviceSn(rs.getString("device_sn"));
                data.setTime(rs.getTimestamp("time").toLocalDateTime());
                data.setTcc(rs.getDouble("tcc"));
                data.setLcc(rs.getDouble("lcc"));
                data.setMcc(rs.getDouble("mcc"));
                data.setHcc(rs.getDouble("hcc"));
                data.setDswrf(rs.getDouble("dswrf"));
                data.setDlwrf(rs.getDouble("dlwrf"));
                data.setUswrf(rs.getDouble("uswrf"));
                data.setUlwrf(rs.getDouble("ulwrf"));
                result.add(data);
            }

            logger.debug("查询到 {} 条逆变器天气数据, 逆变器SN: {}", result.size(), inverterSn);
        } catch (SQLException e) {
            logger.error("查询逆变器天气数据失败: {}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * 根据电站PS_KEY和时间范围查询天气预报数据
     */
    public List<InverterWeatherData> getPowerStationWeatherByTimeRange(String psKey, LocalDateTime startTime, LocalDateTime endTime) {
        if (connection == null) {
            logger.warn("Lindorm连接未初始化，无法查询电站天气数据");
            return new ArrayList<>();
        }

        List<InverterWeatherData> result = new ArrayList<>();
        String querySQL = String.format(
            "SELECT ps_name, ps_key, device_sn, time, tcc, lcc, mcc, hcc, dswrf, dlwrf, uswrf, ulwrf " +
            "FROM %s WHERE ps_key = ? AND time >= ? AND time <= ? ORDER BY time",
            TABLE_NAME
        );

        try (PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
            pstmt.setString(1, psKey);
            pstmt.setTimestamp(2, Timestamp.valueOf(startTime));
            pstmt.setTimestamp(3, Timestamp.valueOf(endTime));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                InverterWeatherData data = new InverterWeatherData();
                data.setPsName(rs.getString("ps_name"));
                data.setPsKey(rs.getString("ps_key"));
                data.setDeviceSn(rs.getString("device_sn"));
                data.setTime(rs.getTimestamp("time").toLocalDateTime());
                data.setTcc(rs.getDouble("tcc"));
                data.setLcc(rs.getDouble("lcc"));
                data.setMcc(rs.getDouble("mcc"));
                data.setHcc(rs.getDouble("hcc"));
                data.setDswrf(rs.getDouble("dswrf"));
                data.setDlwrf(rs.getDouble("dlwrf"));
                data.setUswrf(rs.getDouble("uswrf"));
                data.setUlwrf(rs.getDouble("ulwrf"));
                result.add(data);
            }

            logger.debug("查询到 {} 条电站天气数据, 电站PS_KEY: {}", result.size(), psKey);
        } catch (SQLException e) {
            logger.error("查询电站天气数据失败: {}", e.getMessage(), e);
        }

        return result;
    }

    @PreDestroy
    public void shutdown() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("逆变器天气数据 Lindorm TSDB JDBC连接已关闭");
            } catch (SQLException e) {
                logger.error("关闭逆变器天气数据 Lindorm TSDB JDBC连接失败: {}", e.getMessage(), e);
            }
        }
    }
}