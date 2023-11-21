package com.example.devopsproj.exceptions;
/**
 * UnauthorizedException is a custom runtime exception class that can be used
 * to indicate an unauthorized access or operation.
 * It extends the RuntimeException class.
 */
public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message) {
        super(message);
    }
}
