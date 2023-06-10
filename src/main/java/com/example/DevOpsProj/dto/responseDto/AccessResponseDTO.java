package com.example.DevOpsProj.dto.responseDto;

import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccessResponseDTO {
    private Long accessRequestId;
    private String pmName;
    private UserDTO user;
    private ProjectDTO project;
    private String accessDescription;
    private boolean allowed;
}
