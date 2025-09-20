package com.virtualpowerplant;

import com.virtualpowerplant.service.InverterRealTimeDataService;
import com.virtualpowerplant.model.InverterRealTimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 逆变器实时数据演示程序
 * 演示基于Lindorm时序数据库的定时收集功能和数据查询功能
 */
@Component
public class InverterRealTimeDemo implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InverterRealTimeDemo.class);

    @Autowired
    private InverterRealTimeDataService realTimeDataService;

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "demo".equals(args[0])) {
            logger.info("=== 逆变器实时数据功能演示（基于Lindorm时序数据库）===");

            // 等待一段时间让第一次定时任务执行
            logger.info("等待定时任务执行...");
            Thread.sleep(5000);

            // 演示查询最新数据
            logger.info("\\n步骤1: 查询最新10条实时数据...");
            List<InverterRealTimeData> latestData = realTimeDataService.getLatestData(10);
            logger.info("获取到 {} 条最新数据", latestData.size());

            if (!latestData.isEmpty()) {
                logger.info("最新数据示例:");
                InverterRealTimeData sample = latestData.get(0);
                logger.info("  电站名称: {}", sample.getPsName());
                logger.info("  电站Key: {}", sample.getPsKey());
                logger.info("  经纬度: [{}, {}]", sample.getLatitude(), sample.getLongitude());
                logger.info("  逆变器序列号: {}", sample.getInverterSn());
                logger.info("  实时有功功率(p24): {} kW", sample.getActivePower());
                logger.info("  设备时间: {}", sample.getDeviceTime());
            }

            // 演示手动触发收集
            logger.info("\\n步骤2: 手动触发实时数据收集...");
            realTimeDataService.collectInverterRealTimeData();

            // 演示时间范围查询
            logger.info("\\n步骤3: 查询过去1小时的数据...");
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusHours(1);
            List<InverterRealTimeData> rangeData = realTimeDataService.getDataByTimeRange(startTime, endTime);
            logger.info("过去1小时内共有 {} 条实时数据", rangeData.size());

            logger.info("\\n=== 演示完成 ===");
            logger.info("定时任务将每60秒自动收集一次实时数据");
            logger.info("数据特点:");
            logger.info("  - 从SunGrow API的p24字段提取有功功率");
            logger.info("  - 解析YYYYmmDDHHmmss格式的device_time为时间戳");
            logger.info("  - 存储电站名、电站Key、电站经纬度、逆变器SN、有功功率和设备时间");
            logger.info("  - 使用Lindorm时序数据库存储，支持高性能时序查询");
            logger.info("");
            logger.info("可通过以下API访问数据:");
            logger.info("  GET /api/inverter/realtime/latest - 获取最新数据");
            logger.info("  GET /api/inverter/realtime/inverter/{sn}/latest - 获取指定逆变器最新数据");
            logger.info("  GET /api/inverter/realtime/powerstation/{psKey}/latest - 获取指定电站最新数据");
            logger.info("  GET /api/inverter/realtime/range - 按时间范围查询");
            logger.info("  GET /api/inverter/realtime/inverter/{sn}/range - 按逆变器和时间范围查询");
            logger.info("  POST /api/inverter/realtime/collect - 手动触发收集");
        }
    }
}