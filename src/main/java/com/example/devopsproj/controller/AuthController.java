package com.example.devopsproj.controller;

import com.example.devopsproj.service.implementations.UserServiceImpl;
import com.example.devopsproj.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final UserServiceImpl userServiceImpl;

    @GetMapping("/api/v1/get-email")
    @Operation(
            description = "Get Email From Token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Email not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getEmailFromToken(@RequestHeader("emailToVerify") String emailToVerify) {
        Object object = userServiceImpl.loginVerification(emailToVerify);
        if (object == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else {
            return ResponseEntity.ok(userServiceImpl.loginVerification(emailToVerify));
        }
    }

    @GetMapping("/api/v1/getEmail")
    @Operation(
            description = "Get Email From Token (Authorization Header)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Email not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getEmailFromToken(
            @RequestHeader("Authorization") String authHeader,
            HttpServletResponse response){
        try {
            String jwt = authHeader.replace("Bearer", "");
            String emailToVerify = JwtUtils.getEmailFromJwt(jwt);
            return new ResponseEntity<>(userServiceImpl.loginVerification(emailToVerify), HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }

}

