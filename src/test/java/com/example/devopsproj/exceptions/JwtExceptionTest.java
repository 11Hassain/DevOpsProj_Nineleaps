package com.example.devopsproj.exceptions;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtExceptionTest {

    private JwtExceptionHandler jwtException;

    @BeforeEach
    void setUp() {
        jwtException = new JwtExceptionHandler();
    }

    @Test
    void testHandleJWTDecodeException() {
        // Arrange
        JWTDecodeException exception = new JWTDecodeException("Invalid JWT token");

        // Act
        ResponseEntity<String> response = jwtException.handleJWTDecodeException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The jwt token is not correct", response.getBody());
    }
}
