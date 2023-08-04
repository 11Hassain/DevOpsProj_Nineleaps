package com.example.DevOpsProj.dto.responseDto;

import com.example.DevOpsProj.model.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ProjectDTO {

    private Long projectId;
    private String projectName;
    private String projectDescription;
    private LocalDateTime lastUpdated;
    private List<User> users;
    private List<GitRepositoryDTO> repositories;
    private boolean status;
    private FigmaDTO figma;
    private GoogleDriveDTO googleDrive;



    public ProjectDTO(Long projectId, String projectName, String projectDescription, List<GitRepositoryDTO> repositories, FigmaDTO figma, GoogleDriveDTO googleDrive) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.repositories = repositories;
        this.figma = figma;
        this.googleDrive = googleDrive;
    }

    public ProjectDTO(Long projectId, String projectName, String projectDescription) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    public ProjectDTO(Long projectId, String projectName, String projectDescription, LocalDateTime lastUpdated) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.lastUpdated = lastUpdated;
    }

    public ProjectDTO(Long projectId, String projectName, String projectDescription, LocalDateTime lastUpdated, List<User> users) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.lastUpdated = lastUpdated;
        this.users = users;
    }

    public ProjectDTO(Long projectId, String projectName, String projectDescription, LocalDateTime lastUpdated, boolean status) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.lastUpdated = lastUpdated;
        this.status = status;
    }
    public ProjectDTO(Long projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }
}
