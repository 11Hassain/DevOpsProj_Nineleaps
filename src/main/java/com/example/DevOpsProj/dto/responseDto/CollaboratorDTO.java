package com.example.DevOpsProj.dto.responseDto;

import lombok.*;

//@NoArgsConstructor
//@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString

public class CollaboratorDTO {
    private String owner;
    private String repo;
    private String username;
    private String accessToken;

    // Getters and setters

    // Constructor
    public CollaboratorDTO(String owner, String repo, String username, String accessToken) {
        this.owner = owner;
        this.repo = repo;
        this.username = username;
        this.accessToken = accessToken;
    }
}

