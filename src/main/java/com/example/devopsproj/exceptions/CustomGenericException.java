package com.example.devopsproj.exceptions;

public class CustomGenericException extends RuntimeException {

    public CustomGenericException(String message) {
        super(message);
    }

    public CustomGenericException(String message, Throwable cause) {
        super(message, cause);
    }
}