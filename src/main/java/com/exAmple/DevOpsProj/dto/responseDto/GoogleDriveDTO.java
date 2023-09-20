package com.exAmple.DevOpsProj.dto.responseDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class GoogleDriveDTO {

    @NotNull(message = "projectDTO cannot be null")
    private ProjectDTO projectDTO;

    @NotBlank(message = "driveLink cannot be blank")
    private String driveLink;

    @Positive(message = "driveId should be a positive number")
    private Long driveId;

    private String message;

    public GoogleDriveDTO(String driveLink) {
        this.driveLink = driveLink;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GoogleDriveDTO(ProjectDTO projectDTO, String driveLink, Long driveId) {
        this.projectDTO = projectDTO;
        this.driveLink = driveLink;
        this.driveId = driveId;
    }
}