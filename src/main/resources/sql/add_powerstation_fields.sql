-- 为现有的sungrow_device表添加PowerStation相关字段
USE vpp;

-- 添加PowerStation相关字段
ALTER TABLE sungrow_device
ADD COLUMN ps_name VARCHAR(100) COMMENT '电站名称' AFTER ps_id,
ADD COLUMN ps_type INT COMMENT '电站类型' AFTER longitude,
ADD COLUMN online_status INT COMMENT '电站在线状态' AFTER ps_type,
ADD COLUMN province_name VARCHAR(50) COMMENT '省份名称' AFTER online_status,
ADD COLUMN city_name VARCHAR(50) COMMENT '城市名称' AFTER province_name,
ADD COLUMN district_name VARCHAR(50) COMMENT '区域名称' AFTER city_name,
ADD COLUMN connect_type INT COMMENT '连接类型' AFTER district_name;

-- 验证字段添加
DESCRIBE sungrow_device;

-- 查看表结构
SHOW CREATE TABLE sungrow_device;