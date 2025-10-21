package com.virtualpowerplant.controller;

import com.virtualpowerplant.model.InverterRealTimeData;
import com.virtualpowerplant.service.InverterDataLindormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inverter/realtime")
@Tag(name = "设备实时功率数据", description = "逆变器实时功率指标数据管理（基于Lindorm时序数据库）")
public class InverterPowerDataController {

    private static final Logger logger = LoggerFactory.getLogger(InverterPowerDataController.class);

    @Autowired
    private InverterDataLindormService inverterDataLindormService;

    @PostMapping("/realTime/getInverterPowerTimeRange")
    @Operation(summary = "获取指定逆变器时间范围内的实时数据", description = "获取指定逆变器时间范围内的实时数据")
    public ResponseEntity<Map<String, Object>> getInverterPowerDataTimeRange(
            @RequestParam String inverterSn,
            @RequestParam long startTime,
            @RequestParam long endTime) {
        try {
            if (startTime > endTime ) {
                throw new RuntimeException("开始时间要小于结束时间");
            }
            List<InverterRealTimeData> inverterRealTimeDatas = inverterDataLindormService.queryBySNTimeRange(inverterSn, startTime, endTime);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("power_data", inverterRealTimeDatas);
            response.put("inverter_sn", inverterSn);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取逆变器最新实时数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

}