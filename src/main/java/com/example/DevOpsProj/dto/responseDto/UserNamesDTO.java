package com.example.DevOpsProj.dto.responseDto;

import com.example.DevOpsProj.model.User;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class UserNamesDTO {
    private String username;
    private User user;
    private String accessToken;
}