package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.commons.enumerations.EnumRole;
import lombok.*;


/**
 * Data Transfer Object (DTO) for creating a user.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class UserCreationDTO {
    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The role of the user, such as SUPER_ADMIN, ADMIN, PROJECT_MANAGER, or USER.
     */
    private EnumRole enumRole;
}
