package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.commons.enumerations.EnumRole;
import lombok.*;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {

    @Positive(message = "id should be a positive number")
    @NotNull(message = "id cannot be null")
    private Long id;

    @Size(max = 50, message = "projectName should not exceed 50 characters")
    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be black")
    private String name;

    @NotNull(message = "email cannot be null")
    @NotBlank(message = "email cannot be blank")
    private String email;

    private EnumRole enumRole;
    private String token;

    @Size(max = 255, message = "gitHubUsername should not exceed 255 characters")
    private String gitHubUsername;

    @NotNull(message = "lastUpdated cannot be null")
    private LocalDateTime lastUpdated;

    @NotNull(message = "lastLogout cannot be null")
    private LocalDateTime lastLogout;

    public UserDTO(Long id, String name, String email, EnumRole enumRole) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.enumRole = enumRole;
    }

    public UserDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDTO(String name, EnumRole enumRole, LocalDateTime lastUpdated) {
        this.name = name;
        this.enumRole = enumRole;
        this.lastUpdated = lastUpdated;
    }

    public UserDTO(Long id, String name, String email, EnumRole enumRole, String gitHubUsername) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.enumRole = enumRole;
        this.gitHubUsername = gitHubUsername;
    }

    public UserDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.enumRole = enumRole;
        this.lastUpdated = lastUpdated;
    }

    public UserDTO(Long id, String name, String email, EnumRole enumRole, LocalDateTime lastUpdated, LocalDateTime lastLogout) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.enumRole = enumRole;
        this.lastUpdated = lastUpdated;
        this.lastLogout = lastLogout;
    }
}
