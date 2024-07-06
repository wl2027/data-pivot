package com.data.pivot.plugin.enums;

public enum MapperMethodType {
    ANNOTATION("annotation"),
    SCRIPT("script"),
    ;
    private String name;

    public String getName() {
        return name;
    }

    MapperMethodType(String name) {
        this.name = name;
    }
}
