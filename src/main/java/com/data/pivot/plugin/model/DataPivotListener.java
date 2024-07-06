package com.data.pivot.plugin.model;

import java.util.ArrayList;
import java.util.List;

public class DataPivotListener<E extends DataPivotCache> {
    private List<DataPivotTrigger<E>> dataPivotTriggerList = new ArrayList<>();

    public void refresh(List<E> dataList){
        process(dataList);
    }
    public DataPivotListener addTrigger(DataPivotTrigger dataPivotTrigger) {
        if (dataPivotTrigger != null) {
            dataPivotTriggerList.add(dataPivotTrigger);
        }
        return this;
    }
    private void process(List<E> dataList) {
        for (DataPivotTrigger<E> dataPivotTrigger : dataPivotTriggerList) {
            try{
                dataPivotTrigger.load(dataList);
            }catch (Exception exception){
                //MessageUtil.Notice.error(String.valueOf(exception.getMessage()));
            }
        }
    }
}
