package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.commons.enumerations.EnumRole;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class UserCreationDTO {
    private Long id;
    private String name;
    private String email;
    private EnumRole enumRole;
}
