package org.example.model;


import lombok.Builder;

@Builder
public class ErrorDto {
    private String message;
    private String requestId;
    private String timestamp;
}
