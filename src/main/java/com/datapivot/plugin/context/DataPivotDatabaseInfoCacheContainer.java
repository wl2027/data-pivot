package com.datapivot.plugin.context;

import com.datapivot.plugin.config.trigger.DatabaseConnectionMapperTrigger;
import com.datapivot.plugin.config.trigger.DatabaseInfoMapperTrigger;
import com.datapivot.plugin.config.trigger.DatabaseReferenceMapperTrigger;
import com.datapivot.plugin.config.trigger.DatabaseUniqueIdMapperTrigger;
import com.datapivot.plugin.constants.DataPivotConstants;
import com.datapivot.plugin.entity.DataPivotDatabaseInfo;
import com.datapivot.plugin.model.DataPivotCacheContainer;
import com.datapivot.plugin.tool.DataGripUtil;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.dataSource.LocalDataSourceManager;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class DataPivotDatabaseInfoCacheContainer extends DataPivotCacheContainer<DataPivotDatabaseInfo> {
    public DataPivotDatabaseInfoCacheContainer() {
        super(new DatabaseInfoMapperTrigger(),new DatabaseReferenceMapperTrigger(),new DatabaseConnectionMapperTrigger(),new DatabaseUniqueIdMapperTrigger());
    }

    @Override
    protected List<DataPivotDatabaseInfo> init(Project project) {
        LocalDataSourceManager localDataSourceManager = new LocalDataSourceManager(project);
        List<? extends LocalDataSource> dataSources = localDataSourceManager.getDataSources();
        List<DataPivotDatabaseInfo> dataPivotDatabaseInfos = new ArrayList<>();
        for (LocalDataSource dataSource : dataSources) {
            //当前只处理mysql类型
            if (DataPivotConstants.MYSQL.equals(dataSource.getDbms().getName())) {
                dataPivotDatabaseInfos.addAll(DataGripUtil.loadDatabaseInfo(dataSource));
            }
        }
        return dataPivotDatabaseInfos;
    }
}
