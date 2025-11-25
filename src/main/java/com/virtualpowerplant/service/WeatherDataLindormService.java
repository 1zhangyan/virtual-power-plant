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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@DependsOn(value = "DatasetMetaInfoService")
public class WeatherDataLindormService {

    @Autowired
    DatasetMetaInfoService datasetMetaInfoService;

    private static final Logger logger = LoggerFactory.getLogger(WeatherDataLindormService.class);

    private static String getTableName(String metaType) {
        return String.format("weather_forest_v1_%s", metaType);
    }

    /**
     * 根据 TerraqtForestData 写入天气数据
     **/
    public void writeWeatherData(TerraqtForestData terraqtForestData) {
        if (LindormConfig.connection == null) {
            throw new RuntimeException("Lindorm 连接为空！");
        }
        if (terraqtForestData == null || terraqtForestData.getTimestamps() == null) {
            logger.warn("天气数据列表为空，跳过写入");
            return;
        }
        try (Statement stmt = LindormConfig.connection.createStatement()) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<String> originalTimestampStrings = terraqtForestData.getTimestamps();
            List<List<Double>> originalValues = terraqtForestData.getMetricValues();
            if (originalTimestampStrings.isEmpty()) {
                return;
            }
            TreeMap<LocalDateTime, List<Double>> dataMap = new TreeMap<>();
            for (int i = 0; i < originalTimestampStrings.size(); i++) {
                LocalDateTime dateTime = LocalDateTime.parse(originalTimestampStrings.get(i), formatter);
                dataMap.put(dateTime, originalValues.get(i));
            }
            LocalDateTime startTime = dataMap.firstKey().truncatedTo(ChronoUnit.HOURS);
            LocalDateTime endTime = dataMap.firstKey().plusDays(2).truncatedTo(ChronoUnit.HOURS);
            if (!dataMap.lastKey().equals(endTime)) {
                endTime = endTime.plusHours(1);
            }
            LocalDateTime current = startTime;
            while (!current.isAfter(endTime)) {
                Map.Entry<LocalDateTime, List<Double>> floorEntry = dataMap.floorEntry(current);
                List<Double> valueToUse = floorEntry != null ? floorEntry.getValue() : dataMap.firstEntry().getValue();
                String valueStr = valueToUse.stream()
                        .map(it -> it == null ? "0.0" : it.toString())
                        .collect(Collectors.joining(","));

                String insertSQL = String.format(
                        "INSERT INTO %s (time,longitude,latitude,%s) VALUES('%s','%s','%s',%s)",
                        getTableName(terraqtForestData.getMetaType()),
                        String.join(",", terraqtForestData.getMetricVars()),
                        Timestamp.valueOf(current),
                        terraqtForestData.getLocation().get(0),
                        terraqtForestData.getLocation().get(1),
                        valueStr
                );
                current = current.plusHours(1);
                try {
                    stmt.execute(insertSQL);
                } catch (Exception e) {
                    throw new RuntimeException("写入天气数据到Lindorm失败:" + insertSQL);
                }
            }
            stmt.executeBatch();
            stmt.clearBatch();
        } catch (
                Exception e) {
            throw new RuntimeException("Failed to write inverter weather data to Lindorm", e);
        }
    }

    /**
     * 根据坐标和时间查询天气数据
     * 坐标：精确到个位数
     * 时间：取当前小时
     **/
    public List<SimpleWeatherForestData> selectWeatherData(Double longitude, Double latitude, String timeStr, String metaType) {

        List<SimpleWeatherForestData> result = new ArrayList<>();
        String querySQL = String.format(
                "SELECT * FROM %s WHERE longitude = ? AND latitude = ? AND time = ?",
                getTableName(metaType)
        );
        try (PreparedStatement pstmt = LindormConfig.connection.prepareStatement(querySQL)) {
            pstmt.setString(1, longitude.toString());
            pstmt.setString(2, latitude.toString());
            pstmt.setTimestamp(3, Timestamp.valueOf(TimeUtils.getHourStart(timeStr)));
            List<SimpleWeatherForestData> simpleWeatherForestDatas = new ArrayList<>();
            List<String> metaVars = datasetMetaInfoService.selectMetaVarByMetaType(metaType);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String time = rs.getTimestamp("time").toString();
                SimpleWeatherForestData simpleWeatherForestData = new SimpleWeatherForestData(time, metaType);
                for (String metaVar : metaVars) {
                    simpleWeatherForestData.getMetricValues().add(rs.getDouble(metaVar));
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

}
