package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class ProjectWithUsersDTO {

    @Positive(message = "projectId should be a positive number")
    private Long projectId;

    @Size(max = 255, message = "projectName should not exceed 255 characters")
    private String projectName;

    private String projectDescription;

    @NotBlank(message = "lastUpdated cannot be blank")
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

