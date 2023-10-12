package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * The AccessRequestDTO class represents a data transfer object for access request information.
 * It contains details about an access request, including the access request ID, project manager name, associated user,
 * associated project, request description, and whether the request is allowed or not.
 *
 * @version 2.0
 */

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class AccessRequestDTO {

    @Positive(message = "accessRequestId should be a positive number")
    private Long accessRequestId;

    @NotBlank(message = "pmName cannot be blank")
    @Size(max = 50, message = "pmName should not exceed 50 characters")
    private String pmName;

    private UserDTO user;
    private ProjectDTO project;
    private String requestDescription;
    private boolean allowed;
}
