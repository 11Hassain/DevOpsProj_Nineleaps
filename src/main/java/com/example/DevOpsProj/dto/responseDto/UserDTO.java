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
}
