package org.example.exception;


import org.example.model.AuthApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public AuthApiResponse<?> handleException(Exception e) {
        return null;
    }
}
