package com.exAmple.DevOpsProj.dto.responseDto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProjectUserDTO {

    @Positive(message = "projectId should be a positive number")
    private Long projectId;

    @Size(max = 100, message = "projectName should not exceed 100 characters")
    private String projectName;

    private String projectDescription;
    private List<UserDTO> users;
}
