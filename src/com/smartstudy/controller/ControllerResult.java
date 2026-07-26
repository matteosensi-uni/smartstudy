package com.smartstudy.controller;

public class ControllerResult<T> {
    private final boolean success;
    private final String message;
    private final T result;

    private ControllerResult(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }

    public static <T> ControllerResult<T> success(T result, String message) {
        return new ControllerResult<>(true, message, result);
    }

    public static <T> ControllerResult<T> success(T result) {
        return new ControllerResult<>(true, "", result);
    }

    public static <T> ControllerResult<T> failure(String message) {
        return new ControllerResult<>(false, message, null);
    }
}
