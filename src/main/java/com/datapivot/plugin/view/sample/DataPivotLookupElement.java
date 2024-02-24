package com.datapivot.plugin.view.sample;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.ide.CopyPasteManager;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

public class DataPivotLookupElement extends LookupElement {
    private final String myData;
    private final String text;
    private final String sql;
    //private final String tablePath;//ds/db/table/column
    private final String info;

    public DataPivotLookupElement(String text,String data,String sql,String info) {
        this.myData = data;
        this.text = text;
        this.sql = sql;
        this.info = info;
    }

    @Override
    public @NotNull String getLookupString() {
        CopyPasteManager.getInstance().setContents(new StringSelection(sql));
//        if (DataPivotBundle.message("data.pivot.dialog.query.result.null").equals(myData)) {
//            //CopyPasteManager.getInstance().setContents(new StringSelection(sql));//复制查询sql
//        }else {
//            CopyPasteManager.getInstance().setContents(new StringSelection(myData));//复制数据
//        }
        return text;//点击后替换内容
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(myData);
        presentation.setTypeText(info);
    }
}
