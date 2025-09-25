package com.virtualpowerplant.service;

import com.virtualpowerplant.mapper.VppMapper;
import com.virtualpowerplant.model.VirtualPowerPlant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class VppService {

    private static final Logger logger = LoggerFactory.getLogger(VppService.class);

    @Autowired
    private VppMapper vppMapper;

    /**
     * 插入或更新虚拟电厂信息
     */
    public Long findOrInsert(VirtualPowerPlant vpp) {
        logger.info("执行虚拟电厂信息upsert操作: {}", vpp.toString());

        LocalDateTime now = LocalDateTime.now();
        if (vpp.getCreatedAt() == null) {
            vpp.setCreatedAt(now);
        }
        vpp.setUpdatedAt(now);
        VirtualPowerPlant result = vppMapper.findByUserAccount(vpp.getUserAccount());
        if (result != null) {
            logger.info("已经存在虚拟电厂 vppId: {}", result.getVppId());
            return result.getVppId();
        } else {
            return vppMapper.insert(vpp);
        }
    }

    /**
     * 根据用户ID、用户账号或VPP ID获取虚拟电厂详细信息
     */
    public VirtualPowerPlant getVppDetails(String userId, String userAccount, Long vppId) {
        logger.info("查询虚拟电厂详细信息 - userId: {}, userAccount: {}, vppId: {}", userId, userAccount, vppId);

        VirtualPowerPlant vpp = null;

        // 优先按vppId查询
        if (vppId != null) {
            vpp = vppMapper.findByVppId(vppId);
            if (vpp != null) {
                logger.info("通过vppId {} 查询到虚拟电厂信息", vppId);
                return vpp;
            }
        }

        // 按userId查询
        if (userId != null && !userId.trim().isEmpty()) {
            vpp = vppMapper.findByUserId(userId);
            if (vpp != null) {
                logger.info("通过userId {} 查询到虚拟电厂信息", userId);
                return vpp;
            }
        }

        // 按userAccount查询
        if (userAccount != null && !userAccount.trim().isEmpty()) {
            vpp = vppMapper.findByUserAccount(userAccount);
            if (vpp != null) {
                logger.info("通过userAccount {} 查询到虚拟电厂信息", userAccount);
                return vpp;
            }
        }

        logger.warn("未找到匹配的虚拟电厂信息 - userId: {}, userAccount: {}, vppId: {}", userId, userAccount, vppId);
        return null;
    }
}