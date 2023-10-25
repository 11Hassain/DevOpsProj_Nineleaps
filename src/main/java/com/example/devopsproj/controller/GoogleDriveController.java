package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;

import com.example.devopsproj.service.interfaces.GoogleDriveService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/googledrive")
@RequiredArgsConstructor
public class GoogleDriveController {
    private final GoogleDriveService googleDriveService;

    // Create a Google Drive entry.
    @PostMapping("/create")
    @ApiOperation("Create a Google Drive entry")
    @ResponseStatus(HttpStatus.CREATED) // Replace with the appropriate status code
    public ResponseEntity<GoogleDriveDTO> createGoogleDrive(@RequestBody GoogleDriveDTO googleDriveDTO) {
        GoogleDriveDTO createdGoogleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoogleDrive);
    }


    // Get a list of all Google Drive entries.
    @GetMapping("/getAllGoogleDrives")
    @ApiOperation("Get a list of all Google Drive entries")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<List<GoogleDriveDTO>> getAllGoogleDrives() {
        List<GoogleDriveDTO> googleDriveDTOs = googleDriveService.getAllGoogleDrives();
        return ResponseEntity.ok(googleDriveDTOs);
    }


    // Get a Google Drive entry by ID.
    @GetMapping("/getGoogleDriveById/{driveId}")
    @ApiOperation("Get a Google Drive entry by ID")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveById(@PathVariable Long driveId) {
        Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveService.getGoogleDriveById(driveId);

        return optionalGoogleDriveDTO
                .map(ResponseEntity::ok) // Replace the lambda with method reference
                .orElse(ResponseEntity.notFound().build());
    }


    // Delete a Google Drive entry by ID.
    @DeleteMapping("/deleteGoogleDriveById/{driveId}")
    @ApiOperation("Delete a Google Drive entry by ID")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<String> deleteGoogleDriveById(@PathVariable Long driveId) {
        return googleDriveService.deleteGoogleDriveById(driveId);
    }



    // Get a Google Drive entry by project ID.
    @GetMapping("/getGoogleDriveByProjectId/{projectId}")
    @ApiOperation("Get a Google Drive entry by project ID")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveByProjectId(@PathVariable Long projectId) {
        return googleDriveService.getGoogleDriveByProjectId(projectId);
    }


}
