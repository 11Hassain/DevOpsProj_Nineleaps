package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.service.implementations.GoogleDriveServiceImpl;
import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.utils.DTOModelMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final GoogleDriveServiceImpl googleDriveServiceImpl;

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
            GoogleDrive googleDrive = googleDriveServiceImpl.createGoogleDrive(googleDriveDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new GoogleDriveDTO(
                    DTOModelMapper.mapProjectToProjectDTO(googleDrive.getProject()),
                    googleDrive.getDriveLink(),
                    googleDrive.getDriveId()
            ));

    }

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

            List<GoogleDrive> googleDrives = googleDriveServiceImpl.getAllGoogleDrives();
            List<GoogleDriveDTO> googleDriveDTOs = new ArrayList<>();
            for (GoogleDrive googleDrive : googleDrives) {
                googleDriveDTOs.add(new GoogleDriveDTO(
                        new ProjectDTO(googleDrive.getProject().getProjectId(), googleDrive.getProject().getProjectName()),
                        googleDrive.getDriveLink(),
                        googleDrive.getDriveId()
                ));
            }
            return ResponseEntity.ok(googleDriveDTOs);

    }

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

            Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveServiceImpl.getGoogleDriveById(driveId);
            return optionalGoogleDriveDTO
                    .map(googleDriveDTO -> ResponseEntity.ok().body(googleDriveDTO))
                    .orElse(ResponseEntity.notFound().build());

    }

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

            boolean deleted = googleDriveServiceImpl.deleteGoogleDriveById(driveId);
            if (deleted) {
                return ResponseEntity.ok("Google Drive with ID: " + driveId + " deleted successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }

    }

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

            Optional<GoogleDrive> optionalGoogleDrive = googleDriveServiceImpl.getGoogleDriveByProjectId(projectId);
            return optionalGoogleDrive.map(googleDrive -> {
                GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(
                        new ProjectDTO(googleDrive.getProject().getProjectId(), googleDrive.getProject().getProjectName()),
                        googleDrive.getDriveLink(),
                        googleDrive.getDriveId()
                );
                return ResponseEntity.ok(googleDriveDTO);
            }).orElse(ResponseEntity.notFound().build());

    }

}