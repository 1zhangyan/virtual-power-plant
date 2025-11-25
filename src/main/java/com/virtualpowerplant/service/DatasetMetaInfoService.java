package com.virtualpowerplant.service;


import com.virtualpowerplant.mapper.DatasetMetaInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;

@Service("DatasetMetaInfoService")
public class DatasetMetaInfoService {
    @Autowired
    private DatasetMetaInfoMapper datasetMetaInfoMapper;

    private List<String> allValidMetaType;

    private HashMap<String, List<String>> metaVarMap;

    public List<String> selectMetaVarByMetaType(String metaType) {
        if (null == metaVarMap) {
            metaVarMap = new HashMap<>();
        }
        if (!metaVarMap.containsKey(metaType)) {
            metaVarMap.put(metaType,datasetMetaInfoMapper.selectMetaVarByMetaType(metaType));
        }
        return metaVarMap.get(metaType);
    }

    public List<String> selectAllValidMetaType() {
        if (null == allValidMetaType || CollectionUtils.isEmpty(allValidMetaType)) {
            allValidMetaType =  datasetMetaInfoMapper.selectAllValidMetaType();
        }
        return allValidMetaType;
    }

    public List<String> selectMetaTypeByDatasetType(String datasetType) {
        return datasetMetaInfoMapper.selectMetaTypeByDatasetType(datasetType);
    }
}
