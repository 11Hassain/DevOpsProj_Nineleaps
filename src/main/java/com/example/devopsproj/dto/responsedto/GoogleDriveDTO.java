package com.example.devopsproj.dto.responsedto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


@NoArgsConstructor
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
    public GoogleDriveDTO(String driveLink) {
        this.driveLink = driveLink;
    }

    public GoogleDriveDTO(ProjectDTO projectDTO, String driveLink, Long driveId) {
        this.projectDTO = projectDTO;
        this.driveLink = driveLink;
        this.driveId = driveId;
    }

    public GoogleDriveDTO(String driveLink, Long driveId) {
        this.driveLink = driveLink;
        this.driveId = driveId;
    }
}