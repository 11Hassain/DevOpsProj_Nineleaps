package com.example.devopsproj.exceptions;
/**
 * NotFoundException is a custom runtime exception class that can be used to indicate
 * a resource or entity not being found. It is annotated with @ResponseStatus(HttpStatus.NOT_FOUND)
 * to specify the HTTP status code for this exception.
 */
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}
