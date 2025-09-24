-- 如果vpp_id字段已经存在为VARCHAR类型，则修改为BIGINT类型
-- 注意：在修改之前请确保现有数据可以安全转换为BIGINT类型

-- 先删除现有索引
DROP INDEX IF EXISTS idx_vpp_id ON sungrow_device;

-- 修改字段类型
ALTER TABLE sungrow_device
MODIFY COLUMN vpp_id BIGINT COMMENT 'VPP电厂标识';

-- 重新创建索引
CREATE INDEX idx_vpp_id ON sungrow_device(vpp_id);