package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDTOTest {

    @Test
    void testNoArgsConstructor() {
        // Create an instance using the no-args constructor
        ProjectDTO projectDTO = new ProjectDTO();

        // Verify that fields are initialized to default values (null, empty, etc.)
        assertNull(projectDTO.getProjectId());
        assertNull(projectDTO.getProjectName());
        assertNull(projectDTO.getProjectDescription());
        assertNull(projectDTO.getLastUpdated());
        assertNull(projectDTO.getUsers());
        assertNull(projectDTO.getPmName());
        assertTrue(projectDTO.getRepositories().isEmpty());
        assertFalse(projectDTO.isStatus());
        assertNull(projectDTO.getFigma());
        assertNull(projectDTO.getGoogleDrive());
        assertNull(projectDTO.getHelpDocuments());
    }

    @Test
    void testConstructorWithIdNameDescription() {
        Long projectId = 1L;
        String projectName = "Project 1";
        String projectDescription = "Description for Project 1";

        ProjectDTO projectDTO = new ProjectDTO(projectId, projectName, projectDescription);

        assertEquals(projectId, projectDTO.getProjectId());
        assertEquals(projectName, projectDTO.getProjectName());
        assertEquals(projectDescription, projectDTO.getProjectDescription());
        assertNull(projectDTO.getLastUpdated());
        assertNull(projectDTO.getUsers());
    }

    @Test
    void testConstructorWithIdNameDescriptionLastUpdated() {
        Long projectId = 1L;
        String projectName = "Project 1";
        String projectDescription = "Description for Project 1";
        LocalDateTime lastUpdated = LocalDateTime.now();

        ProjectDTO projectDTO = new ProjectDTO(projectId, projectName, projectDescription, lastUpdated);

        assertEquals(projectId, projectDTO.getProjectId());
        assertEquals(projectName, projectDTO.getProjectName());
        assertEquals(projectDescription, projectDTO.getProjectDescription());
        assertEquals(lastUpdated, projectDTO.getLastUpdated());
        assertNull(projectDTO.getUsers());
    }

    @Test
    void testConstructorWithIdNameDescriptionLastUpdatedUsers() {
        Long projectId = 1L;
        String projectName = "Project 1";
        String projectDescription = "Description for Project 1";
        LocalDateTime lastUpdated = LocalDateTime.now();

        User user1 = new User();
        user1.setName("User 1");

        User user2 = new User();
        user2.setName("User 1");

        List<User> users = List.of(user1, user2);

        ProjectDTO projectDTO = new ProjectDTO(projectId, projectName, projectDescription, lastUpdated, users);

        assertEquals(projectId, projectDTO.getProjectId());
        assertEquals(projectName, projectDTO.getProjectName());
        assertEquals(projectDescription, projectDTO.getProjectDescription());
        assertEquals(lastUpdated, projectDTO.getLastUpdated());
        assertEquals(users, projectDTO.getUsers());
    }
}
