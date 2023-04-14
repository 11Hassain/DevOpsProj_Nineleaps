package com.example.DevOpsProj.dto.responseDto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ProjectDTO {
    private Long projectId;
    private String projectName;
    private String projectDescription;

}
