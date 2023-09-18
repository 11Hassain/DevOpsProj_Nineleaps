package com.example.DevOpsProj.dto.requestDto;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import lombok.Data;

@Data
public class UserCreationDTO {
    private Long id;
    private String name;
    private String email;
    private EnumRole enumRole;
}
