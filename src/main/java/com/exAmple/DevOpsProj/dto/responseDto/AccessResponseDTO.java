package com.exAmple.DevOpsProj.dto.responseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccessResponseDTO {
    private Long accessRequestId;
    private String pmName;
    private UserDTO user;
    private ProjectDTO project;
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
