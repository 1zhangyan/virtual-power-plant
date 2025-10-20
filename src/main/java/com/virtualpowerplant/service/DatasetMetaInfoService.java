package com.virtualpowerplant.service;


import com.virtualpowerplant.mapper.DatasetMetaInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DatasetMetaInfoService")
public class DatasetMetaInfoService {
    @Autowired
    private DatasetMetaInfoMapper datasetMetaInfoMapper;

    public List<String> selectMetaVarByMetaType(String metaType) {
        return datasetMetaInfoMapper.selectMetaVarByMetaType(metaType);
    }

    public List<String> selectAllValidMetaType() {
        return datasetMetaInfoMapper.selectAllValidMetaType();
    }

    public List<String> selectMetaTypeByDatasetType(String datasetType) {
        return datasetMetaInfoMapper.selectMetaTypeByDatasetType(datasetType);
    }
}
