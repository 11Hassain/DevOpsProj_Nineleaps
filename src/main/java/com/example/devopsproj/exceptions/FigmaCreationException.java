package com.example.devopsproj.exceptions;
/**
 * FigmaCreationException is a custom runtime exception class that can be used
 * to indicate an error during the creation process of a Figma-related operation.
 * It extends the RuntimeException class.
 */
public class FigmaCreationException extends RuntimeException {
    public FigmaCreationException(String message) {
        super(message);
    }

    public FigmaCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
