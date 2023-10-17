package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.service.interfaces.UserNamesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usernames")
@RequiredArgsConstructor
public class UserNamesController {

    private final UserNamesService userNamesService;

    // Endpoint to save a GitHub username.
    @PostMapping("/githubUsername")
    public ResponseEntity<Object> saveUsername(@RequestBody UserNamesDTO userNamesDTO) {
        UserNamesDTO savedUserNames = userNamesService.saveUsername(userNamesDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUserNames);
    }


    // Endpoint to get GitHub usernames by role.
    @GetMapping("/role/{role}")
    public ResponseEntity<Object> getUserNamesByRole(@PathVariable String role) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase()); // Convert role to EnumRole
        return ResponseEntity.ok(userNamesService.getGitHubUserNamesByRole(enumRole));
    }
}
