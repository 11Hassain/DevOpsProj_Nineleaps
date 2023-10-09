package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDTOTest {

    @Test
    void testValidProjectDTO() {
        // Arrange
        Long projectId = 1L;
        String projectName = "ProjectName";
        String projectDescription = "ProjectDescription";
        LocalDateTime lastUpdated = LocalDateTime.now();

        // Act
        ProjectDTO projectDTO = new ProjectDTO(projectId, projectName, projectDescription, lastUpdated);

        // Assert
        assertEquals(projectId, projectDTO.getProjectId());
        assertEquals(projectName, projectDTO.getProjectName());
        assertEquals(projectDescription, projectDTO.getProjectDescription());
        assertEquals(lastUpdated, projectDTO.getLastUpdated());
    }

    @Test
    void testEmptyProjectDTO() {
        // Arrange

        // Act
        ProjectDTO projectDTO = new ProjectDTO();

        // Assert
        assertNull(projectDTO.getProjectId());
        assertNull(projectDTO.getProjectName());
        assertNull(projectDTO.getProjectDescription());
        assertNull(projectDTO.getLastUpdated());
        assertNull(projectDTO.getUsers());
        assertNull(projectDTO.getPmName());
        assertNull(projectDTO.getRepositories());
        assertFalse(projectDTO.isStatus());
        assertNull(projectDTO.getFigma());
        assertNull(projectDTO.getGoogleDrive());
        assertNull(projectDTO.getHelpDocuments());
    }

    @Test
    void testValidProjectDTOWithUsers() {
        // Arrange
        Long projectId = 1L;
        String projectName = "ProjectName";
        String projectDescription = "ProjectDescription";
        LocalDateTime lastUpdated = LocalDateTime.now();
        List<User> users = new ArrayList<>();
        users.add(new User());

        // Act
        ProjectDTO projectDTO = new ProjectDTO(projectId, projectName, projectDescription, lastUpdated, users);

        // Assert
        assertEquals(projectId, projectDTO.getProjectId());
        assertEquals(projectName, projectDTO.getProjectName());
        assertEquals(projectDescription, projectDTO.getProjectDescription());
        assertEquals(lastUpdated, projectDTO.getLastUpdated());
        assertEquals(users, projectDTO.getUsers());
    }

    @Test
    void testValidProjectDTOWithRepositoriesAndFigmaAndGoogleDrive() {
        // Arrange
        Long projectId = 1L;
        String projectName = "ProjectName";
        String projectDescription = "ProjectDescription";
        List<GitRepositoryDTO> repositories = new ArrayList<>();
        repositories.add(new GitRepositoryDTO());
        FigmaDTO figma = new FigmaDTO("https://figma.com");
        GoogleDriveDTO googleDrive = new GoogleDriveDTO("https://drive.com");

        // Act
        ProjectDTO projectDTO = new ProjectDTO(projectId, projectName, projectDescription, repositories, figma, googleDrive);

        // Assert
        assertEquals(projectId, projectDTO.getProjectId());
        assertEquals(projectName, projectDTO.getProjectName());
        assertEquals(projectDescription, projectDTO.getProjectDescription());
        assertEquals(repositories, projectDTO.getRepositories());
        assertEquals(figma, projectDTO.getFigma());
        assertEquals(googleDrive, projectDTO.getGoogleDrive());
    }
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
        assertNull(projectDTO.getRepositories());
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
    @Test
    void testConstructorWithIdAndName() {
        // Arrange
        Long projectId = 1L;
        String projectName = "Project 1";

        // Act
        ProjectDTO projectDTO = new ProjectDTO(projectId, projectName);

        // Assert
        assertEquals(projectId, projectDTO.getProjectId());
        assertEquals(projectName, projectDTO.getProjectName());
        assertNull(projectDTO.getProjectDescription()); // Assuming it should be null by default
        assertNull(projectDTO.getLastUpdated()); // Assuming it should be null by default
        assertNull(projectDTO.getUsers()); // Assuming it should be null by default
        assertNull(projectDTO.getPmName()); // Assuming it should be null by default
        assertNull(projectDTO.getRepositories()); // Assuming it should be null by default
        assertFalse(projectDTO.isStatus()); // Assuming it should be false by default
        assertNull(projectDTO.getFigma()); // Assuming it should be null by default
        assertNull(projectDTO.getGoogleDrive()); // Assuming it should be null by default
        assertNull(projectDTO.getHelpDocuments()); // Assuming it should be null by default
    }
}
