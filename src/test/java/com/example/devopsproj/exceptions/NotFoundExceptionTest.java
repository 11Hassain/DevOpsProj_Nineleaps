package com.example.devopsproj.exceptions;

import com.example.devopsproj.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class NotFoundExceptionTest {

    @Test
    void testNotFoundExceptionWithMessage() {
        // Arrange
        String message = "Resource not found";

        // Act
        NotFoundException exception = new NotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage());
    }
}
