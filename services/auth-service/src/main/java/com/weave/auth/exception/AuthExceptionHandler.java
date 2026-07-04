package com.weave.auth.exception;


import com.weave.model.model.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handleRuntimeException(HandlerMethodValidationException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body("具体错误：" + e.getMessage());
    }

    @ExceptionHandler(CodeErrorException.class)
    public ResponseEntity<?> handleCodeError(CodeErrorException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(EmailExistedException.class)
    public ResponseEntity<?> handleEmailExisted(EmailExistedException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<?>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("参数校验失败");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResult<>(400, msg, null));
    }
}
