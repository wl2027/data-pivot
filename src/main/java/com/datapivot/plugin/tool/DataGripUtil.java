package com.datapivot.plugin.tool;

import cn.hutool.core.util.ReflectUtil;
import com.datapivot.plugin.constants.DataPivotConstants;
import com.datapivot.plugin.entity.DataPivotDatabaseInfo;
import com.datapivot.plugin.i18n.DataPivotBundle;
import com.intellij.database.cli.DbCliUtil;
import com.intellij.database.dataSource.DataSourceSchemaMapping;
import com.intellij.database.dataSource.DatabaseDriver;
import com.intellij.database.dataSource.DatabaseDriverImpl;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.model.ObjectName;
import com.intellij.database.util.TreePatternNode;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataGripUtil {
    public static List<DataPivotDatabaseInfo> loadDatabaseInfo(LocalDataSource dataSource) {
        String dataSourceName = dataSource.getName();
        String version = dataSource.getVersion().toString();
        Icon icon = dataSource.getDbms().getIcon();
        String type = dataSource.getDbms().getName();
        List<DataPivotDatabaseInfo> dataPivotDatabaseInfoList = new ArrayList<>();
        String password = DbCliUtil.getPassword((LocalDataSource) dataSource);
        DatabaseDriver databaseDriver = dataSource.getDatabaseDriver();
        String driverClassName = dataSource.getDatabaseDriver().getDriverClass();
        String uniqueId = dataSource.getUniqueId();
        String username = dataSource.getUsername();
        //是否需要类加载?
        try {
            Class.forName(dataSource.getDriverClass());
        } catch (ClassNotFoundException e) {
            MessageUtil.Notice.error(DataPivotBundle.message("data.pivot.notice.connection.driver.null",driverClassName));
            return dataPivotDatabaseInfoList;
        }
        Map<String, String> driverProperties = ((DatabaseDriverImpl) databaseDriver).getDriverProperties();
        TreePatternNode.Group group = ((DataSourceSchemaMapping) ((LocalDataSource) dataSource).getSchemaMapping()).getIntrospectionScope().root.groups[0];
        Object[] children = (Object[]) ReflectUtil.getFieldValue(group, "children");
        if (children == null) {
            children = (Object[]) ReflectUtil.getFieldValue(group, "positiveChildren");
        }
        TreePatternNode.BaseNaming naming = (TreePatternNode.BaseNaming) ReflectUtil.getFieldValue(children[0], "naming");
        for (ObjectName name : (ObjectName[]) ReflectUtil.getFieldValue(naming, "names")) {
            String dbName = name.name;
            String jdbcUrl = DatabaseUtil.buildConnectionString(dataSource.getUrl(), driverProperties, dbName);
            DataPivotDatabaseInfo dataPivotDatabaseInfo = new DataPivotDatabaseInfo();
            dataPivotDatabaseInfo.setUniqueId(uniqueId);
            dataPivotDatabaseInfo.setDataSourceVersion(version);
            dataPivotDatabaseInfo.setIcon(icon);
            dataPivotDatabaseInfo.setDataSourceName(dataSourceName);
            dataPivotDatabaseInfo.setDatabaseName(dbName);
            dataPivotDatabaseInfo.setDatabasePath(dataSourceName + DataPivotConstants.SLASH + dbName);
            dataPivotDatabaseInfo.setUserName(username);
            dataPivotDatabaseInfo.setPassword(password);
            dataPivotDatabaseInfo.setDatabaseType(type);
            dataPivotDatabaseInfo.setDatabaseDriver(dataSource.getDatabaseDriver().toString());
            dataPivotDatabaseInfo.setDatabaseParam(driverProperties);
            dataPivotDatabaseInfo.setUrl(jdbcUrl);
            dataPivotDatabaseInfo.setDriverClassName(driverClassName);
            dataPivotDatabaseInfo.setDatabaseReference(DataPivotUtil.createDatabaseReference(uniqueId, dbName));
            dataPivotDatabaseInfoList.add(dataPivotDatabaseInfo);
        }
        return dataPivotDatabaseInfoList;
    }
}
