-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS vpp
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE vpp;

-- 创建设备表（如果不存在）
CREATE TABLE IF NOT EXISTS sungrow_device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid BIGINT NOT NULL UNIQUE COMMENT '设备唯一标识',
    ps_id BIGINT NOT NULL COMMENT '电站ID',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_sn VARCHAR(50) NOT NULL COMMENT '设备序列号',
    device_type INT NOT NULL COMMENT '设备类型(1:逆变器, 22:通信模块)',
    device_code INT COMMENT '设备代码',
    type_name VARCHAR(50) COMMENT '设备类型名称',
    device_model_code VARCHAR(50) COMMENT '设备型号代码',
    device_model_id BIGINT COMMENT '设备型号ID',
    factory_name VARCHAR(100) COMMENT '制造商名称',
    channel_id INT COMMENT '通道ID',
    ps_key VARCHAR(100) COMMENT '电站键值',
    communication_dev_sn VARCHAR(50) COMMENT '通信设备序列号',
    dev_status VARCHAR(10) COMMENT '设备状态(0:离线, 1:在线)',
    dev_fault_status INT COMMENT '设备故障状态(4:正常)',
    rel_state INT COMMENT '连接状态(0:未连接, 1:已连接)',
    rel_time DATETIME COMMENT '连接时间',
    grid_connection_date DATETIME COMMENT '并网时间',
    latitude DECIMAL(10, 8) COMMENT '纬度',
    longitude DECIMAL(11, 8) COMMENT '经度',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_ps_id (ps_id),
    INDEX idx_device_type (device_type),
    INDEX idx_dev_status (dev_status),
    INDEX idx_uuid (uuid),
    INDEX idx_device_sn (device_sn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备信息表';

-- 插入示例数据（可选）
INSERT IGNORE INTO sungrow_device (uuid, ps_id, device_name, device_sn, device_type, type_name, factory_name, dev_status, dev_fault_status, rel_state, latitude, longitude)
VALUES
    (1001, 1, '逆变器-001', 'INV-001', 1, '逆变器', '阳光电源', '1', 4, 1, 39.9042, 116.4074),
    (1002, 1, '通信模块-001', 'COM-001', 22, '通信模块', '阳光电源', '1', 4, 1, 39.9042, 116.4074),
    (1003, 1, '逆变器-002', 'INV-002', 1, '逆变器', '阳光电源', '0', 2, 0, 31.2304, 121.4737);

-- 验证数据插入
SELECT 'SunGrow Device table created and sample data inserted' as status;
SELECT COUNT(*) as device_count FROM sungrow_device;