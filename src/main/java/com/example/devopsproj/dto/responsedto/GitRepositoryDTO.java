package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Data Transfer Object (DTO) for Git repository information.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class GitRepositoryDTO {
    /**
     * The unique identifier of the Git repository.
     */
    @Positive(message = "repoId should be a positive number")
    private Long repoId;

    /**
     * The name of the Git repository.
     */
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;

    /**
     * The description of the Git repository.
     */
    private String description;

    /**
     * Create a GitRepositoryDTO with the specified name and description.
     * @param name The name of the Git repository.
     * @param description The description of the Git repository.
     */
    public GitRepositoryDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
