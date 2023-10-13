package com.example.devopsproj.controller;
import com.example.devopsproj.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/api/v1")
public class AuthController {


    private final UserService userService;

    // Get email from a custom "emailToVerify" header.
    @GetMapping("/get-email")
    public ResponseEntity<Object> getEmailFromToken(@RequestHeader("emailToVerify") String emailToVerify) throws IOException {
        // Call the loginVerification method to verify the email.
        Object object = userService.loginVerification(emailToVerify);
        if (object == null) {
            // Return a 404 Not Found response if the email is not found.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            // Return the email verification result in the response.
            return ResponseEntity.ok(object);
        }
    }
}
