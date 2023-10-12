package com.example.devopsproj.mapper;

import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    @Test
    void testMapUserToUserDTO() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        // Act
        UserDTO userDTO = UserMapper.mapUserToUserDTO(user);

        // Assert
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getName(), userDTO.getName());
    }
}
