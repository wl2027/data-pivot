package com.data.pivot.plugin.model;

import com.data.pivot.plugin.entity.DataPivotMappingSettingInfo;
import com.data.pivot.plugin.entity.custom.DataPivotStrategyInfo;
import com.intellij.database.psi.DbColumn;

import java.util.List;

/**
 * 映射关系实体
 */
public class DataPivotRelation implements DataPivotCache {
    private DataPivotStrategyInfo dataPivotStrategyInfo;
    private DataPivotMappingSettingInfo dataPivotMappingSettingInfo;
    private String databaseReference;
    private String uniqueId;
    private String databaseName;
    private String tableName;
    private String columnName;
    private List<String> columnList;
    private DbColumn dbColumn;

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

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public String getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(String databaseReference) {
        this.databaseReference = databaseReference;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DbColumn getDbColumn() {
        return dbColumn;
    }

    public void setDbColumn(DbColumn dbColumn) {
        this.dbColumn = dbColumn;
    }
}
