package com.virtualpowerplant.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class LindormConfig {

    private static final Logger logger = LoggerFactory.getLogger(LindormConfig.class);

    public static Connection connection;

    @PostConstruct
    public void init() {
        try {
            String url = SecretConfigManager.getLindormUrl();
            if (url == null || url.isEmpty()) {
                logger.warn("Lindorm URL not configured, will be disabled");
                return;
            }
            logger.info("初始化天气数据 Lindorm TSDB JDBC连接，URL: {}", url);
            connection = DriverManager.getConnection(url);
//            initWeatherTable();
            logger.info("天气数据 Lindorm TSDB JDBC连接初始化完成");
        } catch (Exception e) {
            logger.error("初始化天气数据 Lindorm TSDB JDBC连接失败: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Lindorm TSDB JDBC连接已关闭");
            } catch (SQLException e) {
                logger.error("关闭Lindorm TSDB JDBC连接失败: {}", e.getMessage(), e);
            }
        }
    }
}
