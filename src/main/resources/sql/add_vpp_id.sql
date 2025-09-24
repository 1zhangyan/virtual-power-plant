-- 添加vpp_id字段到设备表
ALTER TABLE sungrow_device
ADD COLUMN vpp_id BIGINT COMMENT 'VPP电厂标识' AFTER longitude;

-- 添加vpp_id索引
CREATE INDEX idx_vpp_id ON sungrow_device(vpp_id);

-- 更新现有设备的vpp_id（可以根据实际业务需求设置默认值）
-- UPDATE sungrow_device SET vpp_id = 1 WHERE vpp_id IS NULL;