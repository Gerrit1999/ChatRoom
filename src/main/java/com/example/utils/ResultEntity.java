package com.example.utils;

public class ResultEntity<T> {
    public enum ResultType {
        SUCCESS,
        FAILED;
    }

    private ResultType result;//当前请求处理结果

    private String message;//错误消息

    private T data;//返回的数据

    public static <Type> ResultEntity<Type> createResultEntity(ResultType result, String message, Type data) {
        return new ResultEntity<>(result, message, data);
    }

    private ResultEntity(ResultType result, String message, T data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public ResultType getResult() {
        return result;
    }

    public void setResult(ResultType result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
