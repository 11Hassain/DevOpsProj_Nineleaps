package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * The ProjectNamePeopleCountDTO class represents a data transfer object for project information.
 * It includes details such as project ID, project name, and the count of people associated with the project.
 *
 * @version 2.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectNamePeopleCountDTO {

    @Positive(message = "projectId should be a positive number")
    private Long projectId;

    private String projectName;

    @Positive(message = "countPeople should be a positive integer")
    private Integer countPeople;
}
