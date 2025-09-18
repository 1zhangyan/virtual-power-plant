# 数据库配置说明

## SecretConfigManager 配置

本项目使用 `SecretConfigManager` 来管理所有敏感配置信息，包括 MySQL 数据库连接配置。

### 1. 创建 secret.config 文件

在用户主目录下创建 `~/secret.config` 文件：

```bash
touch ~/secret.config
```

### 2. 配置 MySQL 连接信息

编辑 `~/secret.config` 文件，添加以下 JSON 配置：

```json
{
  "weather": {
    "api_token": "your_weather_api_token_here"
  },
  "sungrow": {
    "username": "your_sungrow_username",
    "password": "your_sungrow_password",
    "app_key": "your_sungrow_app_key",
    "base_url": "https://gateway.isolarcloud.com.hk",
    "public_key": "your_sungrow_public_key",
    "access_key": "your_sungrow_access_key",
    "aes_key": "your_sungrow_aes_key"
  },
  "mysql": {
    "url": "jdbc:mysql://localhost:3306/vpp?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai",
    "username": "root",
    "password": "your_mysql_password"
  }
}
```

### 3. MySQL 数据库准备

#### 方式一：手动创建数据库
```sql
CREATE DATABASE vpp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 方式二：使用初始化脚本
```bash
mysql -u root -p < src/main/resources/sql/init.sql
```

### 4. 配置说明

- **mysql.url**: 数据库连接 URL，包含数据库名称和连接参数
- **mysql.username**: 数据库用户名
- **mysql.password**: 数据库密码

### 5. 连接池配置

系统使用 HikariCP 连接池，配置如下：
- 最大连接数：20
- 最小空闲连接：5
- 连接超时：30秒
- 空闲超时：10分钟
- 连接最大生命周期：30分钟

### 6. 启动应用

配置完成后，启动 Spring Boot 应用：

```bash
./mvnw spring-boot:run
```

### 7. 验证配置

启动成功后，可以通过以下方式验证数据库连接：

1. 查看应用日志，确认没有数据库连接错误
2. 访问 Swagger UI：http://localhost:8080/swagger-ui.html
3. 测试设备相关 API 接口

### 8. 常见问题

#### 连接被拒绝
- 检查 MySQL 服务是否启动
- 确认连接 URL、用户名、密码是否正确
- 检查防火墙设置

#### 数据库不存在
- 手动创建 `vpp` 数据库
- 或使用 `init.sql` 脚本自动创建

#### 字符编码问题
- 确保数据库使用 `utf8mb4` 字符集
- 连接 URL 中包含正确的编码参数

### 9. 安全注意事项

- `secret.config` 文件包含敏感信息，请确保文件权限设置为仅当前用户可读
- 不要将 `secret.config` 文件提交到版本控制系统
- 生产环境中建议使用更安全的配置管理方案