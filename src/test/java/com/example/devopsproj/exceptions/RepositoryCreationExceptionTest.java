package com.example.devopsproj.exceptions;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

 class RepositoryCreationExceptionTest {

    @Test
     void testConstructorWithMessage() {
        String message = "Test exception message";
        RepositoryCreationException exception = new RepositoryCreationException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
     void testConstructorWithMessageAndCause() {
        String message = "Test exception message";
        Throwable cause = new RuntimeException("Test cause exception");
        RepositoryCreationException exception = new RepositoryCreationException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
