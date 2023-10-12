package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * The AccessResponseDTO class represents a data transfer object for access request responses.
 * It includes details such as the access request ID, project manager name, user information, project details,
 * access description, whether the access is allowed, response message, and notification status.
 *
 * @version 2.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccessResponseDTO {

    @NotNull(message = "accessRequestId cannot be null")
    @Positive(message = "accessRequestId should be a positive number")
    private Long accessRequestId;

    @NotBlank(message = "pmName cannot be blank")
    private String pmName;

    private UserDTO user;
    private ProjectDTO project;

    @Size(max = 255, message = "accessDescription should not exceed 255 characters")
    private String accessDescription;

    private boolean allowed;
    private String response;
    private boolean notified;

    public AccessResponseDTO(Long accessRequestId, String pmName, UserDTO user, ProjectDTO project, String accessDescription, boolean allowed) {
        this.accessRequestId = accessRequestId;
        this.pmName = pmName;
        this.user = user;
        this.project = project;
        this.accessDescription = accessDescription;
        this.allowed = allowed;
    }
}
