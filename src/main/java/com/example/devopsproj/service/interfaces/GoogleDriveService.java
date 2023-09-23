package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responseDto.GoogleDriveDTO;
import com.example.devopsproj.model.GoogleDrive;

import java.util.List;
import java.util.Optional;

public interface GoogleDriveService {
    GoogleDrive createGoogleDrive(GoogleDriveDTO googleDriveDTO);

    List<GoogleDrive> getAllGoogleDrives();

    boolean deleteGoogleDriveById(Long driveId);

    Optional<GoogleDrive> getGoogleDriveByProjectId(Long projectId);
}
