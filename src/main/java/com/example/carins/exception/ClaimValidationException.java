package com.example.carins.exception;

public class ClaimValidationException extends RuntimeException {
    public ClaimValidationException(String message) {
        super(message);
    }
}