<div align="center">

# ⚡ Virtual Power Plant 虚拟电厂

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-1.8+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**🌿 智能、高效、可持续的分布式电力管理平台**

*一个基于Spring Boot的现代化虚拟电厂管理系统，集成实时数据采集、智能预测和设备监控于一体*

[快速开始](#-快速开始) • [API文档](#-api接口) • [部署指南](#-部署) • [贡献指南](#-贡献)

</div>

---

## ✨ 主要功能

<table>
  <tr>
    <td align="center">
      <img src="https://img.icons8.com/fluency/48/000000/electrical.png" width="40">
      <br><strong>设备管理</strong>
      <br>电力设备的CRUD操作和状态监控
    </td>
    <td align="center">
      <img src="https://img.icons8.com/fluency/48/000000/data-configuration.png" width="40">
      <br><strong>实时数据采集</strong>
      <br>从SunGrow等厂商获取逆变器实时数据
    </td>
    <td align="center">
      <img src="https://img.icons8.com/fluency/48/000000/partly-cloudy-day.png" width="40">
      <br><strong>天气数据集成</strong>
      <br>集成GFS地面预报数据，支持电力预测
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://img.icons8.com/fluency/48/000000/database.png" width="40">
      <br><strong>时序数据存储</strong>
      <br>使用阿里云Lindorm进行时序数据存储
    </td>
    <td align="center">
      <img src="https://img.icons8.com/fluency/48/000000/api.png" width="40">
      <br><strong>API文档</strong>
      <br>集成Swagger/OpenAPI文档
    </td>
    <td align="center">
      <img src="https://img.icons8.com/fluency/48/000000/security-checked.png" width="40">
      <br><strong>数据安全</strong>
      <br>RSA和AES加密支持
    </td>
  </tr>
</table>

## 🛠️ 技术栈

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot&logoColor=6DB33F)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white)

</div>

| 技术分类 | 技术选型 | 版本 | 说明 |
|---------|---------|------|------|
| 🏗️ **后端框架** | Spring Boot | 2.7.18 | 主框架 |
| 💾 **关系数据库** | MySQL + MyBatis | 8.0+ | 持久化存储 |
| 📊 **时序数据库** | 阿里云Lindorm TSDB | 2.2.1.3 | 时序数据存储 |
| 📖 **API文档** | SpringDoc OpenAPI | 1.7.0 | 接口文档 |
| 🔐 **数据加密** | RSA/AES | - | 数据安全 |
| 🔧 **构建工具** | Maven | 3.6+ | 项目构建 |
| ☕ **运行环境** | Java | 1.8+ | JVM运行时 |

## 📁 项目结构

```
📦 virtual-power-plant
├── 📂 src/main/
│   ├── 📂 java/com/virtualpowerplant/
│   │   ├── 📂 config/           # ⚙️ 配置类
│   │   ├── 📂 controller/       # 🎮 REST控制器
│   │   ├── 📂 model/           # 📊 数据模型
│   │   ├── 📂 service/         # 💼 业务逻辑
│   │   ├── 📂 mapper/          # 🗂️ MyBatis映射器
│   │   └── 📂 utils/           # 🛠️ 工具类
│   └── 📂 resources/
│       ├── 📂 mapper/          # 📄 MyBatis XML映射文件
│       ├── 📂 sql/             # 🗃️ 数据库脚本
│       └── 📄 application.yml  # ⚙️ 应用配置
├── 📄 pom.xml                  # 📦 Maven依赖配置
├── 📄 README.md               # 📖 项目说明文档
└── 📄 lindorm.md              # 📊 Lindorm数据库说明
```

## 🔧 核心模块

<details>
<summary><strong>🏭 设备管理模块</strong></summary>

- 🎮 `DeviceController` - 设备管理API接口
- 💼 `DeviceService` - 设备业务逻辑处理
- 📊 `Device` - 设备数据模型定义

</details>

<details>
<summary><strong>📡 实时数据采集模块</strong></summary>

- 🎮 `InverterRealTimeDataController` - 逆变器实时数据API
- 🎮 `PowerDataQueryController` - 功率数据查询API (新增)
- 💼 `SunGrowDataService` - SunGrow数据服务
- 📊 `LindormTSDBService` - 时序数据存储服务

</details>

<details>
<summary><strong>🌤️ 天气数据模块</strong></summary>

- 💼 `GfsSurfaceDataService` - GFS地面预报数据服务
- 📊 `InverterGfsSurfaceLindormService` - 天气数据Lindorm存储

</details>

<details>
<summary><strong>🔐 认证安全模块</strong></summary>

- 🎮 `TokenController` - 认证token管理
- 🛠️ `RSAEncryptUtils` - RSA加密工具
- 🛠️ `AESEncryptUtils` - AES加密工具

</details>

## 🚀 快速开始

### 📋 环境要求

> 在开始之前，请确保您的环境满足以下要求：

| 依赖项 | 最低版本 | 推荐版本 | 说明 |
|--------|---------|---------|------|
| ☕ Java | 1.8+ | 11+ | JDK运行环境 |
| 📦 Maven | 3.6+ | 3.8+ | 构建工具 |
| 🗄️ MySQL | 8.0+ | 8.0+ | 关系数据库 |
| 📊 Lindorm | - | 最新版 | 阿里云时序数据库 |

### 🗄️ 配置数据库

<details>
<summary><strong>点击展开数据库配置步骤</strong></summary>

1. **创建MySQL数据库**
   ```sql
   CREATE DATABASE virtual_power_plant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **执行初始化脚本**
   ```bash
   mysql -u username -p virtual_power_plant < src/main/resources/sql/init.sql
   ```

</details>

### ⚙️ 配置应用

<details>
<summary><strong>点击展开应用配置步骤</strong></summary>

1. **配置数据库连接**

   编辑 `src/main/resources/application.yml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/virtual_power_plant
       username: your_username
       password: your_password
   ```

2. **配置Lindorm连接参数**
   ```yaml
   lindorm:
     host: your-lindorm-host
     port: 8242
     username: your_username
     password: your_password
   ```

3. **设置SunGrow API配置**
   ```yaml
   sungrow:
     api:
       url: https://api.sungrow.com
       app-key: your_app_key
       app-secret: your_app_secret
   ```

</details>

### 🏃‍♂️ 运行应用

<details>
<summary><strong>方式一：使用Maven直接运行（推荐开发环境）</strong></summary>

```bash
# 清理并编译
mvn clean compile

# 启动应用
mvn spring-boot:run
```

</details>

<details>
<summary><strong>方式二：打包后运行（推荐生产环境）</strong></summary>

```bash
# 编译打包
mvn clean package -DskipTests

# 运行JAR包
java -jar target/virtual-power-plant-1.0.0.jar
```

</details>

### 🌐 访问应用

启动成功后，您可以通过以下地址访问应用：

| 服务 | 地址 | 说明 |
|------|------|------|
| 🏠 **主应用** | http://localhost:8080 | 应用主页 |
| 📖 **API文档** | http://localhost:8080/swagger-ui.html | Swagger文档界面 |
| ❤️ **健康检查** | http://localhost:8080/hello | 应用健康状态 |
| 📊 **监控端点** | http://localhost:8080/actuator | Spring Boot监控 |

## 📡 API接口

> 💡 **提示**: 启动应用后，可访问 [Swagger文档](http://localhost:8080/swagger-ui.html) 查看完整的API接口文档

### 🏭 设备管理

| 方法 | 路径 | 描述 | 状态码 |
|------|------|------|--------|
| `GET` | `/devices` | 📋 获取设备列表 | `200` |
| `POST` | `/devices` | ➕ 创建新设备 | `201` |
| `PUT` | `/devices/{id}` | ✏️ 更新设备信息 | `200` |
| `DELETE` | `/devices/{id}` | 🗑️ 删除指定设备 | `204` |

### 📊 实时数据

| 方法 | 路径 | 描述 | 状态码 |
|------|------|------|--------|
| `GET` | `/inverter/realtime-data` | 📈 获取逆变器实时数据 | `200` |
| `POST` | `/inverter/sync-data` | 🔄 同步数据到Lindorm | `200` |

### ⚡ 功率数据查询

| 方法 | 路径 | 描述 | 状态码 |
|------|------|------|--------|
| `GET` | `/api/power-data/inverter/{inverterSn}/power` | 📊 按时间段和逆变器SN获取实时功率数据 | `200` |
| `GET` | `/api/power-data/powerstation/{psKey}/power` | 🏭 按时间段和电站PS_KEY获取实时功率数据 | `200` |
| `GET` | `/api/power-data/inverter/{inverterSn}/weather` | 🌤️ 按时间段和逆变器SN获取天气预报 | `200` |
| `GET` | `/api/power-data/powerstation/{psKey}/weather` | 🌦️ 按时间段和电站PS_KEY获取天气预报 | `200` |

### 🔐 认证管理

| 方法 | 路径 | 描述 | 状态码 |
|------|------|------|--------|
| `POST` | `/token` | 🎫 获取访问token | `200` |

<details>
<summary><strong>📝 API使用示例</strong></summary>

**获取设备列表**
```bash
curl -X GET "http://localhost:8080/devices" \
     -H "accept: application/json"
```

**创建设备**
```bash
curl -X POST "http://localhost:8080/devices" \
     -H "Content-Type: application/json" \
     -d '{
       "name": "太阳能发电站1号",
       "type": "solar_panel",
       "location": {
         "latitude": 39.9042,
         "longitude": 116.4074
       }
     }'
```

**获取实时数据**
```bash
curl -X GET "http://localhost:8080/inverter/realtime-data" \
     -H "Authorization: Bearer your_token_here"
```

**按时间段获取逆变器功率数据**
```bash
curl -X GET "http://localhost:8080/api/power-data/inverter/C2123456789/power?startTime=2024-01-01%2000:00:00&endTime=2024-01-01%2023:59:59" \
     -H "accept: application/json"
```

**按时间段获取电站功率数据**
```bash
curl -X GET "http://localhost:8080/api/power-data/powerstation/PS001/power?startTime=2024-01-01%2000:00:00&endTime=2024-01-01%2023:59:59" \
     -H "accept: application/json"
```

**按时间段获取逆变器天气预报**
```bash
curl -X GET "http://localhost:8080/api/power-data/inverter/C2123456789/weather?startTime=2024-01-01%2000:00:00&endTime=2024-01-01%2023:59:59" \
     -H "accept: application/json"
```

**按时间段获取电站天气预报**
```bash
curl -X GET "http://localhost:8080/api/power-data/powerstation/PS001/weather?startTime=2024-01-01%2000:00:00&endTime=2024-01-01%2023:59:59" \
     -H "accept: application/json"
```

</details>

## 📊 数据模型

### 🏭 设备模型 (Device)

```json
{
  "id": "设备唯一标识",
  "name": "设备名称",
  "type": "设备类型 (solar_panel, inverter, battery)",
  "status": "运行状态 (online, offline, maintenance)",
  "location": {
    "latitude": "纬度",
    "longitude": "经度",
    "address": "详细地址"
  },
  "specifications": {
    "capacity": "装机容量 (kW)",
    "manufacturer": "制造商",
    "model": "设备型号"
  },
  "createdTime": "创建时间",
  "updatedTime": "更新时间"
}
```

### ⚡ 逆变器实时数据 (InverterRealTimeData)

```json
{
  "deviceId": "设备ID",
  "timestamp": "数据时间戳",
  "power": {
    "activePower": "有功功率 (kW)",
    "reactivePower": "无功功率 (kVar)",
    "totalEnergy": "累计发电量 (kWh)"
  },
  "voltage": {
    "phaseA": "A相电压 (V)",
    "phaseB": "B相电压 (V)",
    "phaseC": "C相电压 (V)"
  },
  "current": {
    "phaseA": "A相电流 (A)",
    "phaseB": "B相电流 (A)",
    "phaseC": "C相电流 (A)"
  },
  "environment": {
    "temperature": "温度 (°C)",
    "humidity": "湿度 (%)",
    "irradiance": "辐照度 (W/m²)"
  }
}
```

### 🌤️ 天气数据 (GfsSurfaceData)

```json
{
  "location": {
    "latitude": "纬度",
    "longitude": "经度"
  },
  "timestamp": "预报时间",
  "temperature": "温度 (°C)",
  "humidity": "湿度 (%)",
  "radiation": {
    "dswrf": "向下短波辐射 (W/m²)",
    "dlwrf": "向下长波辐射 (W/m²)",
    "uswrf": "向上短波辐射 (W/m²)",
    "ulwrf": "向上长波辐射 (W/m²)"
  },
  "cloudCover": {
    "total": "总云量 (%)",
    "low": "低云量 (%)",
    "medium": "中云量 (%)",
    "high": "高云量 (%)"
  }
}
```

### ⚡ 功率数据查询响应 (PowerDataQueryResponse)

```json
{
  "success": true,
  "data": [
    {
      "id": "数据ID",
      "psName": "电站名称",
      "psKey": "电站标识",
      "latitude": "纬度",
      "longitude": "经度",
      "inverterSn": "逆变器序列号",
      "activePower": "有功功率 (kW)",
      "deviceTime": "设备时间",
      "createTime": "创建时间"
    }
  ],
  "inverter_sn": "C2123456789",
  "start_time": "查询开始时间",
  "end_time": "查询结束时间",
  "count": "数据条数",
  "message": "查询结果说明"
}
```

### 🌤️ 天气预报查询响应 (WeatherDataQueryResponse)

```json
{
  "success": true,
  "data": [
    {
      "psName": "电站名称",
      "psKey": "电站标识",
      "deviceSn": "设备序列号",
      "time": "预报时间",
      "tcc": "总云量 (%)",
      "lcc": "低云量 (%)",
      "mcc": "中云量 (%)",
      "hcc": "高云量 (%)",
      "dswrf": "向下短波辐射 (W/m²)",
      "dlwrf": "向下长波辐射 (W/m²)",
      "uswrf": "向上短波辐射 (W/m²)",
      "ulwrf": "向上长波辐射 (W/m²)"
    }
  ],
  "inverter_sn": "C2123456789",
  "start_time": "查询开始时间",
  "end_time": "查询结束时间",
  "count": "数据条数",
  "message": "查询结果说明"
}
```

## 👨‍💻 开发说明

### 📝 代码规范

- ✅ 遵循Spring Boot最佳实践
- ✅ 使用MyBatis进行数据库操作
- ✅ 统一异常处理机制
- ✅ 标准化日志记录
- ✅ RESTful API设计规范
- ✅ 代码注释和文档完善

### 🧪 测试

<details>
<summary><strong>点击展开测试相关命令</strong></summary>

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=DeviceServiceTest

# 生成测试覆盖率报告
mvn jacoco:report

# 跳过测试进行打包
mvn package -DskipTests
```

</details>

## 🚀 部署

### 🐳 Docker部署

<details>
<summary><strong>使用Docker部署（推荐）</strong></summary>

```dockerfile
# Dockerfile
FROM openjdk:8-jre-slim

WORKDIR /app
COPY target/virtual-power-plant-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
```

```bash
# 构建镜像
docker build -t virtual-power-plant:1.0.0 .

# 运行容器
docker run -d -p 8080:8080 --name vpp virtual-power-plant:1.0.0
```

</details>

### ☁️ 云平台部署

<details>
<summary><strong>支持的部署方式</strong></summary>

- **🌐 传统部署**: 打包为JAR文件独立运行
- **📦 容器化**: Docker + Kubernetes
- **☁️ 云原生**: 阿里云、腾讯云、AWS等
- **🔄 CI/CD**: Jenkins、GitLab CI、GitHub Actions

</details>

---

## 📄 许可证

<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

本项目采用 **MIT 许可证**，详细信息请查看 [LICENSE](LICENSE) 文件。

</div>

## 🤝 贡献

我们欢迎所有形式的贡献！请阅读以下指南：

<details>
<summary><strong>📖 贡献指南</strong></summary>

1. **🔀 Fork 本仓库**
2. **🌿 创建功能分支** (`git checkout -b feature/AmazingFeature`)
3. **📝 提交更改** (`git commit -m 'Add some AmazingFeature'`)
4. **📤 推送到分支** (`git push origin feature/AmazingFeature`)
5. **🔄 创建 Pull Request**

### 代码贡献类型

- 🐛 Bug修复
- ✨ 新功能开发
- 📚 文档改进
- 🎨 代码重构
- ⚡ 性能优化
- 🧪 测试用例

</details>

## 📞 联系方式

<div align="center">

如有问题或建议，欢迎通过以下方式联系我们：

[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-red?style=for-the-badge&logo=github)](https://github.com/your-username/virtual-power-plant/issues)
[![Email](https://img.shields.io/badge/Email-Contact-blue?style=for-the-badge&logo=gmail)](mailto:your-email@example.com)

---

<sub>🌟 **如果这个项目对您有帮助，请给它一个 Star！** 🌟</sub>

</div>