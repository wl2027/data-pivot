package com.data.pivot.plugin.actions;

import com.data.pivot.plugin.i18n.DataPivotBundle;
import com.data.pivot.plugin.tool.MessageUtil;
import com.data.pivot.plugin.view.query.QueryTableComponent;
import com.data.pivot.plugin.config.DataPivotLineMarkerProvider;
import com.data.pivot.plugin.entity.DatabaseQueryConfig;
import com.data.pivot.plugin.tool.DataGripUtil;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.dataSource.LocalDataSourceManager;
import com.intellij.database.psi.DbColumn;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 选中的最后一个作为查询条件
 * ApplicationManager.getApplication().invokeLater
 */

public class DataPivotQueryAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement != null && psiElement instanceof PsiField) {
            //启用
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setEnabled(false);
        }

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (project == null || editor == null || psiElement == null || !(psiElement instanceof PsiField)) {
            return;
        }

        PsiField psiField = (PsiField) psiElement;
        PsiClass containingClass = psiField.getContainingClass();
        DbTable tableInfo = DataPivotLineMarkerProvider.getTableInfo(containingClass);
        if (tableInfo == null) {
            MessageUtil.Hint.error(editor,containingClass.getName()+":无法找到该类所映射的数据库实体");
            return;
        }
        DbColumn columnInfo = DataPivotLineMarkerProvider.getColumnInfo(tableInfo, psiField);
        if (columnInfo == null) {
            //不是数据库字段
            MessageUtil.Hint.error(editor,psiField.getName()+":无法找到该属性所映射的数据库字段");
            return;
        }
        // 获取选择的数据模型
        List<Caret> allCarets = editor.getCaretModel().getAllCarets();
        List<String> allCaretsText = new ArrayList<>();
        if (allCarets.size() < 2) {
            //单选查全部
            allCaretsText.add("*");
        } else {
            //查一些 psiFieldList
            for (Caret allCaret : allCarets) {
                //光标所在null,光标选中!=null
                //选中的字段不是数据库字段时要排除掉
                DbColumn dbColumn = DataPivotLineMarkerProvider.getColumnInfo(tableInfo, allCaret.getSelectedText());
                if (dbColumn != null) {
                    allCaretsText.add(dbColumn.getName());
                }
            }
        }
        DbDataSource dataSource = tableInfo.getDataSource();

        List<LocalDataSource> dataSources = LocalDataSourceManager.getInstance(project).getDataSources();
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
            return;
        }
        DatabaseQueryConfig databaseQueryConfig = DataGripUtil.loadDatabaseQueryConfig(localDataSource, tableInfo, allCaretsText, columnInfo);
        QueryTableComponent queryTableComponent = QueryTableComponent.getInstance(project, databaseQueryConfig);
        if (queryTableComponent != null) {
            queryTableComponent.setVisible(true);
        }
    }

    public static void getDatabaseQueryConfigByPsiElement(PsiElement psiElement) {
    }

}
