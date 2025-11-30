package org.example.exception;


import org.example.model.ClubApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ClubExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ClubApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ;
    }


    private ResponseEntity<ClubApiResponse<?>> buildErrorResponse(ResourceApiStatus status, Exception e) {
        ErrorDto errorDto = ErrorDto.builder()
                .message(e.getMessage())
                .requestId(requestContext.getRequestId())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();
        log.error("Error: {},Time：{},RequestId：{}", e.getMessage(), errorDto.getTimestamp(), errorDto.getRequestId());
        return ResponseEntity.status(status.getCode())
                .body(ResourcesApiResponse.error(status,errorDto));
    }
}
