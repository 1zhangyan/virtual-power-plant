package com.virtualpowerplant.mapper;

import com.virtualpowerplant.model.VirtualPowerPlant;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface VppMapper {

    /**
     * 插入虚拟电厂信息
     */
    @Insert("INSERT INTO vpp.virtual_power_plant (mobile_tel, user_name, language, user_id, country_name, " +
            "user_account, user_master_org_name, email, country_id) " +
            "VALUES (#{mobileTel}, #{userName}, #{language}, #{userId}, #{countryName}, " +
            "#{userAccount}, #{userMasterOrgName}, #{email}, #{countryId})")
    @Options(useGeneratedKeys = true, keyProperty = "vppId")
    int insert(VirtualPowerPlant vpp);


    /**
     * 查询所有虚拟电厂信息
     */
    @Select("SELECT vpp_id, mobile_tel, user_name, language, user_id, country_name, " +
            "user_account, user_master_org_name, email, country_id, created_at, updated_at " +
            "FROM vpp.virtual_power_plant ORDER BY vpp_id")
    @Results({
        @Result(property = "vppId", column = "vpp_id"),
        @Result(property = "mobileTel", column = "mobile_tel"),
        @Result(property = "userName", column = "user_name"),
        @Result(property = "language", column = "language"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "countryName", column = "country_name"),
        @Result(property = "userAccount", column = "user_account"),
        @Result(property = "userMasterOrgName", column = "user_master_org_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "countryId", column = "country_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<VirtualPowerPlant> selectAll();

    /**
     * 根据用户ID获取虚拟电厂信息
     */
    @Select("SELECT vpp_id, mobile_tel, user_name, language, user_id, country_name, " +
            "user_account, user_master_org_name, email, country_id, created_at, updated_at " +
            "FROM vpp.virtual_power_plant WHERE user_id = #{userId}")
    @Results({
        @Result(property = "vppId", column = "vpp_id"),
        @Result(property = "mobileTel", column = "mobile_tel"),
        @Result(property = "userName", column = "user_name"),
        @Result(property = "language", column = "language"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "countryName", column = "country_name"),
        @Result(property = "userAccount", column = "user_account"),
        @Result(property = "userMasterOrgName", column = "user_master_org_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "countryId", column = "country_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    VirtualPowerPlant findByUserId(@Param("userId") String userId);

    /**
     * 根据用户账号获取虚拟电厂信息
     */
    @Select("SELECT vpp_id, mobile_tel, user_name, language, user_id, country_name, " +
            "user_account, user_master_org_name, email, country_id, created_at, updated_at " +
            "FROM vpp.virtual_power_plant WHERE user_account = #{userAccount}")
    @Results({
        @Result(property = "vppId", column = "vpp_id"),
        @Result(property = "mobileTel", column = "mobile_tel"),
        @Result(property = "userName", column = "user_name"),
        @Result(property = "language", column = "language"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "countryName", column = "country_name"),
        @Result(property = "userAccount", column = "user_account"),
        @Result(property = "userMasterOrgName", column = "user_master_org_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "countryId", column = "country_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    VirtualPowerPlant findByUserAccount(@Param("userAccount") String userAccount);

    /**
     * 根据VPP ID获取虚拟电厂信息
     */
    @Select("SELECT vpp_id, mobile_tel, user_name, language, user_id, country_name, " +
            "user_account, user_master_org_name, email, country_id, created_at, updated_at " +
            "FROM vpp.virtual_power_plant WHERE vpp_id = #{vppId}")
    @Results({
        @Result(property = "vppId", column = "vpp_id"),
        @Result(property = "mobileTel", column = "mobile_tel"),
        @Result(property = "userName", column = "user_name"),
        @Result(property = "language", column = "language"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "countryName", column = "country_name"),
        @Result(property = "userAccount", column = "user_account"),
        @Result(property = "userMasterOrgName", column = "user_master_org_name"),
        @Result(property = "email", column = "email"),
        @Result(property = "countryId", column = "country_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    VirtualPowerPlant findByVppId(@Param("vppId") Long vppId);
}