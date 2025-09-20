CREATE DATABASE IF NOT EXISTS vpp;
USE vpp;

-- 逆变器实时数据表
CREATE TABLE IF NOT EXISTS inverter_realtime_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ps_name VARCHAR(255) COMMENT '电站名称',
    ps_id VARCHAR(128) COMMENT '电站ID',
    latitude DOUBLE COMMENT '电站纬度',
    longitude DOUBLE COMMENT '电站经度',
    inverter_sn VARCHAR(128) NOT NULL COMMENT '逆变器序列号',
    active_power DOUBLE COMMENT '实时有功功率(kW)',
    device_time DATETIME COMMENT '设备时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',

    INDEX idx_inverter_sn (inverter_sn),
    INDEX idx_device_time (device_time),
    INDEX idx_ps_id (ps_id),
    INDEX idx_sn_device_time (inverter_sn, device_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='逆变器实时数据表';