package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.commons.enumerations.EnumRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDTO {

    @Positive(message = "id should be a positive number")
    private Long id;

    @Size(max = 50, message = "projectName should not exceed 50 characters")
    @NotBlank(message = "name cannot be black")
    private String name;

    @NotBlank(message = "email cannot be blank")
    private String email;

    private EnumRole enumRole; // Role
}
