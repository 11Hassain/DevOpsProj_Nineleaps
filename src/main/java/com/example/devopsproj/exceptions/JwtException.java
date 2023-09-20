package com.example.devopsproj.exceptions;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class JwtException extends ResponseEntityExceptionHandler {
    @ExceptionHandler(JWTDecodeException.class)
    protected ResponseEntity<String> handleJWTDecodeException(JWTDecodeException jwtDecodeException){
        return new ResponseEntity<>("The jwt token is not correct", HttpStatus.BAD_REQUEST);
    }
}