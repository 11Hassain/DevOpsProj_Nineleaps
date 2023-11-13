package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.service.interfaces.UserNamesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * The UserNamesController class provides endpoints for managing GitHub usernames.
 * These operations include saving a GitHub username and retrieving GitHub usernames by role.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/api/v1/usernames")
@Validated
@RequiredArgsConstructor
public class UserNamesController {

    private final UserNamesService userNamesService;
    private static final Logger logger = LoggerFactory.getLogger(UserNamesController.class);

    /**
     * Save GitHub username.
     *
     * @param userNamesDTO The DTO containing the GitHub username.
     * @return ResponseEntity containing the saved GitHub username or an error message.
     */
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
    public ResponseEntity<Object> saveUsername(@Valid @RequestBody UserNamesDTO userNamesDTO) {
        logger.info("Received a request to save GitHub username");
        try{
            UserNamesDTO savedUserNames = userNamesService.saveUsername(userNamesDTO);
            if (savedUserNames == null){
                logger.warn("GitHub user not found for the provided username");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Github user not found");
            }
            else {
                logger.info("GitHub username saved successfully: {}", savedUserNames.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body(savedUserNames);
            }
        }catch (DataIntegrityViolationException e){
            logger.error("Conflict - Username already exists: {}", userNamesDTO.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
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
    public ResponseEntity<Object> getUserNamesByRole(@PathVariable String role) {

            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            return ResponseEntity.ok(userNamesService.getGitHubUserNamesByRole(enumRole));

    }
}
