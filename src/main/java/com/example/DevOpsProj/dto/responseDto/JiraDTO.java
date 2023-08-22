package com.example.DevOpsProj.dto.responseDto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class JiraDTO {
    private String name;
    private String key;
    private String projectTypeKey;
    private String leadAccountId;
}