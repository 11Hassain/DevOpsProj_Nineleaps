package com.example.devopsproj.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The `ConflictException` class is an exception that indicates a conflict in the request.
 * It is typically thrown when a resource or operation conflicts with an existing resource or state.
 *
 * @version 2.0
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException{

    /**
     * Constructs a new `ConflictException` with the specified detail message.
     *
     * @param message A brief description of the conflict or error.
     */
    public ConflictException(String message){
        super(message);
    }
}
