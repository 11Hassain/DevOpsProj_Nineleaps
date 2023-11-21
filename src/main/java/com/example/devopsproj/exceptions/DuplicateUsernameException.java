package com.example.devopsproj.exceptions;
/**
 * DuplicateUsernameException is a custom runtime exception class that can be used
 * to indicate an attempt to create a user with a username that already exists.
 * It extends the RuntimeException class.
 */
public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String message) {
        super(message);
    }

    public DuplicateUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}
