package com.data.pivot.plugin.enums;


public enum DefaultStrategyType {
    JPAAnnotation("JPA"),
    MPAnnotation("MybatisPlus"),
    HUMP_UNDERLINE("HumpUnderline"),
    ;
    private final String code;

    public String getCode() {
        return code;
    }

    DefaultStrategyType(String code) {
        this.code = code;
    }
}
