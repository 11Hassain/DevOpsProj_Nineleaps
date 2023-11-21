package com.example.devopsproj.exceptions;
/**
 * FigmaServiceException is a custom runtime exception class that can be used
 * to indicate an error in the Figma service or operation.
 * It extends the RuntimeException class.
 */
public class FigmaServiceException extends RuntimeException {
    public FigmaServiceException(String message) {
        super(message);
    }

    public FigmaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
