package com.example.devopsproj.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The `NotFoundException` class represents an exception that is thrown when a requested resource is not found.
 * It is typically used to indicate that a specific resource or entity does not exist.
 *
 * @version 2.0
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{

    /**
     * Constructs a new `NotFoundException` with the specified error message.
     *
     * @param message A descriptive error message indicating the cause of the exception.
     */
    public NotFoundException(String message){
        super(message);
    }
}
