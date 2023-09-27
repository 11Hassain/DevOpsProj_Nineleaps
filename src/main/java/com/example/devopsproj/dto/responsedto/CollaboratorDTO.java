package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@ToString
@Data
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

