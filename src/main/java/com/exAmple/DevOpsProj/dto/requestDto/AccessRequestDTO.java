package com.exAmple.DevOpsProj.dto.requestDto;

import com.exAmple.DevOpsProj.dto.responseDto.ProjectDTO;
import com.exAmple.DevOpsProj.dto.responseDto.UserDTO;

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
