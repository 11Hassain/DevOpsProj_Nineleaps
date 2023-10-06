package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectWithUsersDTOTest {

    @Test
    void testNoArgsConstructor() {
        ProjectWithUsersDTO dto = new ProjectWithUsersDTO();

        assertNull(dto.getProjectId());
        assertNull(dto.getProjectName());
        assertNull(dto.getProjectDescription());
        assertNull(dto.getLastUpdated());
        assertNull(dto.getUsers());
        assertNull(dto.getRepositories());
        assertFalse(dto.isStatus());
        assertNull(dto.getFigma());
        assertNull(dto.getGoogleDrive());
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
    void testSetterGetter() {
        ProjectWithUsersDTO dto = new ProjectWithUsersDTO();
        Long projectId = 1L;
        String projectName = "Sample Project";
        String projectDescription = "Description";
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
}
