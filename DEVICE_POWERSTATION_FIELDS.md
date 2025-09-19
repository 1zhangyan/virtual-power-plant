# Device模型添加PowerStation字段说明

## 概述

已为Device模型添加PowerStation相关字段，以便在设备数据中包含对应电站的详细信息。

## 新增字段

### Device模型新增字段：

1. **psName** - String - 电站名称
2. **psType** - Integer - 电站类型
3. **onlineStatus** - Integer - 电站在线状态 (对应PowerStation.psStatus)
4. **provinceName** - String - 省份名称
5. **cityName** - String - 城市名称
6. **districtName** - String - 区域名称
7. **connectType** - Integer - 连接类型

### 数据库表新增字段：

```sql
-- 数据库字段定义
ps_name VARCHAR(100) COMMENT '电站名称',
ps_type INT COMMENT '电站类型',
online_status INT COMMENT '电站在线状态',
province_name VARCHAR(50) COMMENT '省份名称',
city_name VARCHAR(50) COMMENT '城市名称',
district_name VARCHAR(50) COMMENT '区域名称',
connect_type INT COMMENT '连接类型'
```

## 数据同步逻辑

在DeviceService.syncDevicesWithCoordinates()方法中，系统会：

1. 获取所有PowerStation信息
2. 获取所有Device信息
3. 根据Device.psId匹配对应的PowerStation
4. 将PowerStation的相关字段复制到Device中：

```java
if (ps != null) {
    device.setLatitude(ps.getLatitude());
    device.setLongitude(ps.getLongitude());
    device.setPsName(ps.getPsName());
    device.setPsType(ps.getPsType());
    device.setOnlineStatus(ps.getPsStatus()); // psStatus对应onlineStatus
    device.setProvinceName(ps.getProvinceName());
    device.setCityName(ps.getCityName());
    device.setDistrictName(ps.getDistrictName());
    device.setConnectType(ps.getConnectType());
}
```

## 字段映射关系

| Device字段 | PowerStation字段 | 说明 |
|-----------|-----------------|------|
| psName | psName | 电站名称 |
| psType | psType | 电站类型 |
| onlineStatus | psStatus | 在线状态（字段名不同但含义相同） |
| provinceName | provinceName | 省份名称 |
| cityName | cityName | 城市名称 |
| districtName | districtName | 区域名称 |
| connectType | connectType | 连接类型 |
| latitude | latitude | 纬度（已存在） |
| longitude | longitude | 经度（已存在） |

## 数据库更新

### 新建表
使用 `init.sql` 创建包含所有字段的新表。

### 现有表更新
使用 `add_powerstation_fields.sql` 为现有表添加新字段：

```sql
-- 执行此脚本为现有表添加字段
mysql -u root -p < src/main/resources/sql/add_powerstation_fields.sql
```

## API响应示例

同步后的设备数据将包含完整的电站信息：

```json
{
  "uuid": 1001,
  "psId": 1,
  "psName": "北京朝阳电站",
  "deviceName": "逆变器-001",
  "deviceType": 1,
  "latitude": 39.9042,
  "longitude": 116.4074,
  "psType": 1,
  "onlineStatus": 1,
  "provinceName": "北京市",
  "cityName": "北京市",
  "districtName": "朝阳区",
  "connectType": 1
}
```

## 使用方法

### 1. 同步设备数据（包含电站信息）

```bash
POST http://localhost:8080/api/devices/sync
```

### 2. 查询逆变器设备

```bash
GET http://localhost:8080/api/devices/inverters/coordinates
```

返回的逆变器数据将包含完整的电站信息。

## 便捷方法

Device类现有的便捷方法仍然可用：

```java
// 设备相关
device.isOnline()         // 设备是否在线
device.isInverter()       // 是否为逆变器
device.isCommunicationModule()  // 是否为通信模块
device.hasFault()         // 是否有故障

// 新增：可通过电站信息判断
device.getOnlineStatus()  // 电站在线状态
device.getPsName()        // 电站名称
device.getProvinceName()  // 省份名称
// ...其他字段
```

## 注意事项

1. **数据一致性**: 设备的电站信息来源于PowerStation表，通过psId关联
2. **字段映射**: PowerStation.psStatus映射到Device.onlineStatus
3. **空值处理**: 如果设备的psId在PowerStation表中找不到对应记录，相关字段将为null
4. **数据更新**: 每次同步都会更新设备的电站信息，保持数据最新