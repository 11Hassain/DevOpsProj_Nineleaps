package com.exAmple.DevOpsProj.dto.responseDto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class GoogleDriveDTO {
    private ProjectDTO projectDTO;
    private String driveLink;
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