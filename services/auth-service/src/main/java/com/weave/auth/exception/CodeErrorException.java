package com.weave.auth.exception;

public class CodeErrorException extends RuntimeException{
    public CodeErrorException(String message){
        super(message);
    }
}
