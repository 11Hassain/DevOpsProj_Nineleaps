package com.example.DevOpsProj.dto.requestDto;

import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class AccessRequestDTO {
    private Long accessRequestId;
    private String pmName;
    private User user;
    private Project project;
    private String requestDescription;
    private boolean allowed;
}
