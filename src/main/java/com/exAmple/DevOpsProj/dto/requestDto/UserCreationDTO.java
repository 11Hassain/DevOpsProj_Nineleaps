package com.exAmple.DevOpsProj.dto.requestDto;

import com.exAmple.DevOpsProj.commons.enumerations.EnumRole;
import lombok.Data;

@Data
public class UserCreationDTO {
    private Long id;
    private String name;
    private String email;
    private EnumRole enumRole;
}
