package com.virtualpowerplant.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        // 从 SecretConfigManager 获取数据库配置
        String url = SecretConfigManager.getMysqlUrl();
        String username = SecretConfigManager.getMysqlUsername();
        String password = SecretConfigManager.getMysqlPassword();

        if (url == null || username == null || password == null) {
            throw new RuntimeException("MySQL配置信息不完整，请检查secret.config文件中的mysql配置");
        }

        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // HikariCP 连接池配置
        dataSource.setMaximumPoolSize(20);
        dataSource.setMinimumIdle(5);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);

        // 连接验证
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.setValidationTimeout(5000);

        return dataSource;
    }
}