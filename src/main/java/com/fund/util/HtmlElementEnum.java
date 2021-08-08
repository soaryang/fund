package com.fund.util;

public enum HtmlElementEnum {

    A("a"),
    SPAN("span"),
    LABEL("label"),
    P("p");
    // 成员变量
    private String name;

    // 构造方法
    private HtmlElementEnum(String name) {
        this.name = name;
    }
}