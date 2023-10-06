package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserProjectsDTOTest {

    @Test
    void testNoArgsConstructor() {
        UserProjectsDTO dto = new UserProjectsDTO();

        assertNull(dto.getUserId());
        assertNull(dto.getUserName());
        assertNull(dto.getProjectNames());
    }

    @Test
    void testAllArgsConstructor() {
        Long userId = 1L;
        String userName = "John";
        List<String> projectNames = new ArrayList<>();

        UserProjectsDTO dto = new UserProjectsDTO(userId, userName, projectNames);

        assertEquals(userId, dto.getUserId());
        assertEquals(userName, dto.getUserName());
        assertEquals(projectNames, dto.getProjectNames());
    }

    @Test
    void testSetterGetter() {
        UserProjectsDTO dto = new UserProjectsDTO();
        Long userId = 2L;
        String userName = "Alice";
        List<String> projectNames = new ArrayList<>();

        dto.setUserId(userId);
        dto.setUserName(userName);
        dto.setProjectNames(projectNames);

        assertEquals(userId, dto.getUserId());
        assertEquals(userName, dto.getUserName());
        assertEquals(projectNames, dto.getProjectNames());
    }
}
