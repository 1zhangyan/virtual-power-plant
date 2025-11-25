package com.virtualpowerplant.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.virtualpowerplant.model.VppDevice;
@Mapper
@Repository
public interface VppDeviceMapper {

   @Insert("insert into vpp.vpp_device (vpp_id, device_sn, device_name, device_type, " +
           "longitude, latitude, longitude_standard, latitude_standard," +
           " province, city) values " +
           "(#{vppId}, #{deviceSn}, #{deviceName}, #{deviceType}, " +
           "#{longitude}, #{latitude}, #{longitudeStandard}, #{latitudeStandard}," +
           " #{province}, #{city}) " +
           "on duplicate key update device_name = #{deviceName}, device_type = #{deviceType}," +
           " longitude = #{longitude}, latitude = #{latitude}, " +
           "longitude_standard = #{longitudeStandard}, latitude_standard = #{latitudeStandard}," +
           " province = #{province}, city = #{city}")
    void upsert(VppDevice device);


    @Select("SELECT * FROM vpp.vpp_device WHERE device_type = '逆变器'")
    List<VppDevice> selectInverters();
    /**
     * 根据vppId查询逆变器设备
     */
    @Select("SELECT * FROM vpp.vpp_device WHERE vpp_id = #{vppId}")
    List<VppDevice> selectInvertersByVppId(@Param("vppId") Long vppId);

}