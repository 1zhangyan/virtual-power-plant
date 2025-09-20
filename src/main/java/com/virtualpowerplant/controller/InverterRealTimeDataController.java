package com.virtualpowerplant.controller;

import com.virtualpowerplant.model.InverterRealTimeData;
import com.virtualpowerplant.service.InverterRealTimeDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inverter/realtime")
@Tag(name = "逆变器实时数据", description = "逆变器实时功率指标数据管理（基于Lindorm时序数据库）")
public class InverterRealTimeDataController {

    private static final Logger logger = LoggerFactory.getLogger(InverterRealTimeDataController.class);

    @Autowired
    private InverterRealTimeDataService realTimeDataService;

    @GetMapping("/latest")
    @Operation(summary = "获取最新实时数据", description = "获取指定数量的最新逆变器实时数据")
    public ResponseEntity<Map<String, Object>> getLatestData(
            @Parameter(description = "数据条数，默认50") @RequestParam(defaultValue = "50") int limit) {
        try {
            if (limit <= 0 || limit > 1000) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "limit参数必须在1-1000之间");
                return ResponseEntity.badRequest().body(response);
            }

            List<InverterRealTimeData> dataList = realTimeDataService.getLatestData(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dataList);
            response.put("count", dataList.size());
            response.put("message", "获取最新实时数据成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取最新实时数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/inverter/{inverterSn}/latest")
    @Operation(summary = "获取逆变器最新实时数据", description = "获取指定逆变器的最新实时数据")
    public ResponseEntity<Map<String, Object>> getLatestInverterData(
            @Parameter(description = "逆变器序列号") @PathVariable String inverterSn,
            @Parameter(description = "数据条数，默认10") @RequestParam(defaultValue = "10") int limit) {
        try {
            if (limit <= 0 || limit > 100) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "limit参数必须在1-100之间");
                return ResponseEntity.badRequest().body(response);
            }

            List<InverterRealTimeData> dataList = realTimeDataService.getLatestInverterData(inverterSn, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dataList);
            response.put("inverter_sn", inverterSn);
            response.put("count", dataList.size());
            response.put("message", "获取逆变器最新实时数据成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取逆变器最新实时数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/powerstation/{psKey}/latest")
    @Operation(summary = "获取电站最新实时数据", description = "获取指定电站的最新实时数据")
    public ResponseEntity<Map<String, Object>> getLatestPowerStationData(
            @Parameter(description = "电站Key") @PathVariable String psKey,
            @Parameter(description = "数据条数，默认50") @RequestParam(defaultValue = "50") int limit) {
        try {
            if (limit <= 0 || limit > 1000) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "limit参数必须在1-1000之间");
                return ResponseEntity.badRequest().body(response);
            }

            List<InverterRealTimeData> dataList = realTimeDataService.getLatestPowerStationData(psKey, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dataList);
            response.put("ps_key", psKey);
            response.put("count", dataList.size());
            response.put("message", "获取电站最新实时数据成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取电站最新实时数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/range")
    @Operation(summary = "根据时间范围获取实时数据", description = "获取指定时间范围内的所有逆变器实时数据")
    public ResponseEntity<Map<String, Object>> getDataByTimeRange(
            @Parameter(description = "开始时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            if (startTime.isAfter(endTime)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "开始时间不能晚于结束时间");
                return ResponseEntity.badRequest().body(response);
            }

            List<InverterRealTimeData> dataList = realTimeDataService.getDataByTimeRange(startTime, endTime);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dataList);
            response.put("start_time", startTime);
            response.put("end_time", endTime);
            response.put("count", dataList.size());
            response.put("message", "根据时间范围获取实时数据成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("根据时间范围获取实时数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/inverter/{inverterSn}/range")
    @Operation(summary = "根据逆变器和时间范围获取实时数据", description = "获取指定逆变器在指定时间范围内的实时数据")
    public ResponseEntity<Map<String, Object>> getInverterDataByTimeRange(
            @Parameter(description = "逆变器序列号") @PathVariable String inverterSn,
            @Parameter(description = "开始时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            if (startTime.isAfter(endTime)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "开始时间不能晚于结束时间");
                return ResponseEntity.badRequest().body(response);
            }

            List<InverterRealTimeData> dataList = realTimeDataService.getInverterDataByTimeRange(inverterSn, startTime, endTime);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dataList);
            response.put("inverter_sn", inverterSn);
            response.put("start_time", startTime);
            response.put("end_time", endTime);
            response.put("count", dataList.size());
            response.put("message", "根据逆变器和时间范围获取实时数据成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("根据逆变器和时间范围获取实时数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/collect")
    @Operation(summary = "手动触发实时数据收集", description = "手动触发一次逆变器实时数据收集任务")
    public ResponseEntity<Map<String, Object>> manualCollect() {
        try {
            realTimeDataService.collectInverterRealTimeData();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "手动触发实时数据收集成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("手动触发实时数据收集失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}