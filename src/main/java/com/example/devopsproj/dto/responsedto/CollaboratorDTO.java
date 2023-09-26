package com.example.devopsproj.dto.responsedto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@NoArgsConstructor
@Setter
@Getter
@ToString
@Data
public class CollaboratorDTO {

    @NotBlank(message = "Owner cannot be blank")
    @Size(max = 255, message = "Owner should not exceed 255 characters")
    private String owner;

    @NotBlank(message = "Repo cannot be blank")
    @Size(max = 255, message = "Repo should not exceed 255 characters")
    private String repo;

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 255, message = "Username should not exceed 255 characters")
    private String username;

    @NotBlank(message = "Access Token cannot be blank")
    private String accessToken;
    public CollaboratorDTO(String owner, String repo, String username, String accessToken) {
        this.owner = owner;
        this.repo = repo;
        this.username = username;
        this.accessToken = accessToken;
    }
}

