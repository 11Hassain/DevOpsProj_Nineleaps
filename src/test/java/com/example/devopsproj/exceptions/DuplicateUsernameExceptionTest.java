package com.example.devopsproj.exceptions;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

 class DuplicateUsernameExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Test Exception Message";
        DuplicateUsernameException exception = new DuplicateUsernameException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Test Exception Message";
        Throwable cause = new RuntimeException("Test Cause");
        DuplicateUsernameException exception = new DuplicateUsernameException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
