package com.example.devopsproj.exceptions;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class ProjectNotFoundExceptionTest {

    @Test
    void testProjectNotFoundExceptionWithMessage() {
        // Arrange
        String message = "Project not found";

        // Act
        ProjectNotFoundException exception = new ProjectNotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage());
    }
}

