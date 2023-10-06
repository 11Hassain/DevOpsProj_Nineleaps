package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectUserDTOTest {

    @Test
    void testAllArgsConstructor() {
        Long projectId = 1L;
        String projectName = "Project 1";
        String projectDescription = "Description";
        List<UserDTO> users = new ArrayList<>();

        ProjectUserDTO dto = new ProjectUserDTO(projectId, projectName, projectDescription, users);

        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertEquals(users, dto.getUsers());
    }

    @Test
    void testNoArgsConstructor() {
        ProjectUserDTO dto = new ProjectUserDTO();

        assertNull(dto.getProjectId());
        assertNull(dto.getProjectName());
        assertNull(dto.getProjectDescription());
        assertNull(dto.getUsers());
    }

    @Test
    void testSetterGetter() {
        ProjectUserDTO dto = new ProjectUserDTO();
        Long projectId = 1L;
        String projectName = "Project 1";
        String projectDescription = "Description";
        List<UserDTO> users = new ArrayList<>();

        dto.setProjectId(projectId);
        dto.setProjectName(projectName);
        dto.setProjectDescription(projectDescription);
        dto.setUsers(users);

        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertEquals(users, dto.getUsers());
    }
}
