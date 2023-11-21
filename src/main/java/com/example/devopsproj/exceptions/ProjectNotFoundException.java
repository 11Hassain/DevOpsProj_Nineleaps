package com.example.devopsproj.exceptions;

/**
 * ProjectNotFoundException is a custom runtime exception class that can be used
 * to indicate that a project could not be found.
 * It extends the RuntimeException class.
 */
public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(String message) {
        super(message);
    }
}

