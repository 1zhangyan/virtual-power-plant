package com.virtualpowerplant.controller;

import com.virtualpowerplant.model.VirtualPowerPlant;
import com.virtualpowerplant.service.VppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vpp")
@Tag(name = "虚拟电厂管理", description = "虚拟电厂信息的创建、更新和查询接口")
public class VppController {

    private static final Logger logger = LoggerFactory.getLogger(VppController.class);

    @Autowired
    private VppService vppService;

    @PostMapping("/upsert")
    @Operation(summary = "创建或更新虚拟电厂", description = "插入新的虚拟电厂信息或更新已存在的虚拟电厂信息")
    public ResponseEntity<VirtualPowerPlant> upsertVpp(@RequestBody VirtualPowerPlant vpp) {
        try {
            logger.info("收到虚拟电厂upsert请求: {}", vpp.toString());
            VirtualPowerPlant result = vppService.upsert(vpp);
            logger.info("虚拟电厂upsert成功，返回结果: {}", result.toString());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("虚拟电厂upsert失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/details")
    @Operation(summary = "获取虚拟电厂详细信息",
               description = "根据userId、userAccount或vppId获取虚拟电厂详细信息，优先级: vppId > userId > userAccount")
    public ResponseEntity<VirtualPowerPlant> getVppDetails(
            @Parameter(description = "用户ID") @RequestParam(value = "userId", required = false) String userId,
            @Parameter(description = "用户账号") @RequestParam(value = "userAccount", required = false) String userAccount,
            @Parameter(description = "虚拟电厂ID") @RequestParam(value = "vppId", required = false) Long vppId) {

            logger.info("收到虚拟电厂详细信息查询请求 - userId: {}, userAccount: {}, vppId: {}",
                       userId, userAccount, vppId);
            if ((userId == null || userId.trim().isEmpty()) &&
                (userAccount == null || userAccount.trim().isEmpty()) &&
                vppId == null) {
                throw new RuntimeException("参数不能全空");
            }

            VirtualPowerPlant vpp = vppService.getVppDetails(userId, userAccount, vppId);
            if (vpp != null) {
                return ResponseEntity.ok(vpp);
            } else {
                throw new RuntimeException("未找到匹配的虚拟电厂信息");
            }
    }
}