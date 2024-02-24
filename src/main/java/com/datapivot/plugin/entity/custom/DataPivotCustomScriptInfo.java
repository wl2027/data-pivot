package com.datapivot.plugin.entity.custom;

import com.datapivot.plugin.model.DataPivotCache;
import com.datapivot.plugin.model.DataPivotStorage;
import com.datapivot.plugin.tool.DataPivotStorageUtil;

import java.util.List;

public class DataPivotCustomScriptInfo extends DataPivotInfo implements DataPivotStorage<List<DataPivotCustomScriptInfo>>, DataPivotCache {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public List<DataPivotCustomScriptInfo> query() {
        return DataPivotStorageUtil.getListData(DataPivotCustomScriptInfo.class.getName(), DataPivotCustomScriptInfo.class);
    }

    @Override
    public void save(List<DataPivotCustomScriptInfo> dataPivotCustomScriptInfos) {
        DataPivotStorageUtil.setListData(DataPivotCustomScriptInfo.class.getName(),dataPivotCustomScriptInfos);
    }
}
