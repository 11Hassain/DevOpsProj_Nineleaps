package com.example.devopsproj.exceptions;

public class FigmaCreationException extends RuntimeException {
    public FigmaCreationException(String message) {
        super(message);
    }

    public FigmaCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
