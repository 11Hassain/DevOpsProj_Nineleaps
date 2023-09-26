package com.example.devopsproj.exceptions;

public class FigmaServiceException extends RuntimeException {
    public FigmaServiceException(String message) {
        super(message);
    }

    public FigmaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
