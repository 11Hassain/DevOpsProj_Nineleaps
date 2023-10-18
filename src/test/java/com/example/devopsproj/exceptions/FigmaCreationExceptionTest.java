package com.example.devopsproj.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FigmaCreationExceptionTest {

    @Test
     void testFigmaCreationExceptionWithMessage() {
        String message = "Figma creation failed";
        FigmaCreationException exception = new FigmaCreationException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
     void testFigmaCreationExceptionWithMessageAndCause() {
        String message = "Figma creation failed";
        Throwable cause = new RuntimeException("Root cause exception");
        FigmaCreationException exception = new FigmaCreationException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
