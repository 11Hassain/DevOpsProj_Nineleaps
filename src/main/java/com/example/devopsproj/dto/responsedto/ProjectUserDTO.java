package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * The ProjectUserDTO class represents a data transfer object for project information along with associated users.
 * It includes details such as project ID, project name, project description, and a list of associated users.
 *
 * @version 2.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProjectUserDTO {

    @Positive(message = "projectId should be a positive number")
    private Long projectId;

    @Size(max = 100, message = "projectName should not exceed 100 characters")
    private String projectName;

    private String projectDescription;
    private List<UserDTO> users;
}
