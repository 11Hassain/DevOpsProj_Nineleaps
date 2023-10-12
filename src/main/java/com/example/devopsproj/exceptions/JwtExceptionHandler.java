package com.example.devopsproj.exceptions;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * The `JwtExceptionHandler` class is an exception handler for handling JWT (JSON Web Token) decode exceptions.
 * It specifically deals with exceptions related to JWT token decoding errors.
 *
 * @version 2.0
 */

public class JwtExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles the `JWTDecodeException` by returning a `ResponseEntity` with a 400 Bad Request status
     * and a message indicating that the JWT token is not correct.
     *
     * @param jwtDecodeException The JWTDecodeException that was thrown.
     * @return A `ResponseEntity` with a 400 Bad Request status and an error message.
     */
    @ExceptionHandler(JWTDecodeException.class)
    protected ResponseEntity<String> handleJWTDecodeException(JWTDecodeException jwtDecodeException){
        return new ResponseEntity<>("The jwt token is not correct", HttpStatus.BAD_REQUEST);
    }
}