package com.data.pivot.plugin.config.trigger;

import com.data.pivot.plugin.context.DataPivotApplication;
import com.data.pivot.plugin.model.DataPivotRelation;
import com.data.pivot.plugin.model.DataPivotTrigger;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataPivotRelationMapperTrigger implements DataPivotTrigger<DataPivotRelation> {
    @Override
    public void load(List<DataPivotRelation> dataPivotRelationList) {
        //dr->table->column->psiElement
        DataPivotApplication.getInstance().MAPPER.DP_RELATION_INFO_MAPPER.clear();
        Map<String, Map<String, Map<String, DataPivotRelation>>> collect = dataPivotRelationList.stream().collect(
                Collectors.groupingBy(
                        DataPivotRelation::getDatabaseReference,
                        Collectors.groupingBy(DataPivotRelation::getTableName,
                                Collectors.toMap(DataPivotRelation::getColumnName, Function.identity(), (o1, o2) -> o1))));
        DataPivotApplication.getInstance().MAPPER.DP_RELATION_INFO_MAPPER.putAll(collect);
    }
}
