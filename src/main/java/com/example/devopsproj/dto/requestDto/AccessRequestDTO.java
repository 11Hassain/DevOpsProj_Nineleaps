package com.example.devopsproj.dto.requestDto;

import com.example.devopsproj.dto.responseDto.ProjectDTO;
import com.example.devopsproj.dto.responseDto.UserDTO;
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
