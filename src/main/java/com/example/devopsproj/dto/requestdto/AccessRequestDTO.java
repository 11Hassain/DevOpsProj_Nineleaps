package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class AccessRequestDTO {
    private Long accessRequestId;
    private String pmName;
    private UserDTO user;
    private ProjectDTO project;
    private String requestDescription;
    private boolean allowed;
}
