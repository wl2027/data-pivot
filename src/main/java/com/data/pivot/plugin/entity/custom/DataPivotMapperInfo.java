package com.data.pivot.plugin.entity.custom;


import com.data.pivot.plugin.model.DataPivotCache;
import com.data.pivot.plugin.model.DataPivotStorage;
import com.data.pivot.plugin.tool.DataPivotStorageUtil;

import java.util.List;

public class DataPivotMapperInfo  extends DataPivotInfo implements DataPivotStorage<List<DataPivotMapperInfo>>, DataPivotCache {
    /**
     * MapperMethod
     */
    private String structMethodCode;//结构名:表名or类名
    private DataPivotMapperMethodInfo structMethod;//结构名:表名or类名
    private String elementMethodCode;//元素名:属性or字段
    private DataPivotMapperMethodInfo elementMethod;//元素名:属性or字段

    public DataPivotMapperMethodInfo getStructMethod() {
        return structMethod;
    }

    public void setStructMethod(DataPivotMapperMethodInfo structMethod) {
        this.structMethod = structMethod;
    }

    public DataPivotMapperMethodInfo getElementMethod() {
        return elementMethod;
    }

    public void setElementMethod(DataPivotMapperMethodInfo elementMethod) {
        this.elementMethod = elementMethod;
    }

    public String getStructMethodCode() {
        return structMethodCode;
    }

    public void setStructMethodCode(String structMethodCode) {
        this.structMethodCode = structMethodCode;
    }

    public String getElementMethodCode() {
        return elementMethodCode;
    }

    public void setElementMethodCode(String elementMethodCode) {
        this.elementMethodCode = elementMethodCode;
    }

    @Override
    public List<DataPivotMapperInfo> query() {
        return DataPivotStorageUtil.getListData(DataPivotMapperInfo.class.getName(), DataPivotMapperInfo.class);
    }

    @Override
    public void save(List<DataPivotMapperInfo> dataPivotMapperInfos) {
        DataPivotStorageUtil.setListData(DataPivotMapperInfo.class.getName(),dataPivotMapperInfos);
    }
}
