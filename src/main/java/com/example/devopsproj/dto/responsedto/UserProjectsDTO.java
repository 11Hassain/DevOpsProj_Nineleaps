package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * The UserProjectsDTO class represents a data transfer object for user projects. It includes the user's ID, name,
 * and a list of project names associated with the user.
 *
 * @version 2.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProjectsDTO {

    @Positive(message = "userId should be a positive number")
    @NotNull(message = "userId cannot be null")
    private Long userId;

    @Size(max = 100, message = "userName cannot exceed 100 characters")
    private String userName;

    private List<String> projectNames;
}
