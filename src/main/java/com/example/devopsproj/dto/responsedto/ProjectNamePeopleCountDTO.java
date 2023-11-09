package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Data Transfer Object (DTO) for project names and their associated people count.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProjectNamePeopleCountDTO {
    /**
     * The unique identifier of the project (positive number).
     */
    @Positive(message = "projectId should be a positive number")
    private Long projectId;

    /**
     * The name of the project.
     */
    private String projectName;

    /**
     * The count of people associated with the project (positive integer).
     */
    @Positive(message = "countPeople should be a positive integer")
    private Integer countPeople;
}
