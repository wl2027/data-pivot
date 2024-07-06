package com.data.pivot.plugin.model;

import com.data.pivot.plugin.entity.DataPivotMappingSettingInfo;
import com.data.pivot.plugin.entity.custom.DataPivotStrategyInfo;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.openapi.module.Module;
import java.util.List;

public class DataPivotObject {
    private DataPivotStrategyInfo dataPivotStrategyInfo;
    private DataPivotMappingSettingInfo dataPivotMappingSettingInfo;
    private PsiElement psiElement;
    private Module module ;
    private String moduleName ;
    private String packageName ;
    private PsiClass currentClass ;
    private PsiField currentPsiField;
    private List<PsiField> psiFieldList ;
    private String currentClassName ;
    private String currentFieldName ;
    private String packageReference ;//模块/包
    private String fieldReference ;//模块/包.类#字段

    public DataPivotStrategyInfo getDataPivotStrategyInfo() {
        return dataPivotStrategyInfo;
    }

    public void setDataPivotStrategyInfo(DataPivotStrategyInfo dataPivotStrategyInfo) {
        this.dataPivotStrategyInfo = dataPivotStrategyInfo;
    }

    public DataPivotMappingSettingInfo getDataPivotMappingSettingInfo() {
        return dataPivotMappingSettingInfo;
    }

    public void setDataPivotMappingSettingInfo(DataPivotMappingSettingInfo dataPivotMappingSettingInfo) {
        this.dataPivotMappingSettingInfo = dataPivotMappingSettingInfo;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }

    public void setPsiElement(PsiElement psiElement) {
        this.psiElement = psiElement;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public PsiClass getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(PsiClass currentClass) {
        this.currentClass = currentClass;
    }

    public PsiField getCurrentPsiField() {
        return currentPsiField;
    }

    public void setCurrentPsiField(PsiField currentPsiField) {
        this.currentPsiField = currentPsiField;
    }

    public List<PsiField> getPsiFieldList() {
        return psiFieldList;
    }

    public void setPsiFieldList(List<PsiField> psiFieldList) {
        this.psiFieldList = psiFieldList;
    }

    public String getCurrentClassName() {
        return currentClassName;
    }

    public void setCurrentClassName(String currentClassName) {
        this.currentClassName = currentClassName;
    }

    public String getCurrentFieldName() {
        return currentFieldName;
    }

    public void setCurrentFieldName(String currentFieldName) {
        this.currentFieldName = currentFieldName;
    }

    public String getPackageReference() {
        return packageReference;
    }

    public void setPackageReference(String packageReference) {
        this.packageReference = packageReference;
    }

    public String getFieldReference() {
        return fieldReference;
    }

    public void setFieldReference(String fieldReference) {
        this.fieldReference = fieldReference;
    }
}
