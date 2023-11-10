package com.example.devopsproj.dto.responsedto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for projects with associated users and other details.
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProjectWithUsersDTO {
    /**
     * The unique identifier of the project (positive number).
     */
    @Positive(message = "projectId should be a positive number")
    private Long projectId;

    /**
     * The name of the project (up to 255 characters).
     */
    @Size(max = 255, message = "projectName should not exceed 255 characters")
    private String projectName;
    /**
     * The description of the project.
     */
    private String projectDescription;
    /**
     * The timestamp of the last update.
     */
    @NotBlank(message = "lastUpdated cannot be blank")
    private LocalDateTime lastUpdated;
    /**
     * The list of users associated with the project.
     */
    private List<UserDTO> users;
    /**
     * The list of repositories associated with the project.
     */
    private List<GitRepositoryDTO> repositories;
    /**
     * The timestamp of the status of the project.
     */
    private boolean status;
    /**
     * The list of figma associated with the project.
     */
    private FigmaDTO figma;
    /**
     * The list of drives associated with the project.
     */
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



