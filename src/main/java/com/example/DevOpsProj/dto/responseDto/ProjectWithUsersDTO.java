package com.example.DevOpsProj.dto.responseDto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ProjectWithUsersDTO {
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private LocalDateTime lastUpdated;
    private List<UserDTO> users;
    private List<GitRepositoryDTO> repositories;
    private boolean status;
    private FigmaDTO figma;
    private GoogleDriveDTO googleDrive;

    // Constructors
    public ProjectWithUsersDTO(Long projectId, String projectName, String projectDescription, LocalDateTime lastUpdated, List<UserDTO> users) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.lastUpdated = lastUpdated;
        this.users = users;
    }

    public ProjectWithUsersDTO(Long projectId, String projectName, String projectDescription) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    }

