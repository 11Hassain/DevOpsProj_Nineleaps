package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNamesDTOTest {

    @Test
    void testNoArgsConstructor() {
        UserNamesDTO dto = new UserNamesDTO();

        assertNull(dto.getUsername());
        assertNull(dto.getUser());
        assertNull(dto.getAccessToken());
    }

    @Test
    void testAllArgsConstructor() {
        String username = "john_doe";
        User user = new User();
        String accessToken = "abc123";

        UserNamesDTO dto = new UserNamesDTO(username, user, accessToken);

        assertEquals(username, dto.getUsername());
        assertEquals(user, dto.getUser());
        assertEquals(accessToken, dto.getAccessToken());
    }

    @Test
    void testSetterGetter() {
        UserNamesDTO dto = new UserNamesDTO();
        String username = "john_doe";
        User user = new User();
        String accessToken = "abc123";

        dto.setUsername(username);
        dto.setUser(user);
        dto.setAccessToken(accessToken);

        assertEquals(username, dto.getUsername());
        assertEquals(user, dto.getUser());
        assertEquals(accessToken, dto.getAccessToken());
    }
}
