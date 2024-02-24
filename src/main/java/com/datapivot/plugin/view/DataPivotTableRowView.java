package com.datapivot.plugin.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class DataPivotTableRowView<T> extends DialogWrapper {

    protected DataPivotTableRowView(@Nullable Project project) {
        super(project);
    }

    public abstract T getValue();

}
