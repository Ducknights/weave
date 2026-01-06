package org.example.exception;

public class NoTokenException extends RuntimeException{
    public NoTokenException(String message) {
        super(message);
    }
}
