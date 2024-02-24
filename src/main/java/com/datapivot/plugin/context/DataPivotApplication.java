package com.datapivot.plugin.context;

import com.datapivot.plugin.entity.DataPivotMappingSettingInfo;
import com.datapivot.plugin.entity.custom.DataPivotCustomSqlInfo;
import com.datapivot.plugin.entity.custom.DataPivotStrategyInfo;
import com.datapivot.plugin.model.*;
import com.datapivot.plugin.entity.DataPivotDatabaseInfo;
import com.datapivot.plugin.tool.ProjectUtils;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataPivotApplication {
    private final Project project;
    public final Cache CACHE;
    public final Mapper MAPPER;

    public DataPivotApplication(Project project) {
        this.project = project;
        this.CACHE = new Cache();
        this.MAPPER = new Mapper();
    }
    public static DataPivotApplication getInstance(){
        return ServiceManager.getService(ProjectUtils.getCurrProject(), DataPivotApplication.class);
    }
    public static DataPivotApplication getInstance(Project project){
        return ServiceManager.getService(project, DataPivotApplication.class);
    }

    public Project getProject() {
        return project;
    }
    public class Cache{
        public final DataPivotCacheContainer<DataPivotDatabaseInfo> DP_DB_INFO_LIST_CACHE = new DataPivotDatabaseInfoCacheContainer();
        public final DataPivotCacheContainer<DataPivotMappingSettingInfo> DP_MAPPING_SETTING_INFO_LIST_CACHE = new DataPivotMappingSettingInfoCacheContainer();
        public final DataPivotCacheContainer<DataPivotRelation> DP_RELATION_INFO_LIST_CACHE = new DataPivotRelationCacheContainer();

    }
    public class Mapper{
        public final Map<String, DataPivotStrategyInfo> DEFAULT_STRATEGY_MAPPER = new ConcurrentHashMap<>();
        public final Map<String, DataPivotCustomSqlInfo> DEFAULT_SQL_MAPPER = new ConcurrentHashMap<>();
        public final Map<String, Connection> DR_DATABASE_CONNECTION_MAPPER = new ConcurrentHashMap<>();
        public final Map<String,List<String>> DP_DS_DATABASE_MAPPER = new ConcurrentHashMap<>();
        public final Map<String, DataPivotDatabaseInfo> DP_DP_DATABASE_MAPPER = new ConcurrentHashMap<>();
        public final Map<String, DataPivotDatabaseInfo> DP_DR_DATABASE_MAPPER = new ConcurrentHashMap<>();
        public final Map<String,Map<String, DataPivotMappingSettingInfo>> DP_ORM_SETTING_MAPPER = new ConcurrentHashMap<>();
        public final Map<String,Map<String, DataPivotMappingSettingInfo>> DP_ROM_SETTING_MAPPER = new ConcurrentHashMap<>();
        public final Map<String, Map<String, Map<String, DataPivotRelation>>> DP_RELATION_INFO_MAPPER = new ConcurrentHashMap<>();
    }

    public static DataPivotObject romMapping(DataPivotRelation dataPivotRelation,Editor editor){
        return DataPivotStrategyActuator.mapping(dataPivotRelation,editor);
    }
    public static DataPivotRelation ormMapping(DataPivotObject dataPivotObject, Editor editor){
        return DataPivotStrategyActuator.mapping(dataPivotObject,editor);
    }

    public static DataPivotStrategyInfo getDataPivotStrategyInfo(String strategyCode) {
        return DataPivotApplication.getInstance().MAPPER.DEFAULT_STRATEGY_MAPPER.get(strategyCode);
    }


}
