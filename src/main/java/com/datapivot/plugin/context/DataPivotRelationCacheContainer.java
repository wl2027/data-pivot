package com.datapivot.plugin.context;

import cn.hutool.core.util.ReflectUtil;
import com.datapivot.plugin.config.trigger.DataPivotRelationMapperTrigger;
import com.datapivot.plugin.i18n.DataPivotBundle;
import com.datapivot.plugin.model.DataPivotCacheContainer;
import com.datapivot.plugin.model.DataPivotRelation;
import com.datapivot.plugin.tool.DataPivotUtil;
import com.datapivot.plugin.tool.MessageUtil;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.dataSource.LocalDataSourceManager;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DataSourceManager;
import com.intellij.database.psi.DbColumnImpl;
import com.intellij.database.psi.DbDataSourceImpl;
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
        LocalDataSourceManager localDataSourceManager = new LocalDataSourceManager(project);
        List<? extends LocalDataSource> dataSources = localDataSourceManager.getDataSources();
        Map<String, List<String>> dbInfoMapCache = DataPivotApplication.getInstance().MAPPER.DP_DS_DATABASE_MAPPER;
        List<DataPivotRelation> rs = new ArrayList<>();
        for (LocalDataSource dataSource : dataSources) {
            List<? extends DasObject> list = dataSource.getModel().getModelRoots().toList();
            for (DasObject dasObject : list) {
                String uniqueId = dataSource.getUniqueId();
                List<String> strings = dbInfoMapCache.get(uniqueId);
                if (strings == null||strings.isEmpty()) {
                    continue;
                }
                String dbName = dasObject.getName();
                if (strings.contains(dbName)) {
                    //mysql类型
                    DbColumnImpl createDbElement = null;
                    try {
                        //先SCHEMA 后 TABLE 从下往上取两层->column->table=>database=>datasource
                        dasObject.getDasChildren(ObjectKind.NONE).toList();//直接获取
                        Object myTables = ReflectUtil.getFieldValue(dasObject, "myTables");//库
                        if (myTables == null) {
                            dasObject.getDasChildren(ObjectKind.SCHEMA).toList().get(10).getDasChildren(ObjectKind.TABLE).toList();
                            continue;
                        }
                        Object myElements = ReflectUtil.getFieldValue(myTables, "myElements");
                        if (myElements == null) {
                            continue;
                        }
                        List tableList = (List) myElements;
                        if (tableList == null) {
                            continue;
                        }
                        for (Object table : tableList) {
                            Object myColumns = ReflectUtil.getFieldValue(table, "myColumns");
                            if (myColumns == null) {
                                continue;
                            }
                            Object myElementsColumn = ReflectUtil.getFieldValue(myColumns, "myElements");
                            if (myColumns == null) {
                                continue;
                            }
                            List columnList = (List) myElementsColumn;
                            String tableName = ReflectUtil.invoke(table, "getName");
                            if (tableName == null) {
                                continue;
                            }
                            //mongo 没有字段的概念,所以可能为空
                            if (columnList == null) {
                                continue;
                            }
                            for (Object column : columnList) {
                                String columnName = ReflectUtil.invoke(column, "getName");
                                if (columnName == null) {
                                    continue;
                                }
                                DbDataSourceImpl dbDataSource = ReflectUtil.newInstance(DbDataSourceImpl.class, project, dataSource, (DataSourceManager) localDataSourceManager);
                                if (dbDataSource == null) {
                                    continue;
                                }
                                createDbElement = ReflectUtil.invoke(dbDataSource, "createDbElement", column);
                                if (createDbElement == null) {
                                    continue;
                                }
                                DataPivotRelation dataPivotRelation = new DataPivotRelation();
                                dataPivotRelation.setDatabaseReference(DataPivotUtil.createDatabaseReference(uniqueId, dbName));
                                dataPivotRelation.setDatabaseName(dbName);
                                dataPivotRelation.setTableName(tableName);
                                dataPivotRelation.setColumnName(columnName);
                                dataPivotRelation.setDbColumn(createDbElement);
                                rs.add(dataPivotRelation);
                            }

                        }
                    } catch (Exception ex) {
                        MessageUtil.Notice.error(DataPivotBundle.message("data.pivot.notice.rom.data.error",dbName,String.valueOf(ex.getMessage())));
                    }
                }
            }
        }
        return rs;
    }
}
