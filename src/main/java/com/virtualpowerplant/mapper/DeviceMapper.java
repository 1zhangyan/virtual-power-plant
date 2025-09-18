package com.virtualpowerplant.mapper;

import com.virtualpowerplant.model.Device;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DeviceMapper {

    /**
     * 插入或更新设备信息 (使用UUID作为唯一键)
     */
    int insertOrUpdate(Device device);

    /**
     * 查询所有带经纬度信息的逆变器
     */
    @Select("SELECT * FROM vpp.sungrow_device WHERE device_type = 1  ORDER BY device_name")
    List<Device> selectInvertersWithCoordinates();
}