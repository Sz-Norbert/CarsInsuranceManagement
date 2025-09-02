package com.example.carins.exception;

public class PolicyValidationException extends RuntimeException {
    public PolicyValidationException(String message) {
        super(message);
    }
}