package com.datapivot.plugin.actions;

import cn.hutool.core.collection.ListUtil;
import com.datapivot.plugin.context.DataPivotApplication;
import com.datapivot.plugin.i18n.DataPivotBundle;
import com.datapivot.plugin.model.BaseAnAction;
import com.datapivot.plugin.model.DataPivotObject;
import com.datapivot.plugin.model.DataPivotRelation;
import com.datapivot.plugin.tool.DatabaseUtil;
import com.datapivot.plugin.tool.MessageUtil;
import com.datapivot.plugin.tool.PsiElementUtil;
import com.datapivot.plugin.view.sample.DataPivotLookupElement;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataPivotSampleAction extends BaseAnAction {
    @Override
    protected void action(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        // 获取当前编辑器对象
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        // 获取选择的数据模型
        SelectionModel selectionModel = editor.getSelectionModel();
        // 获取当前选择的文本
        String selectedText = selectionModel.getSelectedText();
        List<DataPivotLookupElement> lookupElements = queryDataPivotLookupElements(psiElement,selectedText);
        if (lookupElements == null) {
            return;
        }
        Lookup lookup = LookupManager.getInstance(e.getProject()).showLookup(
                CommonDataKeys.EDITOR.getData(e.getDataContext()),
                lookupElements.toArray(LookupElement[]::new));
    }
    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement!=null&&psiElement instanceof PsiField){
            //启用
            e.getPresentation().setEnabledAndVisible(true);
        }else {
            e.getPresentation().setEnabledAndVisible(false);
        }

    }
    private List<DataPivotLookupElement> queryDataPivotLookupElements(PsiElement psiElement, String selectedText) {
        DataPivotObject dataPivotObject = PsiElementUtil.getDataPivotObject(psiElement);
        if (dataPivotObject.getDataPivotMappingSettingInfo() == null) {
            MessageUtil.Hint.error(editor,
                    DataPivotBundle.message("data.pivot.hint.object.mapping.null",
                            dataPivotObject.getPackageReference()));
            return null;
        }
        DataPivotRelation dataPivotRelation = DataPivotApplication.ormMapping(dataPivotObject, editor);
        String selectSql = DatabaseUtil.createQuerySql(dataPivotObject.getDataPivotMappingSettingInfo().getDataPivotCustomSqlInfo(),dataPivotRelation.getTableName(), dataPivotRelation.getColumnName());
        List<Map<String, Object>> maps = null;
        maps = DatabaseUtil.executeQuery(selectSql, dataPivotRelation.getDatabaseReference());
        if (maps == null) {
            return null;
        }
        List<DataPivotLookupElement> lookupElements = new ArrayList<>();
        if (maps.isEmpty()) {
            return ListUtil.of(new DataPivotLookupElement(
                    selectedText,
                    DataPivotBundle.message("data.pivot.dialog.query.result.null"),
                    selectSql,
                    null));

        }
        for (Map<String, Object> map : maps) {
            String columnValue = String.valueOf(map.get(dataPivotRelation.getColumnName()));
            String rs_count = String.valueOf(map.get("rs_count"));
            String percentage = String.valueOf(map.get("percentage"));
                    lookupElements.add(new DataPivotLookupElement(
                            selectedText,
                            columnValue,
                            selectSql,
                            "count:"+rs_count+" percentage:"+percentage+"%"));
        }
        return lookupElements;
    }

}
