package com.example.devopsproj.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConflictExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Conflict occurred.";
        ConflictException exception = new ConflictException(message);

        assertEquals(message, exception.getMessage());
    }

}
