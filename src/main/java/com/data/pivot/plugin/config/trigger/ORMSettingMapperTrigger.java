package com.data.pivot.plugin.config.trigger;

import com.data.pivot.plugin.entity.DataPivotMappingSettingInfo;
import com.data.pivot.plugin.context.DataPivotApplication;
import com.data.pivot.plugin.model.DataPivotTrigger;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ORMSettingMapperTrigger implements DataPivotTrigger<DataPivotMappingSettingInfo> {
    @Override
    public void load(List<DataPivotMappingSettingInfo> dataPivotMappingSettingInfoList) {
        DataPivotApplication.getInstance().MAPPER.DP_ORM_SETTING_MAPPER.clear();
        //module->package->databaseReference
        Map<String, Map<String, DataPivotMappingSettingInfo>> collect = dataPivotMappingSettingInfoList.stream().collect(
                Collectors.groupingBy(
                        DataPivotMappingSettingInfo::getModelName,
                        Collectors.toMap(DataPivotMappingSettingInfo::getPackageName, Function.identity(),
                                (o1, o2) -> o1)));
        DataPivotApplication.getInstance().MAPPER.DP_ORM_SETTING_MAPPER.putAll(collect);
    }
}
