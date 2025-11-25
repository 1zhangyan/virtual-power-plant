package com.virtualpowerplant.service;

import java.util.List;
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
}