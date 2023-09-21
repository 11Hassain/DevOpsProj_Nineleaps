package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responseDto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.service.GoogleDriveService;
import com.example.devopsproj.service.JwtService;
import com.example.devopsproj.dto.responseDto.GoogleDriveDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class GoogleDriveController {

    @Autowired
    private GoogleDriveService googleDriveService;
    @Autowired
    private JwtService jwtService;

    private static final String INVALID_TOKEN = "Invalid Token";

    @PostMapping("/createGoogleDrive")
    @Operation(
            description = "Create Google Drive",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Google Drive created successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createGoogleDrive(@RequestBody GoogleDriveDTO googleDriveDTO,
                                                            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            GoogleDrive googleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new GoogleDriveDTO(
                    googleDriveService.mapProjectToProjectDTO(googleDrive.getProject()),
                    googleDrive.getDriveLink(),
                    googleDrive.getDriveId()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
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
    public ResponseEntity<Object> getAllGoogleDrives(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<GoogleDrive> googleDrives = googleDriveService.getAllGoogleDrives();
            List<GoogleDriveDTO> googleDriveDTOs = new ArrayList<>();
            for (GoogleDrive googleDrive : googleDrives) {
                googleDriveDTOs.add(new GoogleDriveDTO(
                        new ProjectDTO(googleDrive.getProject().getProjectId(), googleDrive.getProject().getProjectName()),
                        googleDrive.getDriveLink(),
                        googleDrive.getDriveId()
                ));
            }
            return ResponseEntity.ok(googleDriveDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
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
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveById(@PathVariable Long driveId,
                                                     @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveService.getGoogleDriveById(driveId);
            return optionalGoogleDriveDTO
                    .map(googleDriveDTO -> ResponseEntity.ok().body(googleDriveDTO))
                    .orElse(ResponseEntity.notFound().build());
        } else {
            GoogleDriveDTO errorDTO = new GoogleDriveDTO();
            errorDTO.setMessage(INVALID_TOKEN);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
        }
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
    public ResponseEntity<String> deleteGoogleDriveById(@PathVariable Long driveId,
                                                        @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            boolean deleted = googleDriveService.deleteGoogleDriveById(driveId);
            if (deleted) {
                return ResponseEntity.ok("Google Drive with ID: " + driveId + " deleted successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
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
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveByProjectId(@PathVariable Long projectId,
                                                                    @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Optional<GoogleDrive> optionalGoogleDrive = googleDriveService.getGoogleDriveByProjectId(projectId);
            return optionalGoogleDrive.map(googleDrive -> {
                GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(
                        new ProjectDTO(googleDrive.getProject().getProjectId(), googleDrive.getProject().getProjectName()),
                        googleDrive.getDriveLink(),
                        googleDrive.getDriveId()
                );
                return ResponseEntity.ok(googleDriveDTO);
            }).orElse(ResponseEntity.notFound().build());
        } else {
            GoogleDriveDTO errorDTO = new GoogleDriveDTO();
            errorDTO.setMessage(INVALID_TOKEN);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
        }
    }

}