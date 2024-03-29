package com.example.DevOpsProj.dto.responseDto;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private EnumRole enumRole;
    private String token;

    public UserDTO(Long id, String name, String email, EnumRole enumRole) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.enumRole = enumRole;
    }
}
