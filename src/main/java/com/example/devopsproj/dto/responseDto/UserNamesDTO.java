package com.example.devopsproj.dto.responseDto;

import com.example.devopsproj.model.User;
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