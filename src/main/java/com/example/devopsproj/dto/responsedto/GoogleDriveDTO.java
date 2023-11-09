package com.example.devopsproj.dto.responsedto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


/**
 * Data Transfer Object (DTO) for Google Drive information.
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class GoogleDriveDTO {
    /**
     * The associated project information for the Google Drive.
     */
    @NotNull(message = "projectDTO cannot be null")
    private ProjectDTO projectDTO;

    /**
     * The Google Drive link.
     */
    @NotBlank(message = "driveLink cannot be blank")
    private String driveLink;

    /**
     * The unique identifier of the Google Drive.
     */
    @Positive(message = "driveId should be a positive number")
    private Long driveId;

    /**
     * Create a GoogleDriveDTO with the specified drive link.
     * @param driveLink The Google Drive link.
     */
    public GoogleDriveDTO(String driveLink) {
        this.driveLink = driveLink;
    }

    /**
     * Create a GoogleDriveDTO with the specified project information, drive link, and drive ID.
     * @param projectDTO The associated project information.
     * @param driveLink The Google Drive link.
     * @param driveId The unique identifier of the Google Drive.
     */
    public GoogleDriveDTO(ProjectDTO projectDTO, String driveLink, Long driveId) {
        this.projectDTO = projectDTO;
        this.driveLink = driveLink;
        this.driveId = driveId;
    }

    /**
     * Create a GoogleDriveDTO with the specified drive link and drive ID.
     * @param driveLink The Google Drive link.
     * @param driveId The unique identifier of the Google Drive.
     */
    public GoogleDriveDTO(String driveLink, Long driveId) {
        this.driveLink = driveLink;
        this.driveId = driveId;
    }
}
