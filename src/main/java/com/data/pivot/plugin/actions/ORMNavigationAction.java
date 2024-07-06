package com.data.pivot.plugin.actions;

import com.data.pivot.plugin.context.DataPivotApplication;
import com.data.pivot.plugin.i18n.DataPivotBundle;
import com.data.pivot.plugin.model.BaseAnAction;
import com.data.pivot.plugin.model.DataPivotObject;
import com.data.pivot.plugin.model.DataPivotRelation;
import com.data.pivot.plugin.tool.MessageUtil;
import com.data.pivot.plugin.tool.PsiElementUtil;
import com.intellij.database.view.DatabaseView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

public class ORMNavigationAction extends BaseAnAction {

    @Override
    protected void action(AnActionEvent e) {
        DataPivotObject dataPivotObject = PsiElementUtil.getDataPivotObject(e.getData(CommonDataKeys.PSI_ELEMENT));
        if (dataPivotObject.getDataPivotMappingSettingInfo() == null) {
            MessageUtil.Hint.error(editor,
                    DataPivotBundle.message("data.pivot.hint.object.mapping.null",
                            dataPivotObject.getPackageReference()));
            return;
        }
        DataPivotRelation dataPivotRelation = DataPivotApplication.ormMapping(dataPivotObject,editor);
        if (dataPivotRelation == null) {
            return;
        }
        DatabaseView.select(dataPivotRelation.getDbColumn(), true);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement!=null&&psiElement instanceof PsiField){
            //启用
            e.getPresentation().setEnabled(true);
        }else {
            e.getPresentation().setEnabled(false);
        }

    }
}
