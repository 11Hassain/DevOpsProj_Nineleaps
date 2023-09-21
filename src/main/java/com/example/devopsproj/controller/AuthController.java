package com.example.devopsproj.controller;

import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.utils.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/api/v1")
public class AuthController {

    private final IUserService userService;

    // Get email from a custom "emailToVerify" header.
    @GetMapping("/get-email")
    public ResponseEntity<Object> getEmailFromToken(@RequestHeader("emailToVerify") String emailToVerify) throws IOException {
        // Call the loginVerification method to verify the email.
        Object object = userService.loginVerification(emailToVerify);
        if (object == null){
            // Return a 404 Not Found response if the email is not found.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            // Return the email verification result in the response.
            return ResponseEntity.ok(object);
        }
    }

    // Get email from a JWT token in the "Authorization" header.
    @GetMapping("/getEmail")
    public ResponseEntity<Object> getEmailFromToken(
            @RequestHeader("Authorization") String authHeader,
            HttpServletResponse response) throws IOException {
        try {
            // Extract the JWT token from the "Authorization" header.
            String jwt = authHeader.replace("Bearer", "");
            // Extract the email to verify from the JWT.
            String emailToVerify = JwtUtils.getEmailFromJwt(jwt);
            // Call the loginVerification method to verify the email.
            return new ResponseEntity<>(userService.loginVerification(emailToVerify), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Return a 404 Not Found response if the JWT is invalid.
            return ResponseEntity.notFound().build();
        }
    }
}
