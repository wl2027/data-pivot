package com.datapivot.plugin.entity.custom;

import com.datapivot.plugin.model.DataPivotCache;
import com.datapivot.plugin.model.DataPivotStorage;
import com.datapivot.plugin.tool.DataPivotStorageUtil;

import java.util.List;
import java.util.Set;

public class DataPivotCustomAnnotationInfo extends DataPivotInfo implements DataPivotStorage<List<DataPivotCustomAnnotationInfo>>, DataPivotCache {
    private String annotationQualifiedName;
    private List<String> annotationParameters;
    private String annotationParameterHandleFunction;//是否大小写等...可关联DataPivotCustomScriptInfo

    public String getAnnotationQualifiedName() {
        return annotationQualifiedName;
    }

    public void setAnnotationQualifiedName(String annotationQualifiedName) {
        this.annotationQualifiedName = annotationQualifiedName;
    }

    public List<String> getAnnotationParameters() {
        return annotationParameters;
    }

    public void setAnnotationParameters(List<String> annotationParameters) {
        this.annotationParameters = annotationParameters;
    }

    public String getAnnotationParameterHandleFunction() {
        return annotationParameterHandleFunction;
    }

    public void setAnnotationParameterHandleFunction(String annotationParameterHandleFunction) {
        this.annotationParameterHandleFunction = annotationParameterHandleFunction;
    }

    @Override
    public List<DataPivotCustomAnnotationInfo> query() {
        return DataPivotStorageUtil.getListData(DataPivotCustomAnnotationInfo.class.getName(), DataPivotCustomAnnotationInfo.class);
    }

    @Override
    public void save(List<DataPivotCustomAnnotationInfo> dataPivotCustomAnnotationInfos) {
        DataPivotStorageUtil.setListData(DataPivotCustomAnnotationInfo.class.getName(),dataPivotCustomAnnotationInfos);
    }
}
