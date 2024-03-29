package com.example.DevOpsProj.controller;


import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.repository.UserRepository;
import com.example.DevOpsProj.service.JwtService;
import com.example.DevOpsProj.service.UserService;
import com.example.DevOpsProj.utils.JwtUtils;
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
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/api/get-email")
    public ResponseEntity<Object> getEmailFromToken(@RequestHeader("emailToVerify") String emailToVerify) throws IOException {
        return ResponseEntity.ok(userService.loginVerification(emailToVerify));
    }


    @GetMapping("/api/getEmail")
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

