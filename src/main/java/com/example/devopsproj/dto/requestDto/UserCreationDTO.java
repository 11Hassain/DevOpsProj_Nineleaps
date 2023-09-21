package com.example.devopsproj.dto.requestDto;

import com.example.devopsproj.commons.enumerations.EnumRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreationDTO {

    @Positive(message = "id should be a positive number")
    @NotNull(message = "id cannot be null")
    private Long id;

    @Size(max = 50, message = "projectName should not exceed 50 characters")
    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be black")
    private String name;

    @NotNull(message = "email cannot be null")
    @NotBlank(message = "email cannot be blank")
    private String email;

    private EnumRole enumRole;
}
