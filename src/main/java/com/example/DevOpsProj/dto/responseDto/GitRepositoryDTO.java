package com.example.DevOpsProj.dto.responseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class GitRepositoryDTO {
    private Long repoId;
    private String name;
    private String description;

    public GitRepositoryDTO(String name, String description){
        this.name = name;
        this.description = description;
    }
}
