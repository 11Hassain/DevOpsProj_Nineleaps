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
    void testAllArgsConstructorWithUsers() {
        Long projectId = 1L;
        String projectName = "Sample Project";
        String projectDescription = "Description";
        LocalDateTime lastUpdated = LocalDateTime.now();
        List<UserDTO> users = new ArrayList<>();

        ProjectWithUsersDTO dto = new ProjectWithUsersDTO(projectId, projectName, projectDescription, lastUpdated, users);

        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertEquals(lastUpdated, dto.getLastUpdated());
        assertEquals(users, dto.getUsers());
        assertNull(dto.getRepositories());
        assertFalse(dto.isStatus());
        assertNull(dto.getFigma());
        assertNull(dto.getGoogleDrive());
    }
    @Test
    void testAllArgsConstructorWithoutUsers() {
        Long projectId = 1L;
        String projectName = "Sample Project";
        String projectDescription = "Description";

        ProjectWithUsersDTO dto = new ProjectWithUsersDTO(projectId, projectName, projectDescription);

        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertNull(dto.getLastUpdated());
        assertNull(dto.getUsers());
        assertNull(dto.getRepositories());
        assertFalse(dto.isStatus());
        assertNull(dto.getFigma());
        assertNull(dto.getGoogleDrive());
    }


    @Test
    void testSettersAndGetters() {
        ProjectWithUsersDTO dto = new ProjectWithUsersDTO();
        Long projectId = 3L;
        String projectName = "Project C";
        String projectDescription = "Description C";
        LocalDateTime lastUpdated = LocalDateTime.now();
        List<UserDTO> users = new ArrayList<>();

        dto.setProjectId(projectId);
        dto.setProjectName(projectName);
        dto.setProjectDescription(projectDescription);
        dto.setLastUpdated(lastUpdated);
        dto.setUsers(users);

        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(projectDescription, dto.getProjectDescription());
        assertEquals(lastUpdated, dto.getLastUpdated());
        assertEquals(users, dto.getUsers());
        assertNull(dto.getRepositories());
        assertFalse(dto.isStatus());
        assertNull(dto.getFigma());
        assertNull(dto.getGoogleDrive());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long projectId = 1L;
        String projectName = "Test Project";
        String projectDescription = "Description of the project";
        LocalDateTime lastUpdated = LocalDateTime.now();
        List<UserDTO> users = List.of(new UserDTO(1L, "User 1"), new UserDTO(2L, "User 2"));

        // Act
        ProjectWithUsersDTO projectDTO = new ProjectWithUsersDTO(projectId, projectName, projectDescription, lastUpdated, users);

        // Assert
        assertEquals(projectId, projectDTO.getProjectId());
        assertEquals(projectName, projectDTO.getProjectName());
        assertEquals(projectDescription, projectDTO.getProjectDescription());
        assertEquals(lastUpdated, projectDTO.getLastUpdated());
        assertEquals(users, projectDTO.getUsers());
    }

    @Test
    void testToString() {
        // Arrange
        Long projectId = 1L;
        String projectName = "Test Project";
        String projectDescription = "Description of the project";

        // Act
        ProjectWithUsersDTO projectDTO = new ProjectWithUsersDTO(projectId, projectName, projectDescription);

        // Assert
        String expectedToString = "ProjectWithUsersDTO(projectId=1, projectName=Test Project, projectDescription=Description of the project, lastUpdated=null, users=null, repositories=null, status=false, figma=null, googleDrive=null)";
        assertEquals(expectedToString, projectDTO.toString());
    }

}
