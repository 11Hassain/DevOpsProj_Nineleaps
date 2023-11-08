package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;

import com.example.devopsproj.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface GoogleDriveService {
    GoogleDriveDTO createGoogleDrive(GoogleDriveDTO googleDriveDTO);

    Page<GoogleDriveDTO> getAllGoogleDrives(Pageable pageable);

    Optional<GoogleDriveDTO> getGoogleDriveById(Long driveId);

    ResponseEntity<String> deleteGoogleDriveById(Long driveId);

    ResponseEntity<GoogleDriveDTO> getGoogleDriveByProjectId(Long projectId);

    ProjectDTO mapProjectToProjectDTO(Project project);
}