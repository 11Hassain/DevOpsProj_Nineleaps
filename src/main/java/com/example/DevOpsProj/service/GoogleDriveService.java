package com.example.DevOpsProj.service;

import com.example.DevOpsProj.dto.responseDto.GoogleDriveDTO;
import com.example.DevOpsProj.dto.responseDto.ProjectDTO;
import com.example.DevOpsProj.model.GoogleDrive;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.repository.GoogleDriveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoogleDriveService {

    @Autowired
    private GoogleDriveRepository googleDriveRepository;

    public GoogleDrive createGoogleDrive(GoogleDriveDTO googleDriveDTO) {
        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setProject(mapProjectDTOToProject(googleDriveDTO.getProjectDTO()));
        googleDrive.setDriveLink(googleDriveDTO.getDriveLink());
        return googleDriveRepository.save(googleDrive);
    }

    public List<GoogleDrive> getAllGoogleDrives() {
        return googleDriveRepository.findAll();
    }

    public Optional<GoogleDriveDTO> getGoogleDriveById(Long driveId) {
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        return optionalGoogleDrive.map(googleDrive -> new GoogleDriveDTO(
                mapProjectToProjectDTO(googleDrive.getProject()),
                googleDrive.getDriveLink(),
                googleDrive.getDriveId()
        ));
    }
    public boolean deleteGoogleDriveById(Long driveId) {
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        if (optionalGoogleDrive.isPresent()) {
            googleDriveRepository.deleteById(driveId);
            return true;
        } else {
            return false;
        }
    }


    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }

    public GoogleDrive mapGoogleDriveDTOToGoogleDrive(GoogleDriveDTO googleDriveDTO, GoogleDrive googleDrive) {
        googleDrive.setDriveId(googleDriveDTO.getDriveId());
        googleDrive.setDriveLink(googleDriveDTO.getDriveLink());
        googleDrive.setProject(mapProjectDTOToProject(googleDriveDTO.getProjectDTO()));
        return googleDrive;
    }

    public Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        return project;
    }
}
