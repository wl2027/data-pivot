package com.datapivot.plugin.entity.custom;

import com.datapivot.plugin.model.DataPivotCache;
import com.datapivot.plugin.model.DataPivotStorage;
import com.datapivot.plugin.tool.DataPivotStorageUtil;

import java.util.List;

public class DataPivotStrategyInfo extends DataPivotInfo implements DataPivotStorage<List<DataPivotStrategyInfo>>, DataPivotCache {
    private String ormMapperCode;//mapper
    private DataPivotMapperInfo ormMapper;//mapper
    private String romMapperCode;
    private DataPivotMapperInfo romMapper;

    public DataPivotMapperInfo getOrmMapper() {
        return ormMapper;
    }

    public void setOrmMapper(DataPivotMapperInfo ormMapper) {
        this.ormMapper = ormMapper;
    }

    public DataPivotMapperInfo getRomMapper() {
        return romMapper;
    }

    public void setRomMapper(DataPivotMapperInfo romMapper) {
        this.romMapper = romMapper;
    }

    public String getOrmMapperCode() {
        return ormMapperCode;
    }

    public void setOrmMapperCode(String ormMapperCode) {
        this.ormMapperCode = ormMapperCode;
    }

    public String getRomMapperCode() {
        return romMapperCode;
    }

    public void setRomMapperCode(String romMapperCode) {
        this.romMapperCode = romMapperCode;
    }

    @Override
    public List<DataPivotStrategyInfo> query() {
        return DataPivotStorageUtil.getListData(DataPivotStrategyInfo.class.getName(), DataPivotStrategyInfo.class);
    }

    @Override
    public void save(List<DataPivotStrategyInfo> dataPivotStrategyInfos) {
        DataPivotStorageUtil.setListData(DataPivotStrategyInfo.class.getName(),dataPivotStrategyInfos);
    }
}
