package org.example.model;

public record AuthApiResponse<T>(int code, String msg, T data) {
}