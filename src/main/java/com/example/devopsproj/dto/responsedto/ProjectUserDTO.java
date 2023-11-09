package com.example.devopsproj.dto.responsedto;

import lombok.*;

import java.util.List;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for projects and their associated users.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProjectUserDTO {
    /**
     * The unique identifier of the project (positive number).
     */
    @Positive(message = "projectId should be a positive number")
    private Long projectId;

    /**
     * The name of the project (up to 100 characters).
     */
    @Size(max = 100, message = "projectName should not exceed 100 characters")
    private String projectName;

    /**
     * The description of the project.
     */
    private String projectDescription;

    /**
     * The list of users associated with the project.
     */
    private List<UserDTO> users;
}
