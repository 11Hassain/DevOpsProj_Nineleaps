package com.example.devopsproj.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProjectsDTO {
    private Long userId;
    private String userName;
    private List<String> projectNames;
}
