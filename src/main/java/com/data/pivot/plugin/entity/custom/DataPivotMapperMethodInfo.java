package com.data.pivot.plugin.entity.custom;

import com.data.pivot.plugin.model.DataPivotCache;
import com.data.pivot.plugin.model.DataPivotStorage;
import com.data.pivot.plugin.tool.DataPivotStorageUtil;

import java.util.List;

public class DataPivotMapperMethodInfo extends DataPivotInfo implements DataPivotStorage<List<DataPivotMapperMethodInfo>>, DataPivotCache {
    private String type;//注解 or 脚本
    private String[] customCode;//注解 or 脚本 的code
    private List<DataPivotCustomAnnotationInfo> customAnnotation;
    private DataPivotCustomScriptInfo defaultMethod;//优先级最低
    private DataPivotCustomScriptInfo customScript;

    public DataPivotCustomScriptInfo getDefaultMethod() {
        return defaultMethod;
    }

    public void setDefaultMethod(DataPivotCustomScriptInfo defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getCustomCode() {
        return customCode;
    }

    public void setCustomCode(String[] customCode) {
        this.customCode = customCode;
    }

    public List<DataPivotCustomAnnotationInfo> getCustomAnnotation() {
        return customAnnotation;
    }

    public void setCustomAnnotation(List<DataPivotCustomAnnotationInfo> customAnnotation) {
        this.customAnnotation = customAnnotation;
    }

    public DataPivotCustomScriptInfo getCustomScript() {
        return customScript;
    }

    public void setCustomScript(DataPivotCustomScriptInfo customScript) {
        this.customScript = customScript;
    }

    @Override
    public List<DataPivotMapperMethodInfo> query() {
        return DataPivotStorageUtil.getListData(DataPivotMapperMethodInfo.class.getName(), DataPivotMapperMethodInfo.class);
    }

    @Override
    public void save(List<DataPivotMapperMethodInfo> dataPivotMapperMethodInfos) {
        DataPivotStorageUtil.setListData(DataPivotMapperMethodInfo.class.getName(),dataPivotMapperMethodInfos);
    }
}
