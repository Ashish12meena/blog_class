package com.wordle.blog.exception;

public class UsernameAlreadyExist extends RuntimeException {
    public UsernameAlreadyExist(String message){
        super(message);
    }
}