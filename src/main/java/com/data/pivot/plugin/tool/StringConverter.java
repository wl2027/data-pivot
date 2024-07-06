package com.data.pivot.plugin.tool;

public class StringConverter {

    // 转下划线
    public static String toUnderScore(String str) {
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    // 转大驼峰
    public static String toBigCamelCase(String str) {
        String camel = str.replaceAll("_([a-z])", "$1");
        return camel.substring(0, 1).toUpperCase() + camel.substring(1);
    }

    // 转小驼峰
    public static String toCamelCase(String str) {
        String camel = str.replaceAll("_([a-z])", "$1");
        return camel.substring(0, 1).toLowerCase() + camel.substring(1);
    }

    public static void main(String[] args) {
        // 测试转下划线
        String underScored = toUnderScore("helloWorldTest");
        System.out.println("转下划线结果：" + underScored); // 输出 hello_world_test

        // 测试转大驼峰
        String bigCamelCase = toBigCamelCase("hello_world_test");
        System.out.println("转大驼峰结果：" + bigCamelCase); // 输出 HelloWorldTest

        // 测试转小驼峰
        String camelCase = toCamelCase("hello_world_test");
        System.out.println("转小驼峰结果：" + camelCase); // 输出 helloWorldTest
    }
}

