package com.exAmple.DevOpsProj.dto.responseDto;

import com.exAmple.DevOpsProj.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ProjectDTO {
    @Positive(message = "projectId should be a positive number")
    private Long projectId;

    @NotBlank(message = "projectName cannot be blank")
    @Size(max = 255, message = "projectName should not exceed 255 characters")
    private String projectName;

    @Size(max = 255, message = "projectDescription should not exceed 255 characters")
    private String projectDescription;

    @NotNull(message = "lastUpdated cannot be null")
    private LocalDateTime lastUpdated;

    private List<User> users;

    private String pmName;
    private List<GitRepositoryDTO> repositories;
    private boolean status;
    private FigmaDTO figma;
    private GoogleDriveDTO googleDrive;
    private String helpDocuments;

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
    public ProjectDTO(
            String projectName,
            String projectDescription,
            boolean status,
            String pmName,
            List<GitRepositoryDTO> repositories,
            FigmaDTO figma,
            GoogleDriveDTO googleDrive,
            LocalDateTime lastUpdated) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.status = status;
        this.pmName = pmName;
        this.repositories = repositories;
        this.figma = figma;
        this.googleDrive = googleDrive;
        this.lastUpdated = lastUpdated;
    }
}