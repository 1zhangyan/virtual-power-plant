package com.virtualpowerplant.mapper;

import com.virtualpowerplant.model.VirtualPowerPlant;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface VppMapper {

    /**
     * 插入或更新虚拟电厂信息
     */
    int upsert(VirtualPowerPlant vpp);

    /**
     * 根据用户ID获取虚拟电厂信息
     */
    @Select("SELECT * FROM vpp.virtual_power_plant WHERE user_id = #{userId}")
    VirtualPowerPlant findByUserId(@Param("userId") String userId);

    /**
     * 根据用户账号获取虚拟电厂信息
     */
    @Select("SELECT * FROM vpp.virtual_power_plant WHERE user_account = #{userAccount}")
    VirtualPowerPlant findByUserAccount(@Param("userAccount") String userAccount);

    /**
     * 根据VPP ID获取虚拟电厂信息
     */
    @Select("SELECT * FROM vpp.virtual_power_plant WHERE vpp_id = #{vppId}")
    VirtualPowerPlant findByVppId(@Param("vppId") Long vppId);
}