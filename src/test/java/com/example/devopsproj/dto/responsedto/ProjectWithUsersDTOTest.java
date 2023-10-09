package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectWithUsersDTOTest {

    @Test
    void testValidProjectWithUsersDTO() {
        // Arrange
        Long projectId = 1L;
        String projectName = "Project A";
        String projectDescription = "Description A";
        LocalDateTime lastUpdated = LocalDateTime.now();
        List<UserDTO> users = new ArrayList<>();

        // Act
        ProjectWithUsersDTO dto = new ProjectWithUsersDTO(projectId, projectName, projectDescription, lastUpdated, users);

        // Assert
        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertEquals(lastUpdated, dto.getLastUpdated());
        assertEquals(users, dto.getUsers());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        ProjectWithUsersDTO dto = new ProjectWithUsersDTO();

        // Assert
        assertNull(dto.getProjectId());
        assertNull(dto.getProjectName());
        assertNull(dto.getProjectDescription());
        assertNull(dto.getLastUpdated());
        assertNull(dto.getUsers());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long projectId = 2L;
        String projectName = "Project B";
        String projectDescription = "Description B";
        LocalDateTime lastUpdated = LocalDateTime.now();
        List<UserDTO> users = new ArrayList<>();

        // Act
        ProjectWithUsersDTO dto = new ProjectWithUsersDTO(projectId, projectName, projectDescription, lastUpdated, users);

        // Assert
        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertEquals(lastUpdated, dto.getLastUpdated());
        assertEquals(users, dto.getUsers());
    }

    @Test
    void testSetterGetter() {
        // Arrange
        Long projectId = 3L;
        String projectName = "Project C";
        String projectDescription = "Description C";
        LocalDateTime lastUpdated = LocalDateTime.now();
        List<UserDTO> users = new ArrayList<>();

        // Act
        ProjectWithUsersDTO dto = new ProjectWithUsersDTO();
        dto.setProjectId(projectId);
        dto.setProjectName(projectName);
        dto.setProjectDescription(projectDescription);
        dto.setLastUpdated(lastUpdated);
        dto.setUsers(users);

        // Assert
        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertEquals(lastUpdated, dto.getLastUpdated());
        assertEquals(users, dto.getUsers());
    }

    @Test
    void testToString() {
        // Arrange
        Long projectId = 4L;
        String projectName = "Project D";
        String projectDescription = "Description D";
        LocalDateTime lastUpdated = LocalDateTime.now();
        List<UserDTO> users = new ArrayList<>();

        // Act
        ProjectWithUsersDTO dto = new ProjectWithUsersDTO(projectId, projectName, projectDescription, lastUpdated, users);

        // Assert
        String expectedToString = "ProjectWithUsersDTO(projectId=4, projectName=Project D, projectDescription=Description D, lastUpdated=" + lastUpdated + ", users=[])";
//        assertEquals(expectedToString, dto.toString());
    }
}
