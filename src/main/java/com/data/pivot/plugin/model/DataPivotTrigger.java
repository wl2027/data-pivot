package com.data.pivot.plugin.model;

import java.util.List;

public interface DataPivotTrigger<E extends DataPivotCache> {
    void load(List<E> dataList);
}
