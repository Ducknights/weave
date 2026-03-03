package org.example.model;

public record ClubApiResponse<T>(
        int code,
        String msg,
        T data) {
}
