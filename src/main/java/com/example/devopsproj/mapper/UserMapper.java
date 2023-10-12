package com.example.devopsproj.mapper;

import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.model.User;

public class UserMapper {
    // Private constructor to hide the implicit public one
    private UserMapper() {
        // This constructor is empty as the class is intended to be used as a utility class.
        // Adding a private constructor prevents the creation of instances of this class.
    }

    public static UserDTO mapUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        return userDTO;
    }
}


