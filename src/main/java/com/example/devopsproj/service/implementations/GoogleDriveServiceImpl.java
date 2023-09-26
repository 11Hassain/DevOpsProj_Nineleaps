package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GoogleDriveRepository;
import com.example.devopsproj.service.interfaces.GoogleDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private final GoogleDriveRepository googleDriveRepository;


    // Create a new Google Drive entry
    @Override
    public GoogleDrive createGoogleDrive(GoogleDriveDTO googleDriveDTO) {
        // Create a new GoogleDrive object and populate it with data from the DTO
        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setProject(mapProjectDTOToProject(googleDriveDTO.getProjectDTO()));
        googleDrive.setDriveLink(googleDriveDTO.getDriveLink());

        // Save the new Google Drive entry to the repository
        return googleDriveRepository.save(googleDrive);
    }

    // Get a list of all Google Drive entries
    @Override
    public List<GoogleDrive> getAllGoogleDrives() {
        // Retrieve all Google Drive entries from the repository
        return googleDriveRepository.findAll();
    }

    // Get a Google Drive entry by its ID
    @Override
    public Optional<GoogleDriveDTO> getGoogleDriveById(Long driveId) {
        // Retrieve the Google Drive entry by its ID
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);

        // Map the Google Drive entry to a GoogleDriveDTO if it exists
        return optionalGoogleDrive.map(googleDrive -> new GoogleDriveDTO(
                mapProjectToProjectDTO(googleDrive.getProject()),
                googleDrive.getDriveLink(),
                googleDrive.getDriveId()
        ));
    }

    // Delete a Google Drive entry by its ID
    @Override
    public boolean deleteGoogleDriveById(Long driveId) {
        // Check if a Google Drive entry with the specified ID exists
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        if (optionalGoogleDrive.isPresent()) {
            // If it exists, delete it from the repository and return true
            googleDriveRepository.deleteById(driveId);
            return true;
        } else {
            // If it doesn't exist, return false
            return false;
        }
    }

    // Get a Google Drive entry by its associated Project ID
    @Override
    public Optional<GoogleDrive> getGoogleDriveByProjectId(Long projectId) {
        // Retrieve the Google Drive entry associated with the specified Project ID
        return googleDriveRepository.findGoogleDriveByProjectId(projectId);
    }

    // Helper method to map Project to ProjectDTO
    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }

    // Add the missing method to map ProjectDTO to Project
    private Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        return project;
    }
}