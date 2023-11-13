package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.service.interfaces.GoogleDriveService;
import com.example.devopsproj.utils.DTOModelMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The GoogleDriveController class provides RESTful API endpoints for managing Google Drive resources and operations.
 * These endpoints include creating Google Drives, retrieving Google Drives by ID or project, and deleting Google Drives.
 * User authentication is required using the JwtServiceImpl.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class GoogleDriveController {

    private final GoogleDriveService googleDriveService;
    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveController.class);

    /**
     * Create a Google Drive entry.
     *
     * @param googleDriveDTO The GoogleDriveDTO containing the Google Drive data.
     * @return ResponseEntity with the created Google Drive entry or an error response.
     */
    @PostMapping("/createGoogleDrive")
    @Operation(
            description = "Create Google Drive",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Google Drive created successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createGoogleDrive(@Valid @RequestBody GoogleDriveDTO googleDriveDTO) {
        logger.info("Received a request to create a Google Drive entry.");

        GoogleDrive googleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);

        if (googleDrive != null) {
            logger.info("Google Drive entry created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(new GoogleDriveDTO(
                    DTOModelMapper.mapProjectToProjectDTO(googleDrive.getProject()),
                    googleDrive.getDriveLink(),
                    googleDrive.getDriveId()
            ));
        } else {
            logger.error("Failed to create Google Drive entry.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }

    /**
     * Get all Google Drive entries.
     *
     * @return ResponseEntity with the retrieved Google Drive entries or an error response.
     */
    @GetMapping("/getAllGoogleDrives")
    @Operation(
            description = "Get All Google Drives",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Google Drives retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllGoogleDrives() {
        logger.info("Received a request to retrieve all Google Drive entries.");

        List<GoogleDrive> googleDrives = googleDriveService.getAllGoogleDrives();
        List<GoogleDriveDTO> googleDriveDTOs = new ArrayList<>();

        for (GoogleDrive googleDrive : googleDrives) {
            googleDriveDTOs.add(new GoogleDriveDTO(
                    new ProjectDTO(googleDrive.getProject().getProjectId(), googleDrive.getProject().getProjectName()),
                    googleDrive.getDriveLink(),
                    googleDrive.getDriveId()
            ));
        }

        logger.info("Google Drives retrieved successfully.");
        return ResponseEntity.ok(googleDriveDTOs);
    }

    /**
     * Get a Google Drive entry by its ID.
     *
     * @param driveId The ID of the Google Drive entry to retrieve.
     * @return ResponseEntity with the retrieved Google Drive entry or an error response if not found.
     */
    @GetMapping("/getGoogleDriveById/{driveId}")
    @Operation(
            description = "Get Google Drive by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Google Drive retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Google Drive not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveById(@PathVariable Long driveId) {
        logger.info("Received a request to retrieve a Google Drive entry by ID: {}", driveId);

        Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveService.getGoogleDriveById(driveId);

        if (optionalGoogleDriveDTO.isPresent()) {
            logger.info("Google Drive retrieved successfully for ID: {}", driveId);
            return ResponseEntity.ok().body(optionalGoogleDriveDTO.get());
        } else {
            logger.info("Google Drive not found for ID: {}", driveId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a Google Drive entry by its ID.
     *
     * @param driveId The ID of the Google Drive entry to delete.
     * @return ResponseEntity indicating the result of the operation.
     */
    @DeleteMapping("/deleteGoogleDriveById/{driveId}")
    @Operation(
            description = "Delete Google Drive by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Google Drive deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Google Drive not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteGoogleDriveById(@PathVariable Long driveId) {
        logger.info("Received a request to delete a Google Drive entry by ID: {}", driveId);

        boolean deleted = googleDriveService.deleteGoogleDriveById(driveId);

        if (deleted) {
            logger.info("Google Drive with ID {} deleted successfully.", driveId);
            return ResponseEntity.ok("Google Drive with ID: " + driveId + " deleted successfully.");
        } else {
            logger.info("Google Drive not found for ID: {}", driveId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get a Google Drive entry by its associated Project ID.
     *
     * @param projectId The Project ID associated with the Google Drive entry.
     * @return ResponseEntity with the retrieved Google Drive entry or an error response if not found.
     */
    @GetMapping("/getGoogleDriveByProjectId/{projectId}")
    @Operation(
            description = "Get Google Drive by Project ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Google Drive retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Google Drive not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveByProjectId(@PathVariable Long projectId) {
        logger.info("Received a request to retrieve a Google Drive entry by Project ID: {}", projectId);

        Optional<GoogleDrive> optionalGoogleDrive = googleDriveService.getGoogleDriveByProjectId(projectId);

        if (optionalGoogleDrive.isPresent()) {
            GoogleDrive googleDrive = optionalGoogleDrive.get();
            GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(
                    new ProjectDTO(googleDrive.getProject().getProjectId(), googleDrive.getProject().getProjectName()),
                    googleDrive.getDriveLink(),
                    googleDrive.getDriveId()
            );
            logger.info("Google Drive retrieved successfully for Project ID: {}", projectId);
            return ResponseEntity.ok(googleDriveDTO);
        } else {
            logger.info("Google Drive not found for Project ID: {}", projectId);
            return ResponseEntity.notFound().build();
        }
    }

}