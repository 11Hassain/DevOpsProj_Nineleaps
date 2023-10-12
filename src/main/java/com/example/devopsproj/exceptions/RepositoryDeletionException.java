package com.example.devopsproj.exceptions;

/**
 * The `RepositoryDeletionException` class represents an exception that is thrown when there is an issue deleting a repository.
 * This exception is typically used to handle errors related to the deletion of a repository.
 *
 * @version 2.0
 */

public class RepositoryDeletionException extends RuntimeException{

    /**
     * Constructs a new `RepositoryDeletionException` with the specified error message.
     *
     * @param message A descriptive error message indicating the cause of the exception.
     */
    public RepositoryDeletionException(String message) {
        super(message);
    }
}
