package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.dto.responseDto.GoogleDriveDTO;
import com.example.DevOpsProj.dto.responseDto.ProjectDTO;
import com.example.DevOpsProj.model.GoogleDrive;
import com.example.DevOpsProj.service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class GoogleDriveController {

    @Autowired
    private GoogleDriveService googleDriveService;

    @PostMapping("/createGoogleDrive")
    public ResponseEntity<GoogleDriveDTO> createGoogleDrive(@RequestBody GoogleDriveDTO googleDriveDTO) {
        GoogleDrive googleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GoogleDriveDTO(
                googleDriveService.mapProjectToProjectDTO(googleDrive.getProject()),
                googleDrive.getDriveLink(),
                googleDrive.getDriveId()
        ));
    }

    @GetMapping("/getAllGoogleDrives")
    public ResponseEntity<List<GoogleDriveDTO>> getAllGoogleDrives() {
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


    @GetMapping("/getGoogleDriveById/{driveId}")
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveById(@PathVariable Long driveId) {
        Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveService.getGoogleDriveById(driveId);
        return optionalGoogleDriveDTO
                .map(googleDriveDTO -> ResponseEntity.ok().body(googleDriveDTO))
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/deleteGoogleDriveById/{driveId}")
    public ResponseEntity<String> deleteGoogleDriveById(@PathVariable Long driveId) {
        boolean deleted = googleDriveService.deleteGoogleDriveById(driveId);
        if (deleted) {
            return ResponseEntity.ok("Google Drive with ID: " + driveId + " deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}