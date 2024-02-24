package com.datapivot.plugin.enums;

public enum SubjectType {
    INIT("初始化"),
    LAZY("懒加载"),
    REFRESH("DG刷新"),
    APPLY("应用&保存"),
    ;
    private String name;

    public String getName() {
        return name;
    }

    SubjectType(String name) {
        this.name = name;
    }
}
