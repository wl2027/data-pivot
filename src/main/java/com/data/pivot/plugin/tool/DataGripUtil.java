package com.data.pivot.plugin.tool;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.data.pivot.plugin.config.DataPivotLineMarkerProvider;
import com.data.pivot.plugin.constants.DataPivotConstants;
import com.data.pivot.plugin.entity.DataPivotDatabaseInfo;
import com.data.pivot.plugin.entity.DatabaseQueryConfig;
import com.data.pivot.plugin.enums.DBType;
import com.data.pivot.plugin.i18n.DataPivotBundle;
import com.intellij.database.cli.DbCliUtil;
import com.intellij.database.dataSource.DataSourceSchemaMapping;
import com.intellij.database.dataSource.DatabaseDriver;
import com.intellij.database.dataSource.DatabaseDriverImpl;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.dataSource.LocalDataSourceManager;
import com.intellij.database.model.ObjectName;
import com.intellij.database.psi.DbColumn;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbElement;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.TreePatternNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static DatabaseQueryConfig loadDatabaseQueryConfig(LocalDataSource localDataSource, DbTable tableInfo, List<String> allCaretsText, DbColumn columnInfo) {
        String uniqueId = localDataSource.getUniqueId();
        String type = localDataSource.getDbms().getName();

        String url = localDataSource.getUrl();
        DatabaseDriver databaseDriver = localDataSource.getDatabaseDriver();
        Map<String, String> driverProperties = ((DatabaseDriverImpl) databaseDriver).getDriverProperties();
        String jdbcUrl = DatabaseUtil.buildConnectionString(url, driverProperties);

        String username = localDataSource.getUsername();
        String password = DbCliUtil.getPassword((LocalDataSource) localDataSource);

        //TODO 根据tableInfo 向上找db和schema
        String dbName = null;
        String schema = null;
        DbElement temp = tableInfo;
        while (temp.getParent()!=null){
            DbElement parent = temp.getParent();
            if (StrUtil.endWith(temp.getParent().toString(),"database")) {
                dbName = parent.getName();
            }
            if (StrUtil.endWith(temp.getParent().toString(),"schema")) {
                schema = parent.getName();
            }
            temp = parent;
        }
        if (StrUtil.isEmpty(dbName)){
            dbName = schema;
        }
        return new DatabaseQueryConfig(uniqueId, DBType.getByName(type),url,username,password,dbName,schema,tableInfo.getName(),allCaretsText,columnInfo.getName());
    }

    public static @Nullable DatabaseQueryConfig getDatabaseQueryConfigByPsiElement(@NotNull PsiElement psiElement,Editor editor) {
        PsiField psiField = (PsiField) psiElement;
        PsiClass containingClass = psiField.getContainingClass();
        DbTable tableInfo = DataPivotLineMarkerProvider.getTableInfo(containingClass);
        if (tableInfo == null) {
            MessageUtil.Hint.error(editor,containingClass.getName()+":无法找到该类所映射的数据库实体");
            return null;
        }
        DbColumn columnInfo = DataPivotLineMarkerProvider.getColumnInfo(tableInfo, psiField);
        if (columnInfo == null) {
            //不是数据库字段
            MessageUtil.Hint.error(editor,psiField.getName()+":无法找到该属性所映射的数据库字段");
            return null;
        }
        DbDataSource dataSource = tableInfo.getDataSource();

        List<LocalDataSource> dataSources = LocalDataSourceManager.getInstance(psiElement.getProject()).getDataSources();
        LocalDataSource localDataSource = null;
        for (LocalDataSource source : dataSources) {
            String uniqueId = dataSource.getUniqueId();
            if (source.getUniqueId().equals(uniqueId)) {
                localDataSource = source;
                break;
            }
        }
        if (localDataSource == null) {
            MessageUtil.Hint.error(editor,psiField.getName()+":无法找到关联度映射的数据源");
            return null;
        }
        DatabaseQueryConfig databaseQueryConfig = DataGripUtil.loadDatabaseQueryConfig(localDataSource, tableInfo, null, columnInfo);
        return databaseQueryConfig;
    }
}
