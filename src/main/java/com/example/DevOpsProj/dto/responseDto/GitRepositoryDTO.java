package com.example.DevOpsProj.dto.responseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class GitRepositoryDTO {
    private String name;
    private String description;
}
