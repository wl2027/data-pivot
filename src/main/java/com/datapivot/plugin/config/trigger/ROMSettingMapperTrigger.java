package com.datapivot.plugin.config.trigger;

import com.datapivot.plugin.context.DataPivotApplication;
import com.datapivot.plugin.entity.DataPivotMappingSettingInfo;
import com.datapivot.plugin.model.DataPivotTrigger;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ROMSettingMapperTrigger implements DataPivotTrigger<DataPivotMappingSettingInfo> {
    @Override
    public void load(List<DataPivotMappingSettingInfo> dataPivotMappingSettingInfoList) {
        DataPivotApplication.getInstance().MAPPER.DP_ROM_SETTING_MAPPER.clear();
        //dataSource->database->packageReference
        Map<String, Map<String, DataPivotMappingSettingInfo>> collect = dataPivotMappingSettingInfoList.stream().collect(
                Collectors.groupingBy(
                        DataPivotMappingSettingInfo::getDataSourceName,
                        Collectors.toMap(DataPivotMappingSettingInfo::getDatabaseName, Function.identity(),
                                (o1, o2) -> o1)));
        DataPivotApplication.getInstance().MAPPER.DP_ROM_SETTING_MAPPER.putAll(collect);
    }
}
