package com.example.devopsproj.exceptions;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtExceptionHandlerTest {

    @Test
    void testHandleJWTDecodeException() {
        JwtExceptionHandler handler = new JwtExceptionHandler();

        JWTDecodeException jwtDecodeException = new JWTDecodeException("Invalid JWT token");

        ResponseEntity<String> response = handler.handleJWTDecodeException(jwtDecodeException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertEquals("The jwt token is not correct", response.getBody());
    }
}
