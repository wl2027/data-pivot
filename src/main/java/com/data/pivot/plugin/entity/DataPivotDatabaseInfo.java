package com.data.pivot.plugin.entity;

import com.data.pivot.plugin.model.DataPivotCache;

import javax.sql.DataSource;
import javax.swing.*;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class DataPivotDatabaseInfo implements DataPivotCache {
    private String uniqueId;//DG-ID
    private String driverClassName;//驱动类
    private Icon icon; //图标
    private String dataSourceName;//数据源名
    private String dataSourceVersion;//数据源名
    private String databaseName;//数据库名
    private String databasePath;//数据源名/数据库名
    private String userName;//用户名
    private String password;//密码
    private String databaseType;//类型
    private String databaseDriver;//驱动
    private Map<String,String> databaseParam;//参数
    private String url;//参数
    private List<String> tableId ;//数据库表ids:取Table
    private Connection connection ;//数据源连接
    private DataSource dataSource ;//数据源连接
    private String databaseReference;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceVersion() {
        return dataSourceVersion;
    }

    public void setDataSourceVersion(String dataSourceVersion) {
        this.dataSourceVersion = dataSourceVersion;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    public void setDatabaseDriver(String databaseDriver) {
        this.databaseDriver = databaseDriver;
    }

    public Map<String, String> getDatabaseParam() {
        return databaseParam;
    }

    public void setDatabaseParam(Map<String, String> databaseParam) {
        this.databaseParam = databaseParam;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTableId() {
        return tableId;
    }

    public void setTableId(List<String> tableId) {
        this.tableId = tableId;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(String databaseReference) {
        this.databaseReference = databaseReference;
    }
}
