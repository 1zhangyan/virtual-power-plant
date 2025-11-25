-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS vpp
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE vpp;

CREATE TABLE IF NOT EXISTS vpp.virtual_power_plant (
                                                       vpp_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '虚拟电厂ID（自增主键）',
                                                       mobile_tel VARCHAR(20) COMMENT '手机号码',
    user_name VARCHAR(100) COMMENT '用户姓名',
    language VARCHAR(10) COMMENT '语言',
    user_id VARCHAR(50) COMMENT '用户ID',
    country_name VARCHAR(100) COMMENT '国家名称',
    user_account VARCHAR(100) COMMENT '用户账号',
    user_master_org_name VARCHAR(200) COMMENT '用户主组织名称',
    email VARCHAR(200) COMMENT '邮箱',
    country_id VARCHAR(10) COMMENT '国家ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 创建索引
    UNIQUE KEY uk_user_id (user_id),
    UNIQUE KEY uk_user_account (user_account),
    INDEX idx_mobile_tel (mobile_tel),
    INDEX idx_email (email),
    INDEX idx_user_name (user_name)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='虚拟电厂表';

create table vpp_device
(
    `device_id` bigint UNSIGNED auto_increment PRIMARY KEY comment  '自增主键',
    `vpp_id` bigint not null comment 'vppid',
    `device_sn` varchar(128) not null comment 'sn',
    `device_name` varchar(128) not null comment 'name',
    `device_type` varchar(128) not null comment 'type',
    `longitude` double not null comment 'longitude',
    `latitude` double not null comment 'latitude',
    `longitude_standard` double not null comment 'longitude_standard',
    `latitude_standard` double not null comment 'latitude_standard',
    `province` varchar(128) not null comment 'province',
    `city` varchar(128) not null comment 'city',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
    UNIQUE INDEX  uniq_feature_ref (feature_id,feature_table_id,feature_table_col)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci comment '特征和特征表的关联信息';

create table dataset_meta_info
(
    id           bigint auto_increment
        primary key,
    dataset_type varchar(56) not null,
    meta_type    varchar(56) not null,
    var_name     varchar(56) not null,
    meta_var     varchar(56) not null,
    default_unit varchar(56) null,
    support_unit varchar(56) null,
    var_desc     text        null,
    valid int  not null  default  0,
    hour_diff int not null
)
    comment '天气数据集元数据信息';
-- lindorm
CREATE TABLE IF NOT EXISTS weather_forest_v1_cfs_h6_surface (longitude varchar TAG, latitude varchar TAG, time TIMESTAMP, t double ,t2m double ,tmax double ,tmin double ,tcc double ,lcc double,mcc double,hcc double,dswrf double,dlwrf double,uswrf double,ulwrf double, PRIMARY KEY (longitude,latitude) );
CREATE TABLE IF NOT EXISTS weather_forest_v1_icon_surface (longitude varchar TAG, latitude varchar TAG, time TIMESTAMP,  d2m double,t2m double,t2m_max double,t2m_min double,t_snow double,nswrf-acc double,nswrf_top-acc double,nswrfcs-acc double,dswdif-acc double,uswdif-acc double,dswdir-acc double,nswrf double,nswrf_top double,nswrfcs double,dswdif double,uswdif double,dswdir double,nlwrf double,nlwrf_top double,hcc double,lcc double,mcc double,tcc double,t_s double, PRIMARY KEY (longitude,latitude) );
CREATE TABLE IF NOT EXISTS weather_forest_v1_aifs_surface (longitude varchar TAG, latitude varchar TAG, time TIMESTAMP, t2m double,d2m double,skt double,wbt double,ssrd-acc double,strd-acc double,ssrd double,strd double,tcc double,lcc double,mcc double,hcc double, PRIMARY KEY (longitude,latitude) );
CREATE TABLE IF NOT EXISTS weather_forest_v1_gfs_surface (longitude varchar TAG, latitude varchar TAG, time TIMESTAMP, t2m double,d2m double,skt double,t0m double,t80m double,t100m double,wbt double,tcc double,lcc double,mcc double,hcc double,dswrf double,dlwrf double,uswrf double,ulwrf double, PRIMARY KEY (longitude,latitude) );
CREATE TABLE IF NOT EXISTS weather_forest_v1_ifs_surface (longitude varchar TAG, latitude varchar TAG, time TIMESTAMP, t2m double,d2m double,skt double,ttr-acc double,ssr-acc double,ssrd-acc double,str-acc double,strd-acc double,ttr double,ssr double,ssrd double,str double,strd double, PRIMARY KEY (longitude,latitude) );
CREATE TABLE IF NOT EXISTS weather_forest_v1_gfs_graphcast (longitude varchar TAG, latitude varchar TAG, time TIMESTAMP, t2m double,t1000hpa double,t925hpa double,t850hpa double,t700hpa double,t600hpa double,t500hpa double,t400hpa double,t300hpa double,t250hpa double,t200hpa double,t150hpa double,t100hpa double,t50hpa double,tcc double,lcc double,mcc double,hcc double,PRIMARY KEY (longitude,latitude) );
CREATE TABLE IF NOT EXISTS weather_forest_v1_gdas_surface (longitude varchar TAG, latitude varchar TAG, time TIMESTAMP, t2m double,d2m double,skt double,t0m double,t80m double,t100m double,tcc double,lcc double,mcc double,hcc double,dswrf double,dlwrf double,uswrf double,ulwrf double ,PRIMARY KEY (longitude,latitude) );
CREATE TABLE IF NOT EXISTS devcie_wheather_active_power_info (longitude varchar TAG, latitude varchar TAG, device_sn varchar TAG, vpp_id  varchar TAG, time TIMESTAMP, active_power double,  cfs_h6_surface_t double , cfs_h6_surface_t2m double , cfs_h6_surface_tmax double , cfs_h6_surface_tmin double , cfs_h6_surface_tcc double , cfs_h6_surface_lcc double , cfs_h6_surface_mcc double , cfs_h6_surface_hcc double , cfs_h6_surface_dswrf double , cfs_h6_surface_dlwrf double , cfs_h6_surface_uswrf double , cfs_h6_surface_ulwrf double , icon_surface_d2m double , icon_surface_t2m double , icon_surface_t2m_max double , icon_surface_t2m_min double , icon_surface_t_snow double , icon_surface_nswrf-acc double , icon_surface_nswrf_top-acc double , icon_surface_nswrfcs-acc double , icon_surface_dswdif-acc double , icon_surface_uswdif-acc double , icon_surface_dswdir-acc double , icon_surface_nswrf double , icon_surface_nswrf_top double , icon_surface_nswrfcs double , icon_surface_dswdif double , icon_surface_uswdif double , icon_surface_dswdir double , icon_surface_nlwrf double , icon_surface_nlwrf_top double , icon_surface_hcc double , icon_surface_lcc double , icon_surface_mcc double , icon_surface_tcc double , icon_surface_t_s double , aifs_surface_t2m double , aifs_surface_d2m double , aifs_surface_skt double , aifs_surface_wbt double , aifs_surface_ssrd-acc double , aifs_surface_strd-acc double , aifs_surface_ssrd double , aifs_surface_strd double , aifs_surface_tcc double , aifs_surface_lcc double , aifs_surface_mcc double , aifs_surface_hcc double , gfs_surface_t2m double , gfs_surface_d2m double , gfs_surface_skt double , gfs_surface_t0m double , gfs_surface_t80m double , gfs_surface_t100m double , gfs_surface_wbt double , gfs_surface_tcc double , gfs_surface_lcc double , gfs_surface_mcc double , gfs_surface_hcc double , gfs_surface_dswrf double , gfs_surface_dlwrf double , gfs_surface_uswrf double , gfs_surface_ulwrf double , ifs_surface_t2m double , ifs_surface_d2m double , ifs_surface_skt double , ifs_surface_ttr-acc double , ifs_surface_ssr-acc double , ifs_surface_ssrd-acc double , ifs_surface_str-acc double , ifs_surface_strd-acc double , ifs_surface_ttr double , ifs_surface_ssr double , ifs_surface_ssrd double , ifs_surface_str double , ifs_surface_strd double , gfs_graphcast_t2m double , gfs_graphcast_t1000hpa double , gfs_graphcast_t925hpa double , gfs_graphcast_t850hpa double , gfs_graphcast_t700hpa double , gfs_graphcast_t600hpa double , gfs_graphcast_t500hpa double , gfs_graphcast_t400hpa double , gfs_graphcast_t300hpa double , gfs_graphcast_t250hpa double , gfs_graphcast_t200hpa double , gfs_graphcast_t150hpa double , gfs_graphcast_t100hpa double , gfs_graphcast_t50hpa double , gfs_graphcast_tcc double , gfs_graphcast_lcc double , gfs_graphcast_mcc double , gfs_graphcast_hcc double , PRIMARY KEY (longitude,latitude,device_sn,vpp_id));


