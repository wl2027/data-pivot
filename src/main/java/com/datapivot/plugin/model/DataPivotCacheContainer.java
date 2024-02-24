package com.datapivot.plugin.model;

import com.intellij.openapi.project.Project;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class DataPivotCacheContainer<E extends DataPivotCache> {
    private List<E> dataList = new CopyOnWriteArrayList<>();

    private final DataPivotListener<E> dataPivotListener = new DataPivotListener<E>();

    public DataPivotCacheContainer(DataPivotTrigger<E> ... dataPivotTriggers) {
        for (DataPivotTrigger<E> dataPivotTrigger : dataPivotTriggers) {
            this.dataPivotListener.addTrigger(dataPivotTrigger);
        }
    }

    public List<E> get() {
        return dataList;
    }

    public void update(List<E> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        dataPivotListener.refresh(dataList);
    }
    public void loading(Project project) {
        List<E> initDataList = init(project);
        update(initDataList);
    }
    public boolean isEmpty() {
        return this.dataList.isEmpty();
    }

    protected abstract List<E> init(Project project);
}
