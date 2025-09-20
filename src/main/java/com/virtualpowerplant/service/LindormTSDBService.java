package com.virtualpowerplant.service;

import com.aliyun.lindorm.tsdb.client.ClientOptions;
import com.aliyun.lindorm.tsdb.client.LindormTSDBClient;
import com.aliyun.lindorm.tsdb.client.LindormTSDBFactory;
import com.aliyun.lindorm.tsdb.client.exception.LindormTSDBException;
import com.aliyun.lindorm.tsdb.client.model.QueryResult;
import com.aliyun.lindorm.tsdb.client.model.Record;
import com.aliyun.lindorm.tsdb.client.model.ResultSet;
import com.aliyun.lindorm.tsdb.client.model.WriteResult;
import com.aliyun.lindorm.tsdb.client.utils.ExceptionUtils;
import com.virtualpowerplant.config.SecretConfigManager;
import com.virtualpowerplant.model.InverterRealTimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LindormTSDBService {

    private static final Logger logger = LoggerFactory.getLogger(LindormTSDBService.class);

    private LindormTSDBClient lindormTSDBClient;
    private static final String DATABASE_NAME = "vpp";
    private static final String TABLE_NAME = "inverter_realtime";

    @PostConstruct
    public void init() {
        try {
            String url = SecretConfigManager.getLindormUrl();
            if (url == null || url.isEmpty()) {
                throw new RuntimeException("Lindorm URL not configured in secret.config");
            }

            logger.info("初始化Lindorm TSDB客户端，URL: {}", url);
            ClientOptions options = ClientOptions.newBuilder(url).build();
            lindormTSDBClient = LindormTSDBFactory.connect(options);

            // 创建数据库和表（如果不存在）
            initDatabaseAndTable();
            logger.info("Lindorm TSDB客户端初始化完成");
        } catch (Exception e) {
            logger.error("初始化Lindorm TSDB客户端失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Lindorm TSDB client", e);
        }
    }

    private void initDatabaseAndTable() {
        try {
            // 创建数据库
            lindormTSDBClient.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            logger.info("数据库 {} 创建或确认存在", DATABASE_NAME);

            // 创建表
            String createTableSQL = String.format(
                    "CREATE TABLE IF NOT EXISTS %s (" +
                            "ps_name VARCHAR TAG, " +
                            "ps_key VARCHAR TAG, " +
                            "inverter_sn VARCHAR TAG, " +
                            "time BIGINT, " +
                            "latitude DOUBLE, " +
                            "longitude DOUBLE, " +
                            "active_power DOUBLE, " +
                            "PRIMARY KEY(inverter_sn)" +
                            ")",
                    TABLE_NAME
            );

            lindormTSDBClient.execute(DATABASE_NAME, createTableSQL);
            logger.info("表 {} 创建或确认存在", TABLE_NAME);
        } catch (Exception e) {
            logger.error("初始化数据库和表失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize database and table", e);
        }
    }

    public void writeRealTimeData(List<InverterRealTimeData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            logger.warn("实时数据列表为空，跳过写入");
            return;
        }

        try {
            List<Record> records = new ArrayList<>();

            for (InverterRealTimeData data : dataList) {
                if (data.getInverterSn() == null || data.getInverterSn().isEmpty()) {
                    logger.warn("逆变器序列号为空，跳过该条记录: {}", data);
                    continue;
                }

                // 将LocalDateTime转换为毫秒时间戳
                long timestamp = data.getDeviceTime() != null ?
                    data.getDeviceTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() :
                    System.currentTimeMillis();

                Record record = Record.table(TABLE_NAME)
                        .time(timestamp)
                        .tag("inverter_sn", data.getInverterSn())
                        .tag("ps_name", data.getPsName() != null ? data.getPsName() : "unknown")
                        .tag("ps_key", data.getPsKey() != null ? data.getPsKey() : "unknown")
                        .addField("latitude", data.getLatitude() != null ? data.getLatitude() : 0.0)
                        .addField("longitude", data.getLongitude() != null ? data.getLongitude() : 0.0)
                        .addField("active_power", data.getActivePower() != null ? data.getActivePower() : 0.0)
                        .build();

                records.add(record);
            }

            if (records.isEmpty()) {
                logger.warn("没有有效的记录可写入");
                return;
            }

            // 异步写入
            CompletableFuture<WriteResult> future = lindormTSDBClient.write(DATABASE_NAME, records);

            // 处理写入结果
            future.whenComplete((result, exception) -> {
                if (exception != null) {
                    logger.error("写入Lindorm失败", exception);
                    Throwable rootCause = ExceptionUtils.getRootCause(exception);
                    if (rootCause instanceof LindormTSDBException) {
                        LindormTSDBException e = (LindormTSDBException) rootCause;
                        logger.error("Lindorm错误码: {}, SQL状态: {}, 错误消息: {}",
                                e.getCode(), e.getSqlstate(), e.getMessage());
                    }
                } else {
                    logger.info("成功写入 {} 条实时数据到Lindorm", records.size());
                }
            });

            // 等待写入完成
            future.join();

        } catch (Exception e) {
            logger.error("写入实时数据到Lindorm失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to write real-time data to Lindorm", e);
        }
    }

    public List<InverterRealTimeData> queryLatestData(int limit) {
        try {
            String sql = String.format(
                    "SELECT * FROM %s ORDER BY time DESC LIMIT %d",
                    TABLE_NAME, limit
            );

            return executeQuery(sql);
        } catch (Exception e) {
            logger.error("查询最新数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> queryByInverterSn(String inverterSn, int limit) {
        try {
            String sql = String.format(
                    "SELECT * FROM %s WHERE inverter_sn = '%s' ORDER BY time DESC LIMIT %d",
                    TABLE_NAME, inverterSn, limit
            );

            return executeQuery(sql);
        } catch (Exception e) {
            logger.error("根据逆变器序列号查询数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> queryByTimeRange(long startTime, long endTime) {
        try {
            String sql = String.format(
                    "SELECT * FROM %s WHERE time >= %d AND time <= %d ORDER BY time DESC",
                    TABLE_NAME, startTime, endTime
            );

            return executeQuery(sql);
        } catch (Exception e) {
            logger.error("根据时间范围查询数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<InverterRealTimeData> queryByPowerStation(String psKey, int limit) {
        try {
            String sql = String.format(
                    "SELECT * FROM %s WHERE ps_key = '%s' ORDER BY time DESC LIMIT %d",
                    TABLE_NAME, psKey, limit
            );

            return executeQuery(sql);
        } catch (Exception e) {
            logger.error("根据电站查询数据失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private List<InverterRealTimeData> executeQuery(String sql) {
        List<InverterRealTimeData> results = new ArrayList<>();

        try (ResultSet resultSet = lindormTSDBClient.query(DATABASE_NAME, sql)) {
            QueryResult result;
            while ((result = resultSet.next()) != null) {
                List<String> columns = result.getColumns();
                List<List<Object>> rows = result.getRows();

                for (List<Object> row : rows) {
                    InverterRealTimeData data = new InverterRealTimeData();

                    for (int i = 0; i < columns.size(); i++) {
                        String column = columns.get(i);
                        Object value = row.get(i);

                        switch (column) {
                            case "inverter_sn":
                                data.setInverterSn(value != null ? value.toString() : null);
                                break;
                            case "ps_name":
                                data.setPsName(value != null ? value.toString() : null);
                                break;
                            case "ps_key":
                                data.setPsKey(value != null ? value.toString() : null);
                                break;
                            case "time":
                                if (value instanceof Number) {
                                    long timestamp = ((Number) value).longValue();
                                    data.setDeviceTime(LocalDateTime.ofInstant(
                                            java.time.Instant.ofEpochMilli(timestamp),
                                            ZoneId.systemDefault()
                                    ));
                                }
                                break;
                            case "latitude":
                                if (value instanceof Number) {
                                    data.setLatitude(((Number) value).doubleValue());
                                }
                                break;
                            case "longitude":
                                if (value instanceof Number) {
                                    data.setLongitude(((Number) value).doubleValue());
                                }
                                break;
                            case "active_power":
                                if (value instanceof Number) {
                                    data.setActivePower(((Number) value).doubleValue());
                                }
                                break;
                        }
                    }

                    results.add(data);
                }
            }
        } catch (Exception e) {
            logger.error("执行查询失败: {}", e.getMessage(), e);
        }

        return results;
    }

    @PreDestroy
    public void shutdown() {
        if (lindormTSDBClient != null) {
            try {
                lindormTSDBClient.shutdown();
                logger.info("Lindorm TSDB客户端已关闭");
            } catch (Exception e) {
                logger.error("关闭Lindorm TSDB客户端失败: {}", e.getMessage(), e);
            }
        }
    }
}