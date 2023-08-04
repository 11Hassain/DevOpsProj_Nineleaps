package com.example.DevOpsProj.dto.responseDto;

import lombok.*;

import java.util.List;
@NoArgsConstructor
//@AllArgsConstructor
@Setter
@Getter
@ToString
public class ProjectWithUsersDTO {
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private List<UserDTO> users;

    public ProjectWithUsersDTO(Long projectId, String projectName, String projectDescription, List<UserDTO> users) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.users = users;
    }
}
