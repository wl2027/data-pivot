package com.datapivot.plugin.entity.custom;

import com.datapivot.plugin.model.DataPivotCache;
import com.datapivot.plugin.model.DataPivotStorage;
import com.datapivot.plugin.tool.DataPivotStorageUtil;

import java.util.List;

public class DataPivotCustomSqlInfo extends DataPivotInfo implements DataPivotStorage<List<DataPivotCustomSqlInfo>>, DataPivotCache {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public List<DataPivotCustomSqlInfo> query() {
        return DataPivotStorageUtil.getListData(DataPivotCustomSqlInfo.class.getName(), DataPivotCustomSqlInfo.class);
    }

    @Override
    public void save(List<DataPivotCustomSqlInfo> dataPivotCustomSqlInfos) {
        DataPivotStorageUtil.setListData(DataPivotCustomSqlInfo.class.getName(),dataPivotCustomSqlInfos);
    }
}
