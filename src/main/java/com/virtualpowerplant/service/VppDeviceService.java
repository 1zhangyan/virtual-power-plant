package com.virtualpowerplant.service;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.virtualpowerplant.mapper.VppDeviceMapper;
import com.virtualpowerplant.model.VppDevice;

@Service
@Transactional
public class VppDeviceService {

    private static final Logger logger = LoggerFactory.getLogger(VppDeviceService.class);

    @Autowired
    private VppDeviceMapper vppDeviceMapper;

    @Autowired
    private VppService vppService;

    public List<VppDevice> getInvertersByVppId(Long vppId) {
        return vppDeviceMapper.selectInvertersByVppId(vppId);
    }

    /**
     * 查询所有带经纬度信息的逆变器
     */
    public List<String> getInverterLocations(Long vppId) {
        return vppDeviceMapper.selectInvertersByVppId(vppId)
                .stream()
                .filter(it -> it.getLatitudeStandard() > 0.0 && it.getLongitudeStandard() > 0.0)
                .map(it -> String.format("%s#%s", it.getLongitudeStandard(), it.getLatitudeStandard()))
                .distinct()
                .collect(Collectors.toList());
    }
}