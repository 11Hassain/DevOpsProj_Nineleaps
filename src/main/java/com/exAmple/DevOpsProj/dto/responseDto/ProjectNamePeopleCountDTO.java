package com.exAmple.DevOpsProj.dto.responseDto;

import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProjectNamePeopleCountDTO {

    @Positive(message = "projectId should be a positive number")
    private Long projectId;

    private String projectName;

    @Positive(message = "countPeople should be a positive integer")
    private Integer countPeople;
}
