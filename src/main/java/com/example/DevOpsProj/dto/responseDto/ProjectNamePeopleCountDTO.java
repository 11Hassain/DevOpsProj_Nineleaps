package com.example.DevOpsProj.dto.responseDto;

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
