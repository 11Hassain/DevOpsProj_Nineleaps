package com.example.devopsproj.controller;
import com.example.devopsproj.service.interfaces.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/api/v1")
public class AuthController {


    private final UserService userService;

    // Get email from a custom "emailToVerify" header.
    @GetMapping("/get-email")
    @ApiOperation("Get email from token")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<Object> getEmailFromToken(@RequestHeader("emailToVerify") String emailToVerify) {
        Object object = userService.loginVerification(emailToVerify);
        if (object == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(userService.loginVerification(emailToVerify));
        }
    }

}