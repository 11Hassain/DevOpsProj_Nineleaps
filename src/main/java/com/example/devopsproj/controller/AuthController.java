package com.example.devopsproj.controller;

import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.utils.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private IUserService userService;

    @GetMapping("/api/v1/get-email")
    public ResponseEntity<Object> getEmailFromToken(@RequestHeader("emailToVerify") String emailToVerify) throws IOException {
        Object object = userService.loginVerification(emailToVerify);
        if (object == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else {
            return ResponseEntity.ok(userService.loginVerification(emailToVerify));
        }
    }

    @GetMapping("/api/v1/getEmail")
    public ResponseEntity<Object> getEmailFromToken(
            @RequestHeader("Authorization") String authHeader,
            HttpServletResponse response) throws IOException{
        try {
            String jwt = authHeader.replace("Bearer", "");
            String emailToVerify = JwtUtils.getEmailFromJwt(jwt);
            return new ResponseEntity<>(userService.loginVerification(emailToVerify), HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }

}