package com.example.devopsproj.dto.responseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProjectNamePeopleCountDTO {

    private Long projectId;
    private String projectName;
    private Integer countPeople;
}
