package com.example.DevOpsProj.Mapper;

import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.model.User;

public class UserMapper {
    public static UserDTO mapUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        return userDTO;
    }
}

