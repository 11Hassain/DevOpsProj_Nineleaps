package com.example.devopsproj.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GitHubUserNotFoundExceptionTest {

    @Test
    public void testGitHubUserNotFoundExceptionWithMessage() {
        // Arrange
        String message = "GitHub user not found";

        // Act
        GitHubUserNotFoundException exception = new GitHubUserNotFoundException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
}
