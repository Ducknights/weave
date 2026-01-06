package org.example.exception;

public class TokenVerifyException extends RuntimeException{
    public TokenVerifyException(String message) {
        super(message);
    }
}
