package com.example.devopsproj.exceptions;
/**
 * CustomGenericException is a custom runtime exception class that can be used
 * to represent generic exceptions in the application. It extends the RuntimeException class.
 *
 * <p>This exception can be thrown to indicate unexpected or generic issues in the application,
 * providing a custom message and, optionally, a cause for further details.
 */
public class CustomGenericException extends RuntimeException {

    public CustomGenericException(String message) {
        super(message);
    }

    public CustomGenericException(String message, Throwable cause) {
        super(message, cause);
    }
}
