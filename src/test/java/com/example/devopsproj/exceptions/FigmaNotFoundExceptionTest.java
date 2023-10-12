package com.example.devopsproj.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FigmaNotFoundExceptionTest {

    @Test
    public void testFigmaNotFoundExceptionWithMessage() {
        String message = "Figma not found";
        FigmaNotFoundException exception = new FigmaNotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testFigmaNotFoundExceptionWithMessageAndCause() {
        String message = "Figma not found";
        Throwable cause = new RuntimeException("Root cause exception");
        FigmaNotFoundException exception = new FigmaNotFoundException(message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
