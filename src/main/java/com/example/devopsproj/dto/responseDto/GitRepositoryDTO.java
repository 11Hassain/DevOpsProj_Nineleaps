package com.example.devopsproj.dto.responseDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class GitRepositoryDTO {

    @Positive(message = "repoId should be a positive number")
    private Long repoId;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;

    private String description;

    public GitRepositoryDTO(String name, String description){
        this.name = name;
        this.description = description;
    }
}
