package com.example.DevOpsProj.dto.responseDto;

import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccessResponseDTO {
    private Long accessRequestId;
    private String pmName;
    private User user;
    private Project project;
    private String accessDescription;
    private boolean allowed;
}
