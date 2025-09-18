# SunGrow API 分页功能说明

## 概述

SunGrow API的电站和设备接口每次最多只能返回200条数据，因此需要使用分页来获取所有数据。本系统已实现自动分页功能，能够获取所有可用的电站和设备数据。

## 分页实现

### 电站数据分页 (`getPowerStationsAndParse`)

```java
public static List<PowerStation> getPowerStationsAndParse(String token) throws Exception {
    List<PowerStation> allPowerStations = new ArrayList<>();
    int page = 1;
    int pageSize = 200; // API每页最大200条

    while (true) {
        // 获取当前页数据
        String jsonResponse = getPowerStationList(token, page, pageSize);
        List<PowerStation> powerStations = SunGrowResponseParser.extractPowerStations(jsonResponse);

        // 检查是否还有数据
        if (powerStations == null || powerStations.isEmpty()) {
            break;
        }

        allPowerStations.addAll(powerStations);

        // 如果返回数据少于页大小，说明这是最后一页
        if (powerStations.size() < pageSize) {
            break;
        }

        page++;
    }

    return allPowerStations;
}
```

### 设备数据分页 (`getDevicesAndParse`)

```java
public static List<Device> getDevicesAndParse(String token) throws Exception {
    List<Device> allDevices = new ArrayList<>();
    int page = 1;
    int pageSize = 200; // API每页最大200条

    while (true) {
        // 获取当前页数据
        String jsonResponse = getDeviceList(token, page, pageSize);
        List<Device> devices = SunGrowResponseParser.extractDevices(jsonResponse);

        // 检查是否还有数据
        if (devices == null || devices.isEmpty()) {
            break;
        }

        allDevices.addAll(devices);

        // 如果返回数据少于页大小，说明这是最后一页
        if (devices.size() < pageSize) {
            break;
        }

        page++;
    }

    return allDevices;
}
```

## 分页参数

### API请求参数

- `curPage`: 当前页码，从1开始
- `size`: 每页数据量，最大200
- `token`: 认证令牌

### 示例请求

```json
{
    "curPage": 1,
    "size": 200,
    "token": "your_access_token",
    "appkey": "your_app_key",
    "api_key_param": {
        "nonce": "random_string",
        "timestamp": 1234567890
    }
}
```

## 停止条件

分页循环在以下情况下停止：

1. **空响应**: API返回null或空列表
2. **最后一页**: 返回的数据量少于请求的页大小(200)

## 日志监控

系统提供详细的日志监控分页过程：

```
获取电站列表第 1 页，每页 200 条
第 1 页获取到 200 个电站，累计 200 个
获取电站列表第 2 页，每页 200 条
第 2 页获取到 150 个电站，累计 350 个
第 2 页数据量 150 小于页大小 200，已获取完所有数据
成功解析到总共 350 个电站信息
```

## 性能考虑

### 数据量估算
- 每页最大200条记录
- 电站数据通常较少，1-3页即可完成
- 设备数据可能较多，根据实际规模确定页数

### 网络请求优化
- 使用最大页大小(200)减少请求次数
- 添加适当的错误重试机制
- 记录详细日志便于调试

### 数据库同步优化
- 使用事务批量处理
- 每100条记录记录一次进度日志
- 使用upsert避免重复数据

## 同步接口使用

### REST API调用

```bash
# 触发设备同步
POST http://localhost:8080/api/devices/sync

# 查询带坐标的逆变器
GET http://localhost:8080/api/devices/inverters/coordinates
```

### 同步流程

1. **登录认证** - 获取访问令牌
2. **分页获取电站** - 获取所有电站信息（包含经纬度）
3. **分页获取设备** - 获取所有设备信息
4. **数据关联** - 根据`ps_id`为设备添加电站的经纬度
5. **数据库同步** - 使用upsert操作更新数据库

## 错误处理

- 网络超时：自动重试机制
- API限流：添加适当延迟
- 数据解析错误：记录错误日志并继续处理
- 数据库错误：事务回滚确保数据一致性

## 测试验证

使用`DeviceSyncDemo`类可以测试分页功能：

```bash
java com.virtualpowerplant.DeviceSyncDemo
```

该演示程序会：
1. 展示分页获取电站数据的过程
2. 展示分页获取设备数据的过程
3. 统计各类型设备数量
4. 显示前5条记录作为示例