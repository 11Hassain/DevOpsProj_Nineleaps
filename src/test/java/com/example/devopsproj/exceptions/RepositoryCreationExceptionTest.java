package com.example.devopsproj.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RepositoryCreationExceptionTest {

    @Test
    void testExceptionMessage() {
        String expectedMessage = "Repository creation failed";

        RepositoryCreationException exception = new RepositoryCreationException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testExceptionWithNullMessage() {
        RepositoryCreationException exception = new RepositoryCreationException(null);

        assertNull(exception.getMessage());
    }
}
