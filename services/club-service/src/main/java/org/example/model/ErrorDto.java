package org.example.model;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorDto {
    private String message;
    private String requestId;
    private String timestamp;
}
