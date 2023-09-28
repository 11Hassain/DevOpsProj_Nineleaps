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
    @PostMapping("/create")
    public ResponseEntity<GoogleDriveDTO> createGoogleDrive(@RequestBody GoogleDriveDTO googleDriveDTO) {
        GoogleDriveDTO createdGoogleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoogleDrive);
    }

    // Get a list of all Google Drive entries.

    @GetMapping("/getAllGoogleDrives")
    public ResponseEntity<List<GoogleDriveDTO>> getAllGoogleDrives() {
        List<GoogleDriveDTO> googleDriveDTOs = googleDriveService.getAllGoogleDrives();
        return ResponseEntity.ok(googleDriveDTOs);
    }

    // Get a Google Drive entry by ID.
    @GetMapping("/getGoogleDriveById/{driveId}")
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveById(@PathVariable Long driveId) {
        Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveService.getGoogleDriveById(driveId);

        return optionalGoogleDriveDTO
                .map(googleDriveDTO -> ResponseEntity.ok(googleDriveDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a Google Drive entry by ID.
    @DeleteMapping("/deleteGoogleDriveById/{driveId}")
    public ResponseEntity<String> deleteGoogleDriveById(@PathVariable Long driveId) {
        return googleDriveService.deleteGoogleDriveById(driveId);
    }


    // Get a Google Drive entry by project ID.
    @GetMapping("/getGoogleDriveByProjectId/{projectId}")
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveByProjectId(@PathVariable Long projectId) {
        return googleDriveService.getGoogleDriveByProjectId(projectId);
    }

}
