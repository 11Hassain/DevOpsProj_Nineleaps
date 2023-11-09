package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Data Transfer Object (DTO) for representing an access request.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class AccessRequestDTO {

    /**
     * The unique identifier for the access request.
     */
    @Positive(message = "accessRequestId should be a positive number")
    private Long accessRequestId;

    /**
     * The name of the project manager associated with the access request.
     */
    @NotBlank(message = "pmName cannot be blank")
    @Size(max = 50, message = "pmName should not exceed 50 characters")
    private String pmName;

    /**
     * The user associated with the access request.
     */
    private UserDTO user;

    /**
     * The project associated with the access request.
     */
    private ProjectDTO project;

    /**
     * A description of the access request.
     */
    private String requestDescription;

    /**
     * A flag indicating whether the access request is allowed.
     */
    private boolean allowed;
}
