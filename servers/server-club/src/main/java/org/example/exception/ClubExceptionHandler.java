package org.example.exception;


import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.example.bean.RequestContext;
import org.example.model.ClubApiResponse;
import org.example.model.ClubApiStatus;
import org.example.model.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class ClubExceptionHandler {

    @Resource
    private RequestContext requestContext;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ClubApiResponse<?>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        return buildErrorResponse(ClubApiStatus.POST_FAIL,e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ClubApiResponse<?>> handleHttpMessageNotReadableException() {
        return buildErrorResponse(ClubApiStatus.BAD_REQUEST,"请求体错误");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ClubApiResponse<?>> handleConstraintViolationException(
            ConstraintViolationException e) {
        return buildErrorResponse(ClubApiStatus.BAD_REQUEST,"参数校验异常");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ClubApiResponse<?>> handleMissingServletRequestParameterException() {
        return buildErrorResponse(ClubApiStatus.BAD_REQUEST,"缺少参数");
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ClubApiResponse<?>> handleNoResultException() {
        return buildErrorResponse(ClubApiStatus.NOT_FOUND);
    }


    private ResponseEntity<ClubApiResponse<?>> buildErrorResponse(ClubApiStatus status, Exception e) {
        ErrorDto errorDto = ErrorDto.builder()
                .message(e.getMessage())
                .requestId(requestContext.getRequestId())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();
        log.error("Error: {},Time：{},RequestId：{}", e.getMessage(), errorDto.getTimestamp(), errorDto.getRequestId());
        return ResponseEntity.status(status.getCode())
                .body(ClubApiResponse.error(status,errorDto));
    }

    private ResponseEntity<ClubApiResponse<?>> buildErrorResponse(ClubApiStatus status, String msg) {
        ErrorDto errorDto = ErrorDto.builder()
                .message(msg)
                .requestId(requestContext.getRequestId())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();
        log.error("Error: {},Time：{},RequestId：{}",msg, errorDto.getTimestamp(), errorDto.getRequestId());
        return ResponseEntity.status(status.getCode())
                .body(ClubApiResponse.error(status,errorDto));
    }
    private ResponseEntity<ClubApiResponse<?>> buildErrorResponse(ClubApiStatus status) {
        ErrorDto errorDto = ErrorDto.builder()
                .message("结果为空")
                .requestId(requestContext.getRequestId())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();
        log.error("Error: {},Time：{},RequestId：{}", "请求失败", errorDto.getTimestamp(), errorDto.getRequestId());
        return ResponseEntity.status(200)
                .body(ClubApiResponse.error(status,errorDto));
    }
}
