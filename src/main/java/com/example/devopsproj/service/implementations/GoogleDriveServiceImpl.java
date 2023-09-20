package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responseDto.GoogleDriveDTO;
import com.example.devopsproj.dto.responseDto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GoogleDriveRepository;
import com.example.devopsproj.service.interfaces.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private final GoogleDriveRepository googleDriveRepository;

    @Autowired
    public GoogleDriveServiceImpl(GoogleDriveRepository googleDriveRepository) {
        this.googleDriveRepository = googleDriveRepository;
    }

    @Override
    public GoogleDrive createGoogleDrive(GoogleDriveDTO googleDriveDTO) {
        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setProject(mapProjectDTOToProject(googleDriveDTO.getProjectDTO()));
        googleDrive.setDriveLink(googleDriveDTO.getDriveLink());
        return googleDriveRepository.save(googleDrive);
    }

    @Override
    public List<GoogleDrive> getAllGoogleDrives() {
        return googleDriveRepository.findAll();
    }

    @Override
    public Optional<GoogleDriveDTO> getGoogleDriveById(Long driveId) {
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        return optionalGoogleDrive.map(googleDrive -> new GoogleDriveDTO(
                mapProjectToProjectDTO(googleDrive.getProject()),
                googleDrive.getDriveLink(),
                googleDrive.getDriveId()
        ));
    }

    @Override
    public boolean deleteGoogleDriveById(Long driveId) {
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        if (optionalGoogleDrive.isPresent()) {
            googleDriveRepository.deleteById(driveId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<GoogleDrive> getGoogleDriveByProjectId(Long projectId) {
        return googleDriveRepository.findGoogleDriveByProjectId(projectId);
    }

    @Override
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
