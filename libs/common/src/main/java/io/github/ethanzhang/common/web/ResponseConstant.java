package io.github.ethanzhang.common.web;

public enum ResponseConstant {
    SUCCEED("200", "Success"),
    ;
    public final String code;
    public final String message;

    ResponseConstant(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
