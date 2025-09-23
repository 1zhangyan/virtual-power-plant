package com.virtualpowerplant.controller;

import com.virtualpowerplant.model.InverterRealTimeData;
import com.virtualpowerplant.model.InverterWeatherData;
import com.virtualpowerplant.service.InverterRealTimeDataService;
import com.virtualpowerplant.service.InverterGfsSurfaceLindormService;
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
@RequestMapping("/api/power-data")
@Tag(name = "功率数据查询", description = "逆变器和电站功率数据及天气预报查询接口")
public class PowerDataQueryController {

    private static final Logger logger = LoggerFactory.getLogger(PowerDataQueryController.class);

    @Autowired
    private InverterRealTimeDataService realTimeDataService;

    @Autowired
    private InverterGfsSurfaceLindormService weatherDataService;

    @GetMapping("/inverter/{inverterSn}/power")
    @Operation(summary = "按时间段和逆变器SN获取实时功率数据",
               description = "根据指定的时间段和逆变器序列号获取每分钟的实时功率数据")
    public ResponseEntity<Map<String, Object>> getInverterPowerByTimeRange(
            @Parameter(description = "逆变器序列号") @PathVariable String inverterSn,
            @Parameter(description = "开始时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        try {
            // 参数验证
            if (startTime.isAfter(endTime)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "开始时间不能晚于结束时间");
                return ResponseEntity.badRequest().body(response);
            }

            // 获取逆变器实时功率数据
            List<InverterRealTimeData> dataList = realTimeDataService.getInverterDataByTimeRange(inverterSn, startTime, endTime);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dataList);
            response.put("inverter_sn", inverterSn);
            response.put("start_time", startTime);
            response.put("end_time", endTime);
            response.put("count", dataList.size());
            response.put("message", "获取逆变器实时功率数据成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取逆变器实时功率数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/powerstation/{psKey}/power")
    @Operation(summary = "按时间段和电站PS_KEY获取实时功率数据",
               description = "根据指定的时间段和电站PS_KEY获取电站每分钟的实时功率数据")
    public ResponseEntity<Map<String, Object>> getPowerStationPowerByTimeRange(
            @Parameter(description = "电站PS_KEY") @PathVariable String psKey,
            @Parameter(description = "开始时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        try {
            // 参数验证
            if (startTime.isAfter(endTime)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "开始时间不能晚于结束时间");
                return ResponseEntity.badRequest().body(response);
            }

            // 获取电站实时功率数据 - 通过psKey查询该电站下所有逆变器的数据
            List<InverterRealTimeData> dataList = realTimeDataService.getPowerStationDataByTimeRange(psKey, startTime, endTime);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dataList);
            response.put("ps_key", psKey);
            response.put("start_time", startTime);
            response.put("end_time", endTime);
            response.put("count", dataList.size());
            response.put("message", "获取电站实时功率数据成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取电站实时功率数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/inverter/{inverterSn}/weather")
    @Operation(summary = "按时间段和逆变器SN获取天气预报",
               description = "根据指定的时间段和逆变器序列号获取对应位置的天气预报信息")
    public ResponseEntity<Map<String, Object>> getInverterWeatherByTimeRange(
            @Parameter(description = "逆变器序列号") @PathVariable String inverterSn,
            @Parameter(description = "开始时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        try {
            // 参数验证
            if (startTime.isAfter(endTime)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "开始时间不能晚于结束时间");
                return ResponseEntity.badRequest().body(response);
            }

            // 获取逆变器天气预报数据
            List<InverterWeatherData> dataList = weatherDataService.getInverterWeatherByTimeRange(inverterSn, startTime, endTime);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dataList);
            response.put("inverter_sn", inverterSn);
            response.put("start_time", startTime);
            response.put("end_time", endTime);
            response.put("count", dataList.size());
            response.put("message", "获取逆变器天气预报数据成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取逆变器天气预报数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/powerstation/{psKey}/weather")
    @Operation(summary = "按时间段和电站PS_KEY获取天气预报",
               description = "根据指定的时间段和电站PS_KEY获取电站位置的天气预报信息")
    public ResponseEntity<Map<String, Object>> getPowerStationWeatherByTimeRange(
            @Parameter(description = "电站PS_KEY") @PathVariable String psKey,
            @Parameter(description = "开始时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间，格式: yyyy-MM-dd HH:mm:ss")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        try {
            // 参数验证
            if (startTime.isAfter(endTime)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "开始时间不能晚于结束时间");
                return ResponseEntity.badRequest().body(response);
            }

            // 获取电站天气预报数据
            List<InverterWeatherData> dataList = weatherDataService.getPowerStationWeatherByTimeRange(psKey, startTime, endTime);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dataList);
            response.put("ps_key", psKey);
            response.put("start_time", startTime);
            response.put("end_time", endTime);
            response.put("count", dataList.size());
            response.put("message", "获取电站天气预报数据成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取电站天气预报数据失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}