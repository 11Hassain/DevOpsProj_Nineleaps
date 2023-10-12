package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * The CollaboratorDTO class represents a data transfer object for collaborator information.
 * It includes details such as the owner's name, repository name, collaborator's username, and access token.
 *
 * @version 2.0
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CollaboratorDTO {

    @NotBlank(message = "Owner cannot be blank")
    @NotNull(message = "Owner cannot be null")
    @Size(max = 255, message = "Owner should not exceed 255 characters")
    private String owner;

    @NotBlank(message = "Repo cannot be blank")
    @NotNull(message = "Repo cannot be null")
    @Size(max = 255, message = "Repo should not exceed 255 characters")
    private String repo;

    @NotBlank(message = "Username cannot be blank")
    @NotNull(message = "Username cannot be null")
    @Size(max = 255, message = "Username should not exceed 255 characters")
    private String username;

    @NotBlank(message = "Access Token cannot be blank")
    @NotNull(message = "Access Token cannot be null")
    private String accessToken;

}

