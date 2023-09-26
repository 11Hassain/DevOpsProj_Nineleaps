package com.example.devopsproj.Mapper;

import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.model.User;

public class UserMapper {
    public static UserDTO mapUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        return userDTO;
    }
}

