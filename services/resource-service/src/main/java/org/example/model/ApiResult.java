package org.example.model;

public record ApiResult<T>(
        int code,
        String message,
        T data) {
}