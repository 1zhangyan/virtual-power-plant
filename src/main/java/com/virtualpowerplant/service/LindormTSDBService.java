package com.virtualpowerplant.service;

import com.virtualpowerplant.config.SecretConfigManager;
import com.virtualpowerplant.model.InverterRealTimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LindormTSDBService {

    private static final Logger logger = LoggerFactory.getLogger(LindormTSDBService.class);

    private Connection connection;
    private static final String TABLE_NAME = "inverter_realtime";

    @PostConstruct
    public void init() {
        try {
            String url = SecretConfigManager.getLindormUrl();
            if (url == null || url.isEmpty()) {
                throw new RuntimeException("Lindorm URL not configured in secret.config");
            }

            logger.info("初始化Lindorm TSDB JDBC连接，URL: {}", url);
            connection = DriverManager.getConnection(url);

            // 创建表（如果不存在）
            initTable();
            logger.info("Lindorm TSDB JDBC连接初始化完成");
        } catch (Exception e) {
            logger.error("初始化Lindorm TSDB JDBC连接失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Lindorm TSDB JDBC connection", e);
        }
    }

    private void initTable() {
        try {
            // 创建表，按照lindorm.md的格式，使用TIMESTAMP而不是BIGINT
            String createTableSQL = String.format(
                    "CREATE TABLE IF NOT EXISTS %s (" +
                            "ps_name VARCHAR TAG, " +
                            "ps_key VARCHAR TAG, " +
                            "inverter_sn VARCHAR TAG, " +
                            "time TIMESTAMP, " +
                            "latitude DOUBLE, " +
                            "longitude DOUBLE, " +
                            "active_power DOUBLE, " +
                            "PRIMARY KEY(inverter_sn)" +
                            ")",
                    TABLE_NAME
            );

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL);
                logger.info("表 {} 创建或确认存在", TABLE_NAME);
            }
        } catch (Exception e) {
            logger.error("初始化表失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize table", e);
        }
    }

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

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
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

    public List<InverterRealTimeData> queryLatestData(int limit) {
        String sql = String.format(
                "SELECT ps_name, ps_key, inverter_sn, time, latitude, longitude, active_power FROM %s ORDER BY time DESC LIMIT %d",
                TABLE_NAME, limit
        );

        return executeQuery(sql);
    }

    public List<InverterRealTimeData> queryByInverterSn(String inverterSn, int limit) {
        String sql = String.format(
                "SELECT ps_name, ps_key, inverter_sn, time, latitude, longitude, active_power FROM %s WHERE inverter_sn = ? ORDER BY time DESC LIMIT %d",
                TABLE_NAME, limit
        );

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, inverterSn);
            try (ResultSet rs = pstmt.executeQuery()) {
                return parseResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("根据逆变器序列号查询数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> queryByTimeRange(long startTime, long endTime) {
        // 按照lindorm.md的示例，使用绑定参数和时间范围查询
        String sql = String.format(
                "SELECT ps_name, ps_key, inverter_sn, time, latitude, longitude, active_power FROM %s WHERE time >= ? AND time <= ? ORDER BY time DESC",
                TABLE_NAME
        );

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(startTime));
            pstmt.setTimestamp(2, new Timestamp(endTime));
            try (ResultSet rs = pstmt.executeQuery()) {
                return parseResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("根据时间范围查询数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> queryByPowerStation(String psKey, int limit) {
        String sql = String.format(
                "SELECT ps_name, ps_key, inverter_sn, time, latitude, longitude, active_power FROM %s WHERE ps_key = ? ORDER BY time DESC LIMIT %d",
                TABLE_NAME, limit
        );

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, psKey);
            try (ResultSet rs = pstmt.executeQuery()) {
                return parseResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("根据电站查询数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private List<InverterRealTimeData> executeQuery(String sql) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return parseResultSet(rs);
        } catch (SQLException e) {
            logger.error("执行查询失败: {}", e.getMessage(), e);
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

            // 按照lindorm.md的格式，处理时间戳
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

    @PreDestroy
    public void shutdown() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Lindorm TSDB JDBC连接已关闭");
            } catch (SQLException e) {
                logger.error("关闭Lindorm TSDB JDBC连接失败: {}", e.getMessage(), e);
            }
        }
    }
}