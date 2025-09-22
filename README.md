# Virtual Power Plant 虚拟电厂

一个基于Spring Boot的虚拟电厂管理系统，用于管理和监控分布式电力设备，包括逆变器实时数据采集、天气预报数据集成和设备管理。

## 主要功能

- **设备管理**: 电力设备的CRUD操作和状态监控
- **实时数据采集**: 从SunGrow等厂商获取逆变器实时数据
- **天气数据集成**: 集成GFS地面预报数据，支持电力预测
- **时序数据存储**: 使用阿里云Lindorm进行时序数据存储
- **API文档**: 集成Swagger/OpenAPI文档
- **数据安全**: RSA和AES加密支持

## 技术栈

- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL + MyBatis
- **时序数据库**: 阿里云Lindorm TSDB
- **API文档**: SpringDoc OpenAPI 3
- **加密**: RSA/AES加密工具
- **构建工具**: Maven
- **Java版本**: 1.8

## 项目结构

```
src/
├── main/
│   ├── java/com/virtualpowerplant/
│   │   ├── config/           # 配置类
│   │   ├── controller/       # REST控制器
│   │   ├── model/           # 数据模型
│   │   ├── service/         # 业务逻辑
│   │   ├── mapper/          # MyBatis映射器
│   │   └── utils/           # 工具类
│   └── resources/
│       ├── mapper/          # MyBatis XML映射文件
│       ├── sql/             # 数据库脚本
│       └── application.yml  # 应用配置
```

## 核心模块

### 设备管理
- `DeviceController`: 设备管理API
- `DeviceService`: 设备业务逻辑
- `Device`: 设备数据模型

### 实时数据采集
- `InverterRealTimeDataController`: 逆变器实时数据API
- `SunGrowDataService`: SunGrow数据服务
- `LindormTSDBService`: 时序数据存储服务

### 天气数据
- `GfsSurfaceDataService`: GFS地面预报数据服务
- `InverterGfsSurfaceLindormService`: 天气数据Lindorm存储

### 认证和安全
- `TokenController`: 认证token管理
- `RSAEncryptUtils`: RSA加密工具
- `AESEncryptUtils`: AES加密工具

## 快速开始

### 环境要求
- Java 1.8+
- Maven 3.6+
- MySQL 8.0+
- 阿里云Lindorm实例

### 配置数据库
1. 创建MySQL数据库
2. 执行初始化脚本：
   ```bash
   mysql -u username -p database_name < src/main/resources/sql/init.sql
   ```

### 配置应用
1. 修改 `src/main/resources/application.yml` 中的数据库连接信息
2. 配置Lindorm连接参数
3. 设置SunGrow API相关配置

### 运行应用
```bash
# 编译打包
mvn clean package

# 运行应用
java -jar target/virtual-power-plant-1.0.0.jar

# 或直接运行
mvn spring-boot:run
```

### 访问应用
- 应用地址: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html
- 健康检查: http://localhost:8080/hello

## API接口

### 设备管理
- `GET /devices` - 获取设备列表
- `POST /devices` - 创建设备
- `PUT /devices/{id}` - 更新设备
- `DELETE /devices/{id}` - 删除设备

### 实时数据
- `GET /inverter/realtime-data` - 获取逆变器实时数据
- `POST /inverter/sync-data` - 同步数据到Lindorm

### 认证
- `POST /token` - 获取访问token

## 数据模型

### 设备 (Device)
- 设备基本信息
- 位置坐标
- 运行状态

### 逆变器实时数据 (InverterRealTimeData)
- 功率数据
- 电压电流
- 温度湿度
- 时间戳

### 天气数据 (GfsSurfaceData)
- 温度
- 湿度
- 辐射强度
- 云量信息

## 开发说明

### 代码规范
- 遵循Spring Boot最佳实践
- 使用MyBatis进行数据库操作
- 统一异常处理
- 日志记录规范

### 测试
```bash
mvn test
```

### 部署
应用支持标准的Spring Boot部署方式，可以打包为JAR文件独立运行，也可以部署到Tomcat等应用服务器。

## 许可证

本项目使用 MIT 许可证，详细信息请查看 LICENSE 文件。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。

## 联系方式

如有问题或建议，请通过以下方式联系：
- 创建 GitHub Issue
- 发送邮件到项目维护者