package com.wordle.blog.exception;

public class SavedPostNotFoundException extends RuntimeException {
    public SavedPostNotFoundException(String message) {
        super(message);
    }
}