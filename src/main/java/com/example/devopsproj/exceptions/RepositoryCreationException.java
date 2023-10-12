package com.example.devopsproj.exceptions;

/**
 * The `RepositoryCreationException` class represents an exception that is thrown when there is an issue creating a repository.
 * This exception is typically used to handle errors related to the creation of a repository.
 *
 * @version 2.0
 */

public class RepositoryCreationException extends RuntimeException {

    /**
     * Constructs a new `RepositoryCreationException` with the specified error message.
     *
     * @param message A descriptive error message indicating the cause of the exception.
     */
    public RepositoryCreationException(String message) {
        super(message);
    }
}
