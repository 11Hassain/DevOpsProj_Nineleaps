package com.example.devopsproj.exceptions;

public class RepositoryCreationException extends RuntimeException {
    public RepositoryCreationException(String message) {
        super(message);
    }
    public RepositoryCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
