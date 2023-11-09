package com.example.devopsproj.dto.responsedto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/**
 * Data Transfer Object (DTO) for a collaborator.
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class CollaboratorDTO {
    /**
     * The owner of the repository.
     */
    @NotBlank(message = "Owner cannot be blank")
    @Size(max = 255, message = "Owner should not exceed 255 characters")
    private String owner;

    /**
     * The name of the repository.
     */
    @NotBlank(message = "Repo cannot be blank")
    @Size(max = 255, message = "Repo should not exceed 255 characters")
    private String repo;

    /**
     * The username of the collaborator.
     */
    @NotBlank(message = "Username cannot be blank")
    @Size(max = 255, message = "Username should not exceed 255 characters")
    private String username;

    /**
     * The access token for the collaborator.
     */
    @NotBlank(message = "Access Token cannot be blank")
    private String accessToken;

    /**
     * Constructor for initializing collaborator details.
     *
     * @param owner      The owner of the repository.
     * @param repo       The name of the repository.
     * @param username   The username of the collaborator.
     * @param accessToken The access token for the collaborator.
     */
    public CollaboratorDTO(String owner, String repo, String username, String accessToken) {
        this.owner = owner;
        this.repo = repo;
        this.username = username;
        this.accessToken = accessToken;
    }
}

