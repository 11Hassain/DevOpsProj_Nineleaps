package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectUserDTOTest {

    @Test
    void testValidProjectUserDTO() {
        // Arrange
        Long projectId = 1L;
        String projectName = "Project A";
        String projectDescription = "Description A";
        List<UserDTO> users = new ArrayList<>();

        // Act
        ProjectUserDTO dto = new ProjectUserDTO(projectId, projectName, projectDescription, users);

        // Assert
        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertEquals(users, dto.getUsers());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        ProjectUserDTO dto = new ProjectUserDTO();

        // Assert
        assertNull(dto.getProjectId());
        assertNull(dto.getProjectName());
        assertNull(dto.getProjectDescription());
        assertNull(dto.getUsers());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long projectId = 2L;
        String projectName = "Project B";
        String projectDescription = "Description B";
        List<UserDTO> users = new ArrayList<>();

        // Act
        ProjectUserDTO dto = new ProjectUserDTO(projectId, projectName, projectDescription, users);

        // Assert
        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertEquals(users, dto.getUsers());
    }

    @Test
    void testSetterGetter() {
        // Arrange
        Long projectId = 3L;
        String projectName = "Project C";
        String projectDescription = "Description C";
        List<UserDTO> users = new ArrayList<>();

        // Act
        ProjectUserDTO dto = new ProjectUserDTO();
        dto.setProjectId(projectId);
        dto.setProjectName(projectName);
        dto.setProjectDescription(projectDescription);
        dto.setUsers(users);

        // Assert
        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertEquals(users, dto.getUsers());
    }

    @Test
    void testToString() {
        // Arrange
        Long projectId = 4L;
        String projectName = "Project D";
        String projectDescription = "Description D";
        List<UserDTO> users = new ArrayList<>();

        // Act
        ProjectUserDTO dto = new ProjectUserDTO(projectId, projectName, projectDescription, users);

        // Assert
        String expectedToString = "ProjectUserDTO(projectId=4, projectName=Project D, projectDescription=Description D, users=[])";
        assertEquals(expectedToString, dto.toString());
    }
}
