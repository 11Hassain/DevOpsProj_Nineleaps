package com.example.devopsproj.model;

import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.ProjectRepository;
import org.hibernate.annotations.Cascade;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectTest {

    @Mock
    private ProjectRepository projectRepository;
    @Test
    public void testAllArgsConstructor() {
        Long projectId = 1L;
        String projectName = "Project1";
        String projectDescription = "Description";
        LocalDateTime lastUpdated = LocalDateTime.now();
        boolean deleted = false;
        List<GitRepository> repositories = new ArrayList<>();
        List<AccessRequest> accessRequests = new ArrayList<>();
        List<User> users = new ArrayList<>();
        Figma figma = new Figma();
        GoogleDrive googleDrive = new GoogleDrive();

        Project project = new Project(
                projectId, projectName, projectDescription, lastUpdated, deleted);
        project.setRepositories(repositories);
        project.setAccessRequest(accessRequests);
        project.setUsers(users);
        project.setFigma(figma);
        project.setGoogleDrive(googleDrive);

        assertEquals(projectId, project.getProjectId());
        assertEquals(projectName, project.getProjectName());
        assertEquals(projectDescription, project.getProjectDescription());
        assertEquals(lastUpdated, project.getLastUpdated());
        assertEquals(deleted, project.getDeleted());
        assertEquals(repositories, project.getRepositories());
        assertEquals(accessRequests, project.getAccessRequest());
        assertEquals(users, project.getUsers());
        assertEquals(figma, project.getFigma());
        assertEquals(googleDrive, project.getGoogleDrive());
    }



    @Test
    public void testGetterSetter() {
        Project project = new Project();

        Long projectId = 1L;
        String projectName = "Project1";
        String projectDescription = "Description";
        LocalDateTime lastUpdated = LocalDateTime.now();
        boolean deleted = false;
        List<GitRepository> repositories = new ArrayList<>();
        List<AccessRequest> accessRequests = new ArrayList<>();
        List<User> users = new ArrayList<>();
        Figma figma = new Figma();
        GoogleDrive googleDrive = new GoogleDrive();

        project.setProjectId(projectId);
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setLastUpdated(lastUpdated);
        project.setDeleted(deleted);
        project.setRepositories(repositories);
        project.setAccessRequest(accessRequests);
        project.setUsers(users);
        project.setFigma(figma);
        project.setGoogleDrive(googleDrive);

        assertEquals(projectId, project.getProjectId());
        assertEquals(projectName, project.getProjectName());
        assertEquals(projectDescription, project.getProjectDescription());
        assertEquals(lastUpdated, project.getLastUpdated());
        assertEquals(deleted, project.getDeleted());
        assertEquals(repositories, project.getRepositories());
        assertEquals(accessRequests, project.getAccessRequest());
        assertEquals(users, project.getUsers());
        assertEquals(figma, project.getFigma());
        assertEquals(googleDrive, project.getGoogleDrive());
    }
    @Test
    public void testUsersField() {
        // Arrange
        Project project = new Project();
        List<User> users = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        users.add(user1);
        users.add(user2);

        // Act
        project.setUsers(users);
        List<User> retrievedUsers = project.getUsers();

        // Assert
        assertNotNull(retrievedUsers);
        assertEquals(users, retrievedUsers);
    }

    @Test
    public void testFigmaField() {
        // Arrange
        Project project = new Project();
        Figma figma = mock(Figma.class);

        // Act
        project.setFigma(figma);
        Figma retrievedFigma = project.getFigma();

        // Assert
        assertNotNull(retrievedFigma);
        assertEquals(figma, retrievedFigma);
    }

    @Test
    public void testGoogleDriveField() {
        // Arrange
        Project project = new Project();
        GoogleDrive googleDrive = mock(GoogleDrive.class);

        // Act
        project.setGoogleDrive(googleDrive);
        GoogleDrive retrievedGoogleDrive = project.getGoogleDrive();

        // Assert
        assertNotNull(retrievedGoogleDrive);
        assertEquals(googleDrive, retrievedGoogleDrive);
    }

    @Test
    public void testGetRepositoriesWithNullRepositories() {
        // Arrange
        Project project = new Project();

        // Act
        List<GitRepository> repositories = project.getRepositories();

        // Assert
        assertNotNull(repositories);
        assertTrue(repositories.isEmpty());
    }

    @Test
    public void testGetDeleted() {
        // Arrange
        Project project = new Project();
        boolean deleted = true;

        // Act
        project.setDeleted(deleted);
        Boolean retrievedDeleted = project.getDeleted();

        // Assert
        assertNotNull(retrievedDeleted);
        assertEquals(deleted, retrievedDeleted);
    }

    @Test
    public void testSetProjectName() {
        // Arrange
        Project project = new Project();
        String projectName = "New Project Name";

        // Act
        project.setProjectName(projectName);
        String retrievedProjectName = project.getProjectName();

        // Assert
        assertNotNull(retrievedProjectName);
        assertEquals(projectName, retrievedProjectName);
    }





}
