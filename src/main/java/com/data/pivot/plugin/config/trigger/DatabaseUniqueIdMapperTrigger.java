package com.data.pivot.plugin.config.trigger;

import com.data.pivot.plugin.entity.DataPivotDatabaseInfo;
import com.data.pivot.plugin.context.DataPivotApplication;
import com.data.pivot.plugin.model.DataPivotTrigger;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DatabaseUniqueIdMapperTrigger implements DataPivotTrigger<DataPivotDatabaseInfo> {
    @Override
    public void load(List<DataPivotDatabaseInfo> dataPivotDatabaseInfoList) {
        //database->uid
        DataPivotApplication.getInstance().MAPPER.DP_DP_DATABASE_MAPPER.clear();
        Map<String,DataPivotDatabaseInfo> collect = dataPivotDatabaseInfoList.stream().collect(
                Collectors.toMap(
                        DataPivotDatabaseInfo::getDatabasePath,
                        Function.identity(),
                        (o1,o2)->o1));
        DataPivotApplication.getInstance().MAPPER.DP_DP_DATABASE_MAPPER.putAll(collect);
    }
}
