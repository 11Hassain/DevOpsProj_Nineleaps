package com.example.devopsproj.dto.responseDto;

import com.example.devopsproj.model.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class UserNamesDTO {

    @NotNull(message = "username cannot be null")
    @Size(max = 100, message = "username should not exceed 100 characters")
    private String username;

    private User user;
    private String accessToken;
}