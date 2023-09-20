package com.example.devopsproj.dto.responseDto;

import com.example.devopsproj.commons.enumerations.EnumRole;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private EnumRole enumRole;
    private String token;
    private String gitHubUsername;
    private LocalDateTime lastUpdated;
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

    public UserDTO(Long id, String name, String email, EnumRole enumRole, LocalDateTime lastUpdated) {
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
