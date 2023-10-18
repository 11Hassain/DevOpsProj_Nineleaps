package com.example.devopsproj.exceptions;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class UnauthorizedExceptionTest {

    @Test
    void testUnauthorizedExceptionWithMessage() {
        // Arrange
        String message = "Unauthorized access";

        // Act
        UnauthorizedException exception = new UnauthorizedException(message);

        // Assert
        assertEquals(message, exception.getMessage());
    }
}
