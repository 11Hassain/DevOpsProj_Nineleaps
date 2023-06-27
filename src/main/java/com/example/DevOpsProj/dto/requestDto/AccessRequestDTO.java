package com.example.DevOpsProj.dto.requestDto;

import com.example.DevOpsProj.dto.responseDto.ProjectDTO;
import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class AccessRequestDTO {
    private Long accessRequestId;
    private String pmName;
    private UserDTO user;
    private ProjectDTO project;
    private String requestDescription;
    private boolean allowed;
}
