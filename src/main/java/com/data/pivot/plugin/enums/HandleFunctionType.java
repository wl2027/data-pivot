package com.data.pivot.plugin.enums;

import cn.hutool.core.util.StrUtil;

/**
 * 按钮,点击后追加到处理文本框 or 模板引擎
 */
public enum HandleFunctionType {
    //下划线转驼峰
    TO_CAMEL_CASE("toCamelCase"),
    //驼峰转下划线
    TO_UNDERLINE_CASE("toUnderlineCase"),
    //转小写
    TO_LOWER_CASE("toLowerCase"),
    //转大写
    TO_UPPER_CASE("toUpperCase"),
    ;
    private String name;

    public String getName() {
        return name;
    }

    HandleFunctionType(String name) {
        this.name = name;
    }
}
