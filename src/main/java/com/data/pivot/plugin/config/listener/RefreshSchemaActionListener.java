package com.data.pivot.plugin.config.listener;

import cn.hutool.core.util.StrUtil;
import com.data.pivot.plugin.config.DataPivotInitializer;
import com.data.pivot.plugin.config.DataPivotLineMarkerProvider;
import com.data.pivot.plugin.tool.ProjectUtils;
import com.intellij.database.actions.ManageDataSourcesAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnActionResult;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import org.jetbrains.annotations.NotNull;

public class RefreshSchemaActionListener implements AnActionListener {
    
    @Override
    public void afterActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event, @NotNull AnActionResult result) {
        if (isRefreshDatabase(action)){
            //刷新范围:dbinfo,rominfo,db连接
            DataPivotInitializer.initDataPivotDatabaseInfo(ProjectUtils.getCurrProject());
            DataPivotInitializer.initDataPivotRelation(ProjectUtils.getCurrProject());
            DataPivotLineMarkerProvider.clearCache();
        }
    }

    private boolean isRefreshDatabase(AnAction action) {
        String name = action.getClass().getName();
        return action.getClass().equals(ManageDataSourcesAction.class)
                ||StrUtil.contains(name,"com.intellij.database.actions.RefreshSchemaAction")
                ||(StrUtil.contains(name,"com.intellij.database.actions")&&(StrUtil.contains(name,"Refresh")||StrUtil.contains(name,"refresh")))
                ;
    }

}
