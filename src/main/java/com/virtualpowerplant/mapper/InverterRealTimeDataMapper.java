package com.virtualpowerplant.mapper;

import com.virtualpowerplant.model.InverterRealTimeData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface InverterRealTimeDataMapper {

    int insertBatch(@Param("dataList") List<InverterRealTimeData> dataList);

    @Select("SELECT * FROM vpp.inverter_realtime_data WHERE inverter_sn = #{inverterSn} " +
            "AND device_time >= #{startTime} AND device_time <= #{endTime} " +
            "ORDER BY device_time DESC")
    List<InverterRealTimeData> selectByInverterAndTimeRange(
            @Param("inverterSn") String inverterSn,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM vpp.inverter_realtime_data WHERE device_time >= #{startTime} " +
            "AND device_time <= #{endTime} ORDER BY device_time DESC")
    List<InverterRealTimeData> selectByTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM vpp.inverter_realtime_data WHERE inverter_sn = #{inverterSn} " +
            "ORDER BY device_time DESC LIMIT #{limit}")
    List<InverterRealTimeData> selectLatestByInverter(
            @Param("inverterSn") String inverterSn,
            @Param("limit") int limit);

    @Select("SELECT * FROM vpp.inverter_realtime_data ORDER BY device_time DESC LIMIT #{limit}")
    List<InverterRealTimeData> selectLatest(@Param("limit") int limit);

    @Select("SELECT * FROM vpp.inverter_realtime_data WHERE ps_id = #{psId} " +
            "ORDER BY device_time DESC LIMIT #{limit}")
    List<InverterRealTimeData> selectLatestByPowerStation(
            @Param("psId") String psId,
            @Param("limit") int limit);
}