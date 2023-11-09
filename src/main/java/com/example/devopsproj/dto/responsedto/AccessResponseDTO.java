package com.example.devopsproj.dto.responsedto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;



/**
 * Data Transfer Object (DTO) for an access response.
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccessResponseDTO {
    /**
     * The unique identifier of the access request.
     */
    @NotNull(message = "accessRequestId cannot be null")
    @Positive(message = "accessRequestId should be a positive number")
    private Long accessRequestId;

    /**
     * The name of the Project Manager (pm) associated with the access request.
     */
    @NotBlank(message = "pmName cannot be blank")
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
     * The description of the access request. Should not exceed 255 characters.
     */
    @Size(max = 255, message = "accessDescription should not exceed 255 characters")
    private String accessDescription;

    /**
     * Indicates whether the access request is allowed.
     */
    private boolean allowed;

    /**
     * The response to the access request.
     */
    private String response;

    /**
     * Indicates whether the user has been notified about the access request.
     */
    private boolean notified;

    /**
     * Constructor for initializing basic access request details.
     *
     * @param accessRequestId     The unique identifier of the access request.
     * @param pmName              The name of the Project Manager associated with the access request.
     * @param user                The user associated with the access request.
     * @param project             The project associated with the access request.
     * @param accessDescription   The description of the access request.
     * @param allowed             Indicates whether the access request is allowed.
     */
    public AccessResponseDTO(Long accessRequestId, String pmName, UserDTO user, ProjectDTO project, String accessDescription, boolean allowed) {
        this.accessRequestId = accessRequestId;
        this.pmName = pmName;
        this.user = user;
        this.project = project;
        this.accessDescription = accessDescription;
        this.allowed = allowed;
    }
}

