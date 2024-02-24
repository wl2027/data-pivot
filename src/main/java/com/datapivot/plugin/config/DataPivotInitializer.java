package com.datapivot.plugin.config;


import com.datapivot.plugin.entity.custom.*;
import com.datapivot.plugin.tool.DatabaseUtil;
import com.datapivot.plugin.context.DataPivotApplication;
import com.datapivot.plugin.tool.ProjectUtils;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;

public class DataPivotInitializer implements ProjectComponent {

    @Override
    public void projectOpened() {
        Project currProject = ProjectUtils.getCurrProject();
        initDefaultStrategy(currProject);
        initDataPivotDatabaseInfo(currProject);
        initDataPivotSettingInfo(currProject);
    }

    @Override
    public void projectClosed() {
        DatabaseUtil.closeConnections();
    }
    private void initDefaultStrategy(Project project) {
        DataPivotStrategyInfo jpaStrategy = DataPivotDefaultInitializer.getJPAStrategy();
        DataPivotStrategyInfo mpStrategy = DataPivotDefaultInitializer.getMPStrategy();
        DataPivotStrategyInfo huStrategy = DataPivotDefaultInitializer.getHUStrategy();
        DataPivotApplication.getInstance().MAPPER.DEFAULT_STRATEGY_MAPPER.put(jpaStrategy.getCode(), jpaStrategy);
        DataPivotApplication.getInstance().MAPPER.DEFAULT_STRATEGY_MAPPER.put(mpStrategy.getCode(), mpStrategy);
        DataPivotApplication.getInstance().MAPPER.DEFAULT_STRATEGY_MAPPER.put(huStrategy.getCode(), huStrategy);
    }

    static public void initDataPivotDatabaseInfo(Project project) {
        DataPivotApplication.getInstance().CACHE.DP_DB_INFO_LIST_CACHE.loading(project);
    }
    private void initDataPivotSettingInfo(Project project) {
        DataPivotApplication.getInstance().CACHE.DP_MAPPING_SETTING_INFO_LIST_CACHE.loading(project);
    }

    public static boolean isInitDataPivotRelation() {
        return !DataPivotApplication.getInstance().CACHE.DP_RELATION_INFO_LIST_CACHE.isEmpty();
    }

    public static void initDataPivotRelation(Project project) {
        DataPivotApplication.getInstance().CACHE.DP_RELATION_INFO_LIST_CACHE.loading(project);
    }

}
