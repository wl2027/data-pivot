package com.data.pivot.plugin.entity;

import com.data.pivot.plugin.enums.DBType;

import java.util.List;

public class DatabaseQueryConfig {
    private String dataSourceId;
    private DBType dbType;
    private String url;
    private String user;
    private String password;
    private String dbName;
    private String schema;
    private String tableName;
    private List<String> columns;
    private String conditionField;
    private String likeValue;
    private String sql;

    public DatabaseQueryConfig(String dataSourceId, DBType dbType, String url, String user, String password,
                               String dbName, String schema, String tableName, List<String> columns,
                               String conditionField) {
        this(dataSourceId, dbType, url, user, password, dbName, schema, tableName, columns, conditionField, null, null);
    }

    public DatabaseQueryConfig(String dataSourceId, DBType dbType, String url, String user, String password,
                               String dbName, String schema, String tableName, List<String> columns,
                               String conditionField, String likeValue, String sql) {
        this.dataSourceId = dataSourceId;
        this.dbType = dbType;
        this.url = url;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.schema = schema;
        this.tableName = tableName;
        this.columns = columns;
        this.conditionField = conditionField;
        this.likeValue = likeValue;
        this.sql = sql;
    }

    public void setLikeValue(String likeValue) {
        this.likeValue = likeValue;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }

    public DBType getDbType() {
        return dbType;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }

    public String getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public String getConditionField() {
        return conditionField;
    }

    public String getLikeValue() {
        return likeValue;
    }

    public String getSql() {
        return sql;
    }

    public boolean isDirectSqlQuery() {
        return sql != null && !sql.isEmpty();
    }
}
