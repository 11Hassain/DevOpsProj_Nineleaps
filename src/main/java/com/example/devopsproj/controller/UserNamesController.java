package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.service.JwtService;
import com.example.devopsproj.service.UserNamesService;
import com.example.devopsproj.dto.responseDto.UserNamesDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usernames")
@Validated
public class UserNamesController {

    @Autowired
    private final UserNamesService userNamesService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    public UserNamesController(UserNamesService userNamesService) {
        this.userNamesService = userNamesService;
    }

    private static final String INVALID_TOKEN = "Invalid Token";

    @PostMapping("/githubUsername")
    @Operation(
            description = "Save GitHub username",
            responses = {
                    @ApiResponse(responseCode = "201", description = "GitHub username saved successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed"),
                    @ApiResponse(responseCode = "404", description = "Not Found - GitHub user not found"),
                    @ApiResponse(responseCode = "409", description = "Conflict - Username already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveUsername(@Valid @RequestBody UserNamesDTO userNamesDTO,
                                               @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try{
                UserNamesDTO savedUserNames = userNamesService.saveUsername(userNamesDTO);
                if (savedUserNames == null){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Github user not found");
                }
                else {
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedUserNames);
                }
            }catch (DataIntegrityViolationException e){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/role/{role}")
    @Operation(
            description = "Get GitHub usernames by role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "GitHub usernames obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserNamesByRole(@PathVariable String role,
                                                     @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            return ResponseEntity.ok(userNamesService.getGitHubUserNamesByRole(enumRole));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }
}
