package com.example.DevOpsProj.dto.responseDto;

import com.example.DevOpsProj.model.User;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ProjectDTO {
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private List<User> users;
    private List<RepositoryDTO> repositories;

    public ProjectDTO(Long projectId, String projectName, String projectDescription) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    public ProjectDTO(Long projectId, String projectName, String projectDescription, List<User> users) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.users = users;
    }
}
