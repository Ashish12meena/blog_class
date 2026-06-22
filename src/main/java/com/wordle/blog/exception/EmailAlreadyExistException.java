package com.wordle.blog.exception;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String message){
        super(message);
    }
}