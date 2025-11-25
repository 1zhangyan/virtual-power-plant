package com.virtualpowerplant.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DatasetMetaInfoMapper {

    @Select("SELECT meta_var FROM vpp.dataset_meta_info WHERE meta_type = #{metaType} group by meta_var")
    List<String> selectMetaVarByMetaType(String metaType);

    @Select("SELECT meta_type FROM vpp.dataset_meta_info WHERE dataset_type = #{datasetType} group by meta_type")
    List<String> selectMetaTypeByDatasetType(String datasetType);

    @Select("SELECT meta_type FROM vpp.dataset_meta_info group by meta_type ")
    List<String> selectAllValidMetaType();


}