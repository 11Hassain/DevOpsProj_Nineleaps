package com.example.DevOpsProj.dto.responseDto;

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

    public GoogleDriveDTO(String driveLink) {
        this.driveLink = driveLink;
    }
}