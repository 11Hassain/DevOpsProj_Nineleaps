package com.example.devopsproj.exceptions;

public class RepositoryDeletionException extends RuntimeException{
    public RepositoryDeletionException(String message) {
        super(message);
    }
}
