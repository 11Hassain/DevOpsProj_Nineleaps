package com.example.devopsproj.mapper;

import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.model.User;

public class UserMapper {
    // Private constructor to hide the implicit public one
    private UserMapper() {
    }

    public static UserDTO mapUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        return userDTO;
    }
}


