package com.example.devopsproj.dto.responseDto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProjectUserDTO {
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private List<UserDTO> users;
}
