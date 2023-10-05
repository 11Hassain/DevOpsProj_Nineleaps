package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.model.GoogleDrive;

import java.util.List;
import java.util.Optional;

public interface GoogleDriveService {
    GoogleDrive createGoogleDrive(GoogleDriveDTO googleDriveDTO);

    List<GoogleDrive> getAllGoogleDrives();

    Optional<GoogleDriveDTO> getGoogleDriveById(Long driveId);

    boolean deleteGoogleDriveById(Long driveId);

    Optional<GoogleDrive> getGoogleDriveByProjectId(Long projectId);
}
