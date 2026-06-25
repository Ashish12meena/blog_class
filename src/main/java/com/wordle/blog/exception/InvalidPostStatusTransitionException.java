package com.wordle.blog.exception;

public class InvalidPostStatusTransitionException extends RuntimeException {
    public InvalidPostStatusTransitionException(String message) {
        super(message);
    }
}