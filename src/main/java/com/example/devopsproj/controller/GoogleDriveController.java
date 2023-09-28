package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.service.implementations.GoogleDriveServiceImpl;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
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

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class GoogleDriveController {

    private final GoogleDriveServiceImpl googleDriveServiceImpl;
    private final JwtServiceImpl jwtServiceImpl;

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
    public ResponseEntity<Object> createGoogleDrive(@Valid @RequestBody GoogleDriveDTO googleDriveDTO,
                                                            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
            GoogleDrive googleDrive = googleDriveServiceImpl.createGoogleDrive(googleDriveDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new GoogleDriveDTO(
                    DTOModelMapper.mapProjectToProjectDTO(googleDrive.getProject()),
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
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
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
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
            Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveServiceImpl.getGoogleDriveById(driveId);
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
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
            boolean deleted = googleDriveServiceImpl.deleteGoogleDriveById(driveId);
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
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
            Optional<GoogleDrive> optionalGoogleDrive = googleDriveServiceImpl.getGoogleDriveByProjectId(projectId);
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