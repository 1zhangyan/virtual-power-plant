-- 创建虚拟电厂表
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

-- 插入示例数据
INSERT IGNORE INTO vpp.virtual_power_plant (mobile_tel, user_name, language, user_id, country_name, user_account, user_master_org_name, email, country_id) VALUES
('13800138001', '张三', 'zh', 'user001', '中国', 'zhangsan@vpp.com', '北京虚拟电厂有限公司', 'zhangsan@example.com', 'CN'),
('13800138002', '李四', 'zh', 'user002', '中国', 'lisi@vpp.com', '上海虚拟电厂有限公司', 'lisi@example.com', 'CN'),
('13800138003', 'John Smith', 'en', 'user003', 'United States', 'john.smith@vpp.com', 'US Virtual Power Plant Inc.', 'john.smith@example.com', 'US');

-- 验证数据插入
SELECT 'Virtual Power Plant table created and sample data inserted' as status;
SELECT COUNT(*) as vpp_count FROM vpp.virtual_power_plant;