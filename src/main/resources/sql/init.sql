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
    var_desc     text        null
)
    comment '天气数据集元数据信息';