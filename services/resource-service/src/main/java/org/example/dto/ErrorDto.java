package org.example.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {
    private String message;
    private Long requestId;
    private String timestamp;
}
