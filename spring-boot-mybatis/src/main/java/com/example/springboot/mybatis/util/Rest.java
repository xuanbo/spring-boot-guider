package com.example.springboot.mybatis.util;

import java.io.Serializable;

public class Rest<T> implements Serializable {

    public static final int OK = 200;
    public static final int FAIL = 500;

    private int code;
    private String message;
    private T data;

    public static <T> Rest<T> ok(String message, T data) {
        return new Rest<>(OK, message, data);
    }

    public static <T> Rest<T> fail(String message, T data) {
        return new Rest<>(FAIL, message, data);
    }

    private Rest() {
    }

    private Rest(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
