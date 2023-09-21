package com.example.devopsproj.controller;

import com.example.devopsproj.service.GitHubCollaboratorService;
import com.example.devopsproj.service.JwtService;
import com.example.devopsproj.dto.responseDto.CollaboratorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/collaborators")
public class GitHubCollaboratorController {
    @Autowired
    private final GitHubCollaboratorService collaboratorService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    public GitHubCollaboratorController(GitHubCollaboratorService collaboratorService) {
        this.collaboratorService = collaboratorService;
    }

    private static final String INVALID_TOKEN = "Invalid Token";

    @PostMapping("/add")
    @Operation(
            description = "Add Collaborator",
            responses = {
                   @ApiResponse(responseCode = "200", description = "Invitation to add collaborator sent successfully"),
                    @ApiResponse(responseCode = "400", description = "Failed to add collaborator"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> addCollaborator(@RequestBody CollaboratorDTO collaboratorDTO,
                                                  @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            boolean added = collaboratorService.addCollaborator(collaboratorDTO);
            if (added) {
                return ResponseEntity.ok("Invitation to add collaborator sent successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to add collaborator.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }

    }

    @DeleteMapping("/delete")
    @Operation(
            description = "Delete Collaborator",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Collaborator removed successfully"),
                    @ApiResponse(responseCode = "400", description = "Failed to remove collaborator"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteCollaborator(@RequestBody CollaboratorDTO collaboratorDTO,
                                                     @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            boolean deleted = collaboratorService.deleteCollaborator(collaboratorDTO);
            if (deleted) {
                return ResponseEntity.ok("Collaborator removed successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to remove collaborator.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }
}