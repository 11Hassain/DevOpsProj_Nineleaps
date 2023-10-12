package com.example.devopsproj.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FigmaServiceExceptionTest {

    @Test
    public void testFigmaServiceExceptionWithMessage() {
        // Arrange
        String message = "Figma service error";

        // Act
        FigmaServiceException exception = new FigmaServiceException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testFigmaServiceExceptionWithMessageAndCause() {
        // Arrange
        String message = "Figma service error";
        Throwable cause = new RuntimeException("Root cause exception");

        // Act
        FigmaServiceException exception = new FigmaServiceException(message, cause);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
