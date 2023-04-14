package com.example.DevOpsProj.dto.responseDto;

import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.model.User;
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
