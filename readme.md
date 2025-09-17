# 查询指定时间气象数据
import requests

data_type = 'gfs_surface'

url = f"https://api-pro-openet.terraqt.com/v1/{data_type}/multi/point" #这里新增multi

headers = {
'Content-Type': 'application/json',
'token': '********' #注意更换为自己的token
}

request = {
'time': '2024-01-15 00:00:00', #注意，这个时间是UTC起报时间，通常每天4次，分别在0点，6点，12点和18点
'points': [
[
103.1693835,
30.5398753
],
[
104.0693835,
30.5398753
]
],
'mete_vars': ['t2m'],
'avg': False,
}

response = requests.request("POST", url, headers=headers, json=request)

print(response.json()['data'])


# token 保存在 ~/token.txt 中，json 格式

# 返回值
{
'data': [{
'location': [120.25, 23.5],
'values': [[303.533, 298.1826],
[304.4319, 298.5295],
[305.4548, 297.7632]
}],
'mete_var': ['t2m', 'd2m'],
'mete_unit': ['K', 'K'],
'time_fcst': '2024-01-15 00:00:00' #UTC起报时刻，仅适用于预测数据，观测数据不返回起报时刻
'timestamp': [
'2024-01-15 01:00:00',
'2024-01-15 02:00:00',
'2024-01-15 03:00:00'
]
}

## Java Spring Boot 实现

基于上述Python代码实现了Java版本的周期性气象数据获取服务：

### 🔧 项目结构
```
src/main/java/com/virtualpowerplant/
├── VirtualPowerPlantApplication.java    # 主启动类 (@EnableScheduling)
├── config/
│   ├── OpenApiConfig.java              # Swagger配置
│   └── TokenConfig.java                # Token配置读取
├── service/
│   └── WeatherDataService.java         # 气象数据服务 (@Scheduled)
└── controller/
    ├── HelloWorldController.java       # Hello World API
    └── WeatherController.java          # 气象数据手动触发API
```

### ⚡ 核心功能
- **每10秒自动执行**: 使用`@Scheduled(fixedDelay = 10000)`
- **Token管理**: 自动读取`~/token.txt`文件
- **详细日志**: 包含时间戳、请求参数、响应结果
- **错误处理**: 完整的异常捕获和日志记录
- **手动触发**: REST API `/api/weather/trigger`

### 🚀 运行方式

#### 方式1: Spring Boot应用 (推荐)
```bash
./mvnw spring-boot:run
```
访问 http://localhost:8080/swagger-ui.html 查看API文档

#### 方式2: 独立演示程序
```bash
java DemoWeatherService
```

### 📋 日志示例
```
=== Weather Data Fetch [2025-09-18 01:33:44] ===
Token: iBDMwMGN4g...
API URL: https://api-pro-openet.terraqt.com/v1/gfs_surface/multi/point
Request Points: [103.1693835, 30.5398753], [104.0693835, 30.5398753]
Meteorological Variables: [t2m]
Query Time: 2024-01-15 00:00:00 (UTC)
Status: SUCCESS
Response Data: {
  "point_1": { "t2m": 15.2, "coordinates": [103.1693835, 30.5398753] },
  "point_2": { "t2m": 16.1, "coordinates": [104.0693835, 30.5398753] }
}
=== Fetch Complete ===
```

## 🧪 测试

项目遵循Spring Boot标准测试结构，包含完整的单元测试和集成测试：

### 测试运行
```bash
# 运行所有测试
./mvnw test

# 运行特定测试
./mvnw test -Dtest=WeatherDataServiceTest
```

### 测试覆盖
- ✅ 单元测试：WeatherDataService、TokenConfig、WeatherController
- ✅ 集成测试：完整API调用流程
- ✅ Mock测试：HTTP请求、JSON解析
- ✅ 测试工具：MockWeatherServer

详细测试文档：[src/test/README.md](src/test/README.md)

### 🛠 技术栈
- Spring Boot 2.7.18
- Spring Scheduling
- RestTemplate HTTP客户端
- Jackson JSON处理
- SpringDoc OpenAPI (Swagger)
- Maven 3.9.5

### 测试技术栈
- JUnit 5
- Mockito
- Spring Boot Test
- WireMock
- MockMvc 
