package com.example.devopsproj.exceptions;
/**
 * RepositoryCreationException is a custom runtime exception class that can be used
 * to indicate an error during the creation of a repository.
 * It extends the RuntimeException class.
 */
public class RepositoryCreationException extends RuntimeException {
    public RepositoryCreationException(String message) {
        super(message);
    }
    public RepositoryCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
