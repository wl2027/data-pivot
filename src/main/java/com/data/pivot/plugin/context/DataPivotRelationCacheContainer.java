package com.data.pivot.plugin.context;

import com.data.pivot.plugin.config.trigger.DataPivotRelationMapperTrigger;
import com.data.pivot.plugin.i18n.DataPivotBundle;
import com.data.pivot.plugin.model.DataPivotCacheContainer;
import com.data.pivot.plugin.model.DataPivotRelation;
import com.data.pivot.plugin.tool.DataPivotUtil;
import com.data.pivot.plugin.tool.MessageUtil;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.dataSource.LocalDataSourceManager;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbColumn;
import com.intellij.database.psi.DbElement;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataPivotRelationCacheContainer extends DataPivotCacheContainer<DataPivotRelation> {
    public DataPivotRelationCacheContainer() {
        super(new DataPivotRelationMapperTrigger());
    }

    @Override
    protected List<DataPivotRelation> init(Project project) {
        LocalDataSourceManager localDataSourceManager = LocalDataSourceManager.getInstance(project);
        List<? extends LocalDataSource> dataSources = localDataSourceManager.getDataSources();
        Map<String, List<String>> dbInfoMapCache = DataPivotApplication.getInstance().MAPPER.DP_DS_DATABASE_MAPPER;
        List<DataPivotRelation> rs = new ArrayList<>();
        for (LocalDataSource dataSource : dataSources) {
            String uniqueId = dataSource.getUniqueId();
            List<String> databaseNames = dbInfoMapCache.get(uniqueId);
            if (databaseNames == null || databaseNames.isEmpty()) {
                continue;
            }

            for (DasObject databaseObject : dataSource.getModel().getModelRoots()) {
                String dbName = databaseObject.getName();
                if (!databaseNames.contains(dbName)) {
                    continue;
                }

                try {
                    collectRelations(project, uniqueId, dbName, databaseObject, rs);
                } catch (Exception ex) {
                    MessageUtil.Notice.error(DataPivotBundle.message("data.pivot.notice.rom.data.error", dbName, String.valueOf(ex.getMessage())));
                }
            }
        }
        return rs;
    }

    private static void collectRelations(Project project, String uniqueId, String dbName, DasObject databaseObject, List<DataPivotRelation> rs) {
        for (DasObject table : collectTables(databaseObject)) {
            String tableName = table.getName();
            if (tableName == null) {
                continue;
            }

            for (DasColumn column : DasUtil.getColumns(table)) {
                String columnName = column.getName();
                if (columnName == null) {
                    continue;
                }

                DbElement dbElement = DbPsiFacade.getInstance(project).findElement(column);
                if (!(dbElement instanceof DbColumn)) {
                    continue;
                }

                DataPivotRelation dataPivotRelation = new DataPivotRelation();
                dataPivotRelation.setDatabaseReference(DataPivotUtil.createDatabaseReference(uniqueId, dbName));
                dataPivotRelation.setDatabaseName(dbName);
                dataPivotRelation.setTableName(tableName);
                dataPivotRelation.setColumnName(columnName);
                dataPivotRelation.setDbColumn((DbColumn) dbElement);
                rs.add(dataPivotRelation);
            }
        }
    }

    private static List<DasObject> collectTables(DasObject databaseObject) {
        List<DasObject> tables = new ArrayList<>();
        databaseObject.getDasChildren(ObjectKind.TABLE).forEach(tables::add);
        databaseObject.getDasChildren(ObjectKind.SCHEMA).forEach(schema -> schema.getDasChildren(ObjectKind.TABLE).forEach(tables::add));
        return tables;
    }
}
