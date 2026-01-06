package org.example.exception;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.RequestContext;
import org.example.dto.ErrorDto;
import org.example.model.ResourceApiStatus;
import org.example.model.ResourcesApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;

@RestControllerAdvice
@Slf4j
public class ResourceExceptionHandler{

    @Resource
    private RequestContext requestContext;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResourcesApiResponse<?>>handleBusinessException(Exception e) {
        return buildErrorResponse(ResourceApiStatus.ERROR, e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResourcesApiResponse<?>>handleFileNotFoundException(IllegalArgumentException e) {
        return buildErrorResponse(ResourceApiStatus.POST_FAIL, e);
    }

    @ExceptionHandler(MinioException.class)
    public ResponseEntity<ResourcesApiResponse<?>>handleFileNotFoundException(MinioException e) {
        return buildErrorResponse(ResourceApiStatus.ERROR, e);
    }

    private ResponseEntity<ResourcesApiResponse<?>> buildErrorResponse(ResourceApiStatus status, Exception e) {
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
