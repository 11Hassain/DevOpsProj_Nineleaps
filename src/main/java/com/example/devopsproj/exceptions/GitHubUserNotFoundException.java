package com.example.devopsproj.exceptions;
/**
 * GitHubUserNotFoundException is a custom runtime exception class that can be used
 * to indicate that a GitHub user could not be found.
 * It extends the RuntimeException class.
 */
public class GitHubUserNotFoundException extends RuntimeException {
    public GitHubUserNotFoundException(String message) {
        super(message);
    }
}
