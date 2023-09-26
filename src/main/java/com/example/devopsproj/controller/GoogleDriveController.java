package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.service.interfaces.GoogleDriveService;
import com.example.devopsproj.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/googledrive")
@RequiredArgsConstructor
public class GoogleDriveController {
    private final GoogleDriveService googleDriveService;

    // Create a Google Drive entry.
    @PostMapping("/createGoogleDrive")
    public ResponseEntity<Object> createGoogleDrive(@RequestBody GoogleDriveDTO googleDriveDTO) {
        // Create a Google Drive entry and return its details.
        GoogleDrive googleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GoogleDriveDTO(
                googleDriveService.mapProjectToProjectDTO(googleDrive.getProject()),
                googleDrive.getDriveLink(),
                googleDrive.getDriveId()
        ));
    }

    // Get a list of all Google Drive entries.
    @GetMapping("/getAllGoogleDrives")
    public ResponseEntity<Object> getAllGoogleDrives() {
        // Retrieve all Google Drive entries and convert them to DTOs.
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
    }

    // Get a Google Drive entry by ID.
    @GetMapping("/getGoogleDriveById/{driveId}")
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveById(@PathVariable Long driveId) {
        // Retrieve a Google Drive entry by ID and return it as a DTO.
        Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveService.getGoogleDriveById(driveId);
        return optionalGoogleDriveDTO
                .map(googleDriveDTO -> ResponseEntity.ok().body(googleDriveDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a Google Drive entry by ID.
    @DeleteMapping("/deleteGoogleDriveById/{driveId}")
    public ResponseEntity<String> deleteGoogleDriveById(@PathVariable Long driveId) {
        // Delete a Google Drive entry by ID.
        boolean deleted = googleDriveService.deleteGoogleDriveById(driveId);
        if (deleted) {
            return ResponseEntity.ok("Google Drive with ID: " + driveId + " deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get a Google Drive entry by project ID.
    @GetMapping("/getGoogleDriveByProjectId/{projectId}")
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveByProjectId(@PathVariable Long projectId) {
        // Retrieve a Google Drive entry by project ID and return it as a DTO.
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveService.getGoogleDriveByProjectId(projectId);
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
