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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private final GoogleDriveRepository googleDriveRepository;
    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveServiceImpl.class);

    // Create a new Google Drive entry
    @Override
    public GoogleDriveDTO createGoogleDrive(GoogleDriveDTO googleDriveDTO) {
        logger.info("Creating a new Google Drive entry");

        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setProject(mapProjectDTOToProject(googleDriveDTO.getProjectDTO()));
        googleDrive.setDriveLink(googleDriveDTO.getDriveLink());

        GoogleDrive savedGoogleDrive = googleDriveRepository.save(googleDrive);

        logger.info("Created Google Drive entry with ID: {}", savedGoogleDrive.getDriveId());

        return new GoogleDriveDTO(
                mapProjectToProjectDTO(savedGoogleDrive.getProject()),
                savedGoogleDrive.getDriveLink(),
                savedGoogleDrive.getDriveId()
        );
    }
    // Get a list of all Google Drive entries
    @Override
    public List<GoogleDriveDTO> getAllGoogleDrives() {
        logger.info("Retrieving all Google Drive entries");

        List<GoogleDrive> googleDrives = googleDriveRepository.findAll();
        List<GoogleDriveDTO> googleDriveDTOs = new ArrayList<>();
        for (GoogleDrive googleDrive : googleDrives) {
            googleDriveDTOs.add(new GoogleDriveDTO(
                    mapProjectToProjectDTO(googleDrive.getProject()),
                    googleDrive.getDriveLink(),
                    googleDrive.getDriveId()
            ));
        }

        logger.info("Retrieved {} Google Drive entries", googleDriveDTOs.size());

        return googleDriveDTOs;
    }

    // Get a Google Drive entry by its ID
    @Override
    public Optional<GoogleDriveDTO> getGoogleDriveById(Long driveId) {
        logger.info("Retrieving Google Drive entry by ID: {}", driveId);

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
        logger.info("Deleting Google Drive entry by ID: {}", driveId);

        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        if (optionalGoogleDrive.isPresent()) {
            GoogleDrive googleDrive = optionalGoogleDrive.get();
            googleDrive.setDeleted(true); // Soft delete
            googleDriveRepository.save(googleDrive);
            logger.info("Soft-deleted Google Drive with ID: {}", driveId);
            return ResponseEntity.ok("Google Drive with ID: " + driveId + " has been soft-deleted successfully.");
        } else {
            logger.info("Google Drive with ID: {} not found for deletion", driveId);
            return ResponseEntity.notFound().build();
        }
    }



    // Get a Google Drive entry by its associated Project ID
    @Override
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveByProjectId(Long projectId) {
        logger.info("Retrieving Google Drive entry by Project ID: {}", projectId);

        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findGoogleDriveByProjectId(projectId);

        return optionalGoogleDrive.map(this::mapToGoogleDriveDTO)
                .orElseGet(() -> {
                    logger.info("Google Drive entry not found for Project ID: {}", projectId);
                    return ResponseEntity.notFound().build();
                });
    }

    public ResponseEntity<GoogleDriveDTO> mapToGoogleDriveDTO(GoogleDrive googleDrive) {
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