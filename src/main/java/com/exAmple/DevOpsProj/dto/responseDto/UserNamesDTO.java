package com.exAmple.DevOpsProj.dto.responseDto;

import com.exAmple.DevOpsProj.model.User;
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