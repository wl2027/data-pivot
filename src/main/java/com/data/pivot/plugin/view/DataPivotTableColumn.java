package com.data.pivot.plugin.view;

import cn.hutool.core.lang.func.Func1;

import java.util.function.BiConsumer;

public class DataPivotTableColumn<T> {
    private String name;
    private Func1<T,?> fieldFun;
    private BiConsumer<T, Object> fieldSetter;

    public DataPivotTableColumn(String name, Func1<T, ?> fieldFun) {
        this(name, fieldFun, null);
    }

    public DataPivotTableColumn(String name, Func1<T, ?> fieldFun, BiConsumer<T, Object> fieldSetter) {
        this.name = name;
        this.fieldFun = fieldFun;
        this.fieldSetter = fieldSetter;
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

    public BiConsumer<T, Object> getFieldSetter() {
        return fieldSetter;
    }

    public void setFieldSetter(BiConsumer<T, Object> fieldSetter) {
        this.fieldSetter = fieldSetter;
    }
}
