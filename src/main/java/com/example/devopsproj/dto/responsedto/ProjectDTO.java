package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.model.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for Project details.
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProjectDTO {
    /**
     * The unique identifier of the project (positive number).
     */
    @Positive(message = "projectId should be a positive number")
    private Long projectId;

    /**
     * The name of the project (should not be blank and not exceed 255 characters).
     */
    @NotBlank(message = "projectName cannot be blank")
    @Size(max = 255, message = "projectName should not exceed 255 characters")
    private String projectName;

    /**
     * The description of the project (should not exceed 255 characters).
     */
    @Size(max = 255, message = "projectDescription should not exceed 255 characters")
    private String projectDescription;

    /**
     * The timestamp when the project was last updated (cannot be null).
     */
    @NotNull(message = "lastUpdated cannot be null")
    private LocalDateTime lastUpdated;

    /**
     * List of users associated with the project.
     */
    private List<User> users;

    /**
     * The name of the Project Manager.
     */
    private String pmName;

    /**
     * List of Git repositories associated with the project.
     */
    private List<GitRepositoryDTO> repositories;

    /**
     * The status of the project (active or inactive).
     */
    private boolean status;

    /**
     * Figma details associated with the project.
     */
    private FigmaDTO figma;

    /**
     * Google Drive details associated with the project.
     */
    private GoogleDriveDTO googleDrive;

    /**
     * Help documents associated with the project.
     */
    private String helpDocuments;



    public ProjectDTO(Long projectId, String projectName, String projectDescription, List<GitRepositoryDTO> repositories,  FigmaDTO figma, GoogleDriveDTO googleDrive) {
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
            String pmName,
            List<GitRepositoryDTO> repositories,
            FigmaDTO figma,
            GoogleDriveDTO googleDrive,
            LocalDateTime lastUpdated) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.pmName = pmName;
        this.repositories = repositories;
        this.figma = figma;
        this.googleDrive = googleDrive;
        this.lastUpdated = lastUpdated;
    }




}