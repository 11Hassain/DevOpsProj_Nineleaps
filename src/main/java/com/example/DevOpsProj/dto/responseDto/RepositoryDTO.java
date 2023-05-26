package com.example.DevOpsProj.dto.responseDto;

import com.example.DevOpsProj.model.Project;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class RepositoryDTO {
    private String name;
    private String description;
}
