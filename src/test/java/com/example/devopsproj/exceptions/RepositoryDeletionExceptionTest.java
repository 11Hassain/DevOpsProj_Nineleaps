package com.example.devopsproj.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RepositoryDeletionExceptionTest {

    @Test
    void testExceptionMessage() {
        String expectedMessage = "Repository deletion failed";

        RepositoryDeletionException exception = new RepositoryDeletionException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testExceptionWithNullMessage() {
        RepositoryDeletionException exception = new RepositoryDeletionException(null);

        assertNull(exception.getMessage());
    }
}
