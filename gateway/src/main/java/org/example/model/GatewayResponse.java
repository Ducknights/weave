package org.example.model;


public record GatewayResponse<T>(
        int code,
        String msg,
        T data) {
}
