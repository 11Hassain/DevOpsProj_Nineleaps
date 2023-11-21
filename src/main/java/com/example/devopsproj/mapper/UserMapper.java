package com.example.devopsproj.mapper;
/**
 * UserMapper is a utility class with static methods for mapping User objects to UserDTO objects.
 * It contains a private constructor to prevent instantiation as it is intended to be used as a utility class.
 */
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


