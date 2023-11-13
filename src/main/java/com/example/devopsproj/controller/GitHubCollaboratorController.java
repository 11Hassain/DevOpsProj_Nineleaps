package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * The GitHubCollaboratorController class is responsible for managing RESTful API endpoints related to GitHub collaborators and their actions.
 * It provides endpoints for adding and deleting GitHub collaborators, utilizing the GitHubCollaboratorServiceImpl for collaborator-related operations.
 * This controller also handles user authentication through the JwtServiceImpl.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/api/v1/collaborators")
@Validated
@RequiredArgsConstructor
public class GitHubCollaboratorController {

    private final GitHubCollaboratorService collaboratorService;
    private static final Logger logger = LoggerFactory.getLogger(GitHubCollaboratorController.class);

    /**
     * Add a collaborator.
     *
     * @param collaboratorDTO The CollaboratorDTO containing the collaborator data.
     * @return ResponseEntity indicating the result of the operation.
     */
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
    public ResponseEntity<String> addCollaborator(@Valid @RequestBody CollaboratorDTO collaboratorDTO) {
        logger.info("Received a request to add a collaborator.");

        boolean added = collaboratorService.addCollaborator(collaboratorDTO);

        if (added) {
            logger.info("Invitation to add collaborator sent successfully.");
            return ResponseEntity.ok("Invitation to add collaborator sent successfully.");
        } else {
            logger.error("Failed to add collaborator.");
            return ResponseEntity.badRequest().body("Failed to add collaborator.");
        }
    }

    /**
     * Delete a collaborator.
     *
     * @param collaboratorDTO The CollaboratorDTO containing the collaborator data to be removed.
     * @return ResponseEntity indicating the result of the operation.
     */
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
    public ResponseEntity<String> deleteCollaborator(@Valid @RequestBody CollaboratorDTO collaboratorDTO) {
        logger.info("Received a request to delete a collaborator.");

        boolean deleted = collaboratorService.deleteCollaborator(collaboratorDTO);

        if (deleted) {
            logger.info("Collaborator removed successfully.");
            return ResponseEntity.ok("Collaborator removed successfully.");
        } else {
            logger.error("Failed to remove collaborator.");
            return ResponseEntity.badRequest().body("Failed to remove collaborator.");
        }
    }
}
