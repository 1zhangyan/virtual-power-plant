-- 为现有的device表添加经纬度字段
USE vpp;

-- 添加纬度和经度字段
ALTER TABLE device
ADD COLUMN latitude DECIMAL(10, 8) COMMENT '纬度' AFTER grid_connection_date,
ADD COLUMN longitude DECIMAL(11, 8) COMMENT '经度' AFTER latitude;

-- 验证字段添加
DESCRIBE device;

-- 查看表结构
SHOW CREATE TABLE device;