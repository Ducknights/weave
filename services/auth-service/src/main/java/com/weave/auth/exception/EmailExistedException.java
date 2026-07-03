package com.weave.auth.exception;

public class EmailExistedException extends RuntimeException{
    public EmailExistedException(String message) {
        super(message);
    }
}
