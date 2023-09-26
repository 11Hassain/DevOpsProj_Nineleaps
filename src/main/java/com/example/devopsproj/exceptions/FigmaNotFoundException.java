package com.example.devopsproj.exceptions;

public class FigmaNotFoundException extends RuntimeException {
    public FigmaNotFoundException(String message) {
        super(message);
    }

    public FigmaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
