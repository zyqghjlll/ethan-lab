package io.github.ethanzhang.common.web;

public record Message<T>(String code, String message, T data) {
    public static <T> Message<T> succeed(T data) {
        return new Message<>(ResponseConstant.SUCCEED.code, ResponseConstant.SUCCEED.message, data);
    }

    public static <T> Message<T> fail(String code, String message) {
        return new Message<>(code, message, null);
    }
}