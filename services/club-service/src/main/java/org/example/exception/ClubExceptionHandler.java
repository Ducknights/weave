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
        return buildErrorResponse(ClubApiStatus.POST_FAIL,e.getMessage());
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
        return buildErrorResponse(ClubApiStatus.NOT_FOUND,"结果为空");
    }

    private ResponseEntity<ClubApiResponse<?>> buildErrorResponse(ClubApiStatus status, String msg) {
        ErrorDto errorDto = ErrorDto.builder()
                .message(msg)
                .requestId(requestContext.getRequestId())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();
        return ResponseEntity.status(status.getCode())
                .body(ClubApiResponse.fail(status,errorDto));
    }
}
