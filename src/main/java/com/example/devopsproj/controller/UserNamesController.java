package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responseDto.UserNamesDTO;
import com.example.devopsproj.service.interfaces.JwtService;
import com.example.devopsproj.service.interfaces.UserNamesService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usernames")
@RequiredArgsConstructor
public class UserNamesController {

    private final UserNamesService userNamesService;
    private final JwtService jwtService;
    private static final String INVALID_TOKEN = "Invalid Token";

    // Endpoint to save a GitHub username.
    @PostMapping("/githubUsername")
    public ResponseEntity<Object> saveUsername(@RequestBody UserNamesDTO userNamesDTO,
                                               @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                UserNamesDTO savedUserNames = userNamesService.saveUsername(userNamesDTO);

                // Check if the GitHub user was not found.
                if (savedUserNames == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("GitHub user not found");
                } else {
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedUserNames);
                }
            } catch (DataIntegrityViolationException e) {
                // Handle the case where the username already exists.
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to get GitHub usernames by role.
    @GetMapping("/role/{role}")
    public ResponseEntity<Object> getUserNamesByRole(@PathVariable String role,
                                                     @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase()); // Convert role to EnumRole
            return ResponseEntity.ok(userNamesService.getGitHubUserNamesByRole(enumRole));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

}