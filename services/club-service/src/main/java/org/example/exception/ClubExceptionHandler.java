package org.example.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.example.model.enums.ClubApiStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class ClubExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(
            IllegalArgumentException e) {
        return ResponseEntity.status(ClubApiStatus.POST_FAIL.getCode())
                .body(ClubApiStatus.POST_FAIL.response(e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException() {
        return ResponseEntity.status(ClubApiStatus.BAD_REQUEST.getCode())
                .body(ClubApiStatus.BAD_REQUEST.response("请求体错误"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(
            ConstraintViolationException e) {
        return ResponseEntity.status(ClubApiStatus.BAD_REQUEST.getCode())
                .body(ClubApiStatus.BAD_REQUEST.response("参数校验异常"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException() {
        return ResponseEntity.status(ClubApiStatus.BAD_REQUEST.getCode())
                .body(ClubApiStatus.BAD_REQUEST.response("缺少参数"));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(e.getStatus().getCode())
                .body(e.getStatus().response());
    }
}
