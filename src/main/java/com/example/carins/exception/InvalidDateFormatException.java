package com.example.carins.exception;

public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException(String message) {
        super(message);
    }
    
    public InvalidDateFormatException(String date, String format) {
        super("Invalid date format '" + date + "'. Expected format: " + format);
    }
}