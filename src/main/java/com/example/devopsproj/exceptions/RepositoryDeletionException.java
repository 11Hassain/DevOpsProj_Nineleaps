package com.example.devopsproj.exceptions;

/**
 * RepositoryDeletionException is a custom runtime exception class that can be used
 * to indicate an error during the deletion of a repository.
 * It extends the RuntimeException class.
 */
public class RepositoryDeletionException extends RuntimeException{
    public RepositoryDeletionException(String message) {
        super(message);
    }
}