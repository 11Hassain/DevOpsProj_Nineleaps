package com.exAmple.DevOpsProj.dto.responseDto;

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
