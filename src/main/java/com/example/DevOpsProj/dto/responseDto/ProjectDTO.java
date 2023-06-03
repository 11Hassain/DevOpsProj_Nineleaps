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
    private List<RepositoryDTO> repositories;
    private boolean status;


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
}
