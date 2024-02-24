package com.datapivot.plugin.actions;

import cn.hutool.core.util.ReflectUtil;
import com.datapivot.plugin.context.DataPivotApplication;
import com.datapivot.plugin.i18n.DataPivotBundle;
import com.datapivot.plugin.model.BaseAnAction;
import com.datapivot.plugin.model.DataPivotObject;
import com.datapivot.plugin.model.DataPivotRelation;
import com.datapivot.plugin.tool.MessageUtil;
import com.datapivot.plugin.tool.PsiElementUtil;
import com.intellij.database.DatabaseDataKeys;
import com.intellij.database.datagrid.DataGrid;
import com.intellij.database.datagrid.DataGridUtil;
import com.intellij.database.psi.DbColumn;
import com.intellij.database.view.SelectInDatabaseView;
import com.intellij.ide.SelectInContext;
import com.intellij.ide.actions.SelectInContextImpl;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.PsiNavigateUtil;
import org.jetbrains.annotations.NotNull;

public class ROMNavigationAction extends BaseAnAction {
    @Override
    protected void action(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if(isDataGrid(e)){
            Project project = e.getProject();
            SelectInContext context = project == null ? null : SelectInContextImpl.createContext(e);
            if (context != null) {
                PsiElement element = ReflectUtil.invokeStatic(ReflectUtil.getMethodByName(SelectInDatabaseView.class,"askProvidersInner"),context,isStrict(e.getDataContext()));
                psiElement = element;
            }
        }
        DataPivotRelation dataPivotRelation = PsiElementUtil.getDataPivotRelation(psiElement);
        if (dataPivotRelation.getDataPivotMappingSettingInfo() == null) {
            //dataPivotRelation.getDatabaseReference()//需要在每次loadsetting做名称映射uuid
            MessageUtil.Dialog.info(
                    DataPivotBundle.message("data.pivot.hint.relation.mapping.null",
                            dataPivotRelation.getDatabaseReference()));
            return;
        }
        DataPivotObject dataPivotObject = DataPivotApplication.romMapping(dataPivotRelation,editor);
        if (dataPivotObject == null) {
            return;
        }
        PsiNavigateUtil.navigate(dataPivotObject.getPsiElement());
    }

    private boolean isDataGrid(AnActionEvent e) {
        DataGrid dataGrid = DataGridUtil.getDataGrid(e.getDataContext());
        return dataGrid!=null&&dataGrid.getVisibleRows().size()*dataGrid.getVisibleColumns().size()>0;
    }

    private static boolean isStrict(DataContext dataContext) {
        Editor editor = (Editor)CommonDataKeys.EDITOR.getData(dataContext);
        PsiFile file = (PsiFile)CommonDataKeys.PSI_FILE.getData(dataContext);
        return editor != null && file != null && file.findReferenceAt(editor.getCaretModel().getOffset()) != null;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if ((psiElement!=null&&psiElement instanceof DbColumn)||isEnabled(e)){
            //启用
            e.getPresentation().setEnabledAndVisible(true);
        }else {
            e.getPresentation().setEnabledAndVisible(false);
        }

    }
    private boolean isEnabled(@NotNull AnActionEvent e) {
        DataGrid dataGrid = (DataGrid)e.getData(DatabaseDataKeys.DATA_GRID_KEY);
        if (dataGrid == null) {
            return false;
        }
        return isDataGrid(e);
    }
}
