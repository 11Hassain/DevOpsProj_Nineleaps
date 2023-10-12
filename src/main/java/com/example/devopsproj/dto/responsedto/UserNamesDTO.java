package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.model.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * The UserNamesDTO class represents a data transfer object for usernames. It includes the username, associated user,
 * and an access token.
 *
 * @version 2.0
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserNamesDTO {

    @NotNull(message = "username cannot be null")
    @Size(max = 100, message = "username should not exceed 100 characters")
    private String username;

    private User user;
    private String accessToken;
}