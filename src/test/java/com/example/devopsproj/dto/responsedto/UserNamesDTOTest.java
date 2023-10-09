package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserNamesDTOTest {

    @Test
    void testGetAndSetUsername() {
        // Arrange
        String expectedUsername = "john_doe";
        UserNamesDTO userNamesDTO = new UserNamesDTO();

        // Act
        userNamesDTO.setUsername(expectedUsername);
        String actualUsername = userNamesDTO.getUsername();

        // Assert
        assertEquals(expectedUsername, actualUsername);
    }

    @Test
    void testGetAndSetUser() {
        // Arrange
        User expectedUser = new User();
        UserNamesDTO userNamesDTO = new UserNamesDTO();

        // Act
        userNamesDTO.setUser(expectedUser);
        User actualUser = userNamesDTO.getUser();

        // Assert
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void testGetAndSetAccessToken() {
        // Arrange
        String expectedAccessToken = "sampleAccessToken";
        UserNamesDTO userNamesDTO = new UserNamesDTO();

        // Act
        userNamesDTO.setAccessToken(expectedAccessToken);
        String actualAccessToken = userNamesDTO.getAccessToken();

        // Assert
        assertEquals(expectedAccessToken, actualAccessToken);
    }

    @Test
    void testNullUsername() {
        // Arrange
        UserNamesDTO userNamesDTO = new UserNamesDTO();

        // Act
        userNamesDTO.setUsername(null);

        // Assert
        assertNull(userNamesDTO.getUsername());
    }
    @Test
    void testNoArgsConstructor() {
        UserNamesDTO dto = new UserNamesDTO();

        assertNull(dto.getUsername());
        assertNull(dto.getUser());
        assertNull(dto.getAccessToken());
    }

}
