package com.virtualpowerplant.service;


import com.virtualpowerplant.config.LindormConfig;
import com.virtualpowerplant.model.SimpleWeatherForestData;
import com.virtualpowerplant.model.TerraqtForestData;
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
public class WeatherDataLindormService {

    @Autowired
    DatasetMetaInfoService datasetMetaInfoService;

    private static final Logger logger = LoggerFactory.getLogger(WeatherDataLindormService.class);

    public void initWeatherTable() {
        List<String> metaTypes = datasetMetaInfoService.selectAllValidMetaType();
        for (String metaType : metaTypes) {
            List<String> metaVars = datasetMetaInfoService.selectMetaVarByMetaType(metaType);
            String createTableSQL = String.format(
                    "CREATE TABLE IF NOT EXISTS %s (" +
                            "longitude varchar TAG, " +
                            "latitude varchar TAG, " +
                            "time TIMESTAMP, %s" +
                            ",PRIMARY KEY (longitude,latitude) )",
                    getTableName(metaType),
                    String.join(",", metaVars.stream().map(it -> String.format("%s Double", it)).collect(Collectors.toList()))
            );
            try (Statement stmt = LindormConfig.connection.createStatement()) {
                stmt.execute(createTableSQL);
                logger.info("天气数据表 {} 创建或确认存在", getTableName(metaType));
            } catch (Exception e) {
                logger.error("初始化天气数据表失败SQL: {}", createTableSQL);
                logger.error("初始化天气数据表失败: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to initialize inverter weather table", e);
            }
        }
    }

    private static String getTableName(String metaType) {
        return String.format("weather_forest_v1_%s", metaType);
    }

    /**
     * 根据 TerraqtForestData 写入天气数据
     **/
    public void writeWeatherData(TerraqtForestData weatherForestData) {
        if (LindormConfig.connection == null) {
            logger.warn("Lindorm连接未初始化，跳过天气数据写入");
            return;
        }
        if (weatherForestData == null || weatherForestData.getTimestamps() == null) {
            logger.warn("天气数据列表为空，跳过写入");
            return;
        }
        try (Statement stmt = LindormConfig.connection.createStatement()) {
            for (int i = 0; i < weatherForestData.getTimestamps().size(); i++) {
                String insertSQL = String.format(
                        "INSERT INTO %s (time,longitude,latitude,%s) VALUES('%s','%s','%s',%s)",
                        getTableName(weatherForestData.getMetaType()),
                        String.join(",", weatherForestData.getMetricVars()),
                        Timestamp.valueOf(weatherForestData.getTimestamps().get(i)),
                        weatherForestData.getLocation().get(0),
                        weatherForestData.getLocation().get(1),
                        String.join(",", weatherForestData.getMetricValues().get(i).stream().map(Object::toString).collect(Collectors.toList()))
                );
                stmt.execute(insertSQL);
            }
            stmt.executeBatch();
            stmt.clearBatch();
        } catch (Exception e) {
            logger.error("写入天气数据到Lindorm失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to write inverter weather data to Lindorm", e);
        }

    }

    /**
     * 根据坐标和时间查询天气数据
     * 坐标：精确到个位数
     * 时间：取当前小时
     **/
    public List<SimpleWeatherForestData> selectWeatherData(Double longitude, Double latitude, String startTime, String endTime, String metaType) {

        List<SimpleWeatherForestData> result = new ArrayList<>();
        String querySQL = String.format(
                "SELECT * FROM %s WHERE longitude = ? AND latitude = ? AND time >= ? and time <= ?",
                getTableName(metaType)
        );
        try (PreparedStatement pstmt = LindormConfig.connection.prepareStatement(querySQL)) {
            pstmt.setString(1, String.valueOf(Math.round(longitude) * 1.0));
            pstmt.setString(2, String.valueOf(Math.round(latitude) * 1.0));
            pstmt.setTimestamp(3, Timestamp.valueOf(startTime));
            pstmt.setTimestamp(4, Timestamp.valueOf(endTime));

            List<SimpleWeatherForestData> simpleWeatherForestDatas = new ArrayList<>();
            List<String> metaVars = datasetMetaInfoService.selectMetaVarByMetaType(metaType);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String time = rs.getTimestamp("time").toString();
                SimpleWeatherForestData simpleWeatherForestData = new SimpleWeatherForestData(time, metaType);
                for (String metaVar : metaVars) {
                    simpleWeatherForestData.getMetricValues().add( rs.getDouble(metaVar));
                    simpleWeatherForestData.getMetricVars().add(metaVar);
                }
                simpleWeatherForestDatas.add(simpleWeatherForestData);
            }
            return simpleWeatherForestDatas;
        } catch (SQLException e) {
            logger.error("查询天气数据失败: longitude {},latitude {}, metaType {} "
                    , metaType
                    , e);
            return null;
        }
    }

//    public List<SimpleWeatherForestData> selectWeatherData(Double longitude, Double latitude, String startTime, String endTime, String metaType) {
//        return selectWeatherData(longitude, latitude, startTime, endTime, metaType);
//    }

}
