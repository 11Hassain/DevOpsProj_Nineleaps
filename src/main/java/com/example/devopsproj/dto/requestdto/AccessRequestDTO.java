package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class AccessRequestDTO {

    @Positive(message = "accessRequestId should be a positive number")
    private Long accessRequestId;

    @NotBlank(message = "pmName cannot be blank")
    @Size(max = 50, message = "pmName should not exceed 50 characters")
    private String pmName;

    private UserDTO user;
    private ProjectDTO project;
    private String requestDescription;
    private boolean allowed;
}
