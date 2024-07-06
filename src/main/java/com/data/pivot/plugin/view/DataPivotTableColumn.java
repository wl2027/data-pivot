package com.data.pivot.plugin.view;

import cn.hutool.core.lang.func.Func1;

public class DataPivotTableColumn<T> {
    private String name;
    private Func1<T,?> fieldFun;

    public DataPivotTableColumn(String name, Func1<T, ?> fieldFun) {
        this.name = name;
        this.fieldFun = fieldFun;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Func1<T, ?> getFieldFun() {
        return fieldFun;
    }

    public void setFieldFun(Func1<T, ?> fieldFun) {
        this.fieldFun = fieldFun;
    }
}
