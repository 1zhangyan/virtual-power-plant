package com.virtualpowerplant.mapper;

import com.virtualpowerplant.model.SungrowDevice;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DeviceMapper {

    /**
     * 插入或更新设备信息 (使用UUID作为唯一键)
     */
    int insertOrUpdate(SungrowDevice device);

    /**
     * 查询所有带经纬度信息的逆变器
     */
    @Select("SELECT * FROM vpp.sungrow_device WHERE device_type = 1  ORDER BY device_name")
    List<SungrowDevice> selectInverters();

    /**
     * 根据vppId查询逆变器设备
     */
    @Select("SELECT * FROM vpp.sungrow_device WHERE device_type = 1 AND vpp_id = #{vppId} ORDER BY device_name")
    List<SungrowDevice> selectInvertersByVppId(@Param("vppId") Long vppId);
}