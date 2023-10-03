package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GoogleDriveRepository;
import com.example.devopsproj.service.interfaces.GoogleDriveService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private final GoogleDriveRepository googleDriveRepository;

    // Create a new Google Drive entry
    @Override
    public GoogleDriveDTO createGoogleDrive(GoogleDriveDTO googleDriveDTO) {
        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setProject(mapProjectDTOToProject(googleDriveDTO.getProjectDTO()));
        googleDrive.setDriveLink(googleDriveDTO.getDriveLink());

        GoogleDrive savedGoogleDrive = googleDriveRepository.save(googleDrive);

        return new GoogleDriveDTO(
                mapProjectToProjectDTO(savedGoogleDrive.getProject()),
                savedGoogleDrive.getDriveLink(),
                savedGoogleDrive.getDriveId()
        );
    }
    // Get a list of all Google Drive entries
    @Override
    public List<GoogleDriveDTO> getAllGoogleDrives() {
        List<GoogleDrive> googleDrives = googleDriveRepository.findAll();
        List<GoogleDriveDTO> googleDriveDTOs = new ArrayList<>();
        for (GoogleDrive googleDrive : googleDrives) {
            googleDriveDTOs.add(new GoogleDriveDTO(
                    mapProjectToProjectDTO(googleDrive.getProject()),
                    googleDrive.getDriveLink(),
                    googleDrive.getDriveId()
            ));
        }
        return googleDriveDTOs;
    }

    // Get a Google Drive entry by its ID
    @Override
    public Optional<GoogleDriveDTO> getGoogleDriveById(Long driveId) {
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);

        return optionalGoogleDrive.map(googleDrive -> new GoogleDriveDTO(
                mapProjectToProjectDTO(googleDrive.getProject()),
                googleDrive.getDriveLink(),
                googleDrive.getDriveId()
        ));
    }

    // Delete a Google Drive entry by its ID
    @Override
    public ResponseEntity<String> deleteGoogleDriveById(Long driveId) {
        // Check if a Google Drive entry with the specified ID exists
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        if (optionalGoogleDrive.isPresent()) {
            // If it exists, delete it from the repository and return a success response
            googleDriveRepository.deleteById(driveId);
            return ResponseEntity.ok("Google Drive with ID: " + driveId + " deleted successfully.");
        } else {
            // If it doesn't exist, return a not found response
            return ResponseEntity.notFound().build();
        }
    }



    // Get a Google Drive entry by its associated Project ID
    // Get a Google Drive entry by its associated Project ID
    @Override
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveByProjectId(Long projectId) {
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findGoogleDriveByProjectId(projectId);

        return optionalGoogleDrive.map(this::mapToGoogleDriveDTO)
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<GoogleDriveDTO> mapToGoogleDriveDTO(GoogleDrive googleDrive) {
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(
                new ProjectDTO(googleDrive.getProject().getProjectId(), googleDrive.getProject().getProjectName()),
                googleDrive.getDriveLink(),
                googleDrive.getDriveId()
        );
        return ResponseEntity.ok(googleDriveDTO);
    }

    // Helper method to map Project to ProjectDTO
    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }

    // Add the missing method to map ProjectDTO to Project
    public Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        return project;
    }
}