package com.example.devopsproj.controller;

import aj.org.objectweb.asm.TypeReference;
import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.controller.ProjectController;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.service.interfaces.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectService projectService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateProjectWithValidInput() throws Exception {
        // Create a valid ProjectDTO
        ProjectDTO validProjectDTO = new ProjectDTO();
        validProjectDTO.setProjectName("Valid Project");

        // Mock the projectService to return a created project
        when(projectService.createProject(any(ProjectDTO.class))).thenReturn(validProjectDTO);

        // Perform a POST request to the create endpoint
        ResponseEntity<Object> responseEntity = projectController.createProject(validProjectDTO);

        // Assert the response status code
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // Assert the response body
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());
        String expectedResponseBody = objectMapper.writeValueAsString(validProjectDTO);
        assertEquals(expectedResponseBody, responseBody);
    }

    @Test
    public void testGetProjectByIdWithValidId() throws Exception {
        // Create a valid ProjectDTO and a corresponding ID
        ProjectDTO validProjectDTO = new ProjectDTO();
        validProjectDTO.setProjectId(1L);
        validProjectDTO.setProjectName("Valid Project");

        // Mock the projectService to return the valid project when given a valid ID
        when(projectService.getProjectById(1L)).thenReturn(validProjectDTO);

        // Perform a GET request to the endpoint with the valid ID
        ResponseEntity<Object> responseEntity = projectController.getProjectById(1L);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body
        String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());
        String expectedResponseBody = objectMapper.writeValueAsString(validProjectDTO);
        assertEquals(expectedResponseBody, responseBody);
    }

    @Test
    public void testGetProjectByIdWithInvalidId() {
        // Mock the projectService to return null when given an invalid ID
        when(projectService.getProjectById(2L)).thenReturn(null);

        // Perform a GET request to the endpoint with an invalid ID
        ResponseEntity<Object> responseEntity = projectController.getProjectById(2L);

        // Assert the response status code
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        // Assert the response body (should be empty)
        assertEquals(null, responseEntity.getBody());
    }
    @Test
    public void testGetAllProjects() {
        // Create a list of ProjectDTO objects
        List<ProjectDTO> projectDTOs = new ArrayList<>();
        projectDTOs.add(new ProjectDTO(1L, "Project 1", "Description 1", null, false));
        projectDTOs.add(new ProjectDTO(2L, "Project 2", "Description 2", null, false));

        // Mock the projectService to return the list of projects
        when(projectService.getAll()).thenReturn(projectDTOs);

        // Call the getAll method
        ResponseEntity<Object> responseEntity = projectController.getAll();

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body
        List<ProjectDTO> responseProjects = (List<ProjectDTO>) responseEntity.getBody();
        assertEquals(projectDTOs.size(), responseProjects.size());
        for (int i = 0; i < projectDTOs.size(); i++) {
            assertEquals(projectDTOs.get(i), responseProjects.get(i));
        }
    }

    @Test
    public void testGetAllProjectsWithUsers() {
        // Create a list of ProjectWithUsersDTO objects
        List<ProjectWithUsersDTO> projectsWithUsers = new ArrayList<>();
        // Using the constructor with projectId, projectName, projectDescription, lastUpdated, and users
        ProjectWithUsersDTO project1 = new ProjectWithUsersDTO(1L, "Project 1", "Description 1", LocalDateTime.now(), new ArrayList<>());

        // Using the constructor with projectId, projectName, and projectDescription
        ProjectWithUsersDTO project2 = new ProjectWithUsersDTO(2L, "Project 2", "Description 2");

        // Mock the projectService to return the list of projects with users
        when(projectService.getAllProjectsWithUsers()).thenReturn(projectsWithUsers);

        // Call the getAllProjectsWithUsers method
        ResponseEntity<Object> responseEntity = projectController.getAllProjectsWithUsers();

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body
        List<ProjectWithUsersDTO> responseProjects = (List<ProjectWithUsersDTO>) responseEntity.getBody();
        assertEquals(projectsWithUsers.size(), responseProjects.size());
        for (int i = 0; i < projectsWithUsers.size(); i++) {
            assertEquals(projectsWithUsers.get(i), responseProjects.get(i));
        }
    }

    @Test
    public void testGetAllUsersByProjectId() {
        // Define a projectId for testing
        Long projectId = 1L;

        // Create a list of UserDTO objects
        List<UserDTO> userDTOs = new ArrayList<>();
        userDTOs.add(new UserDTO(1L, "User 1", "user1@example.com"));
        userDTOs.add(new UserDTO(2L, "User 2", "user2@example.com"));

        // Mock the projectService to return the list of users by projectId
        when(projectService.getAllUsersByProjectId(projectId)).thenReturn(userDTOs);

        // Call the getAllUsersByProjectId method
        ResponseEntity<Object> responseEntity = projectController.getAllUsersByProjectId(projectId);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body
        List<UserDTO> responseUsers = (List<UserDTO>) responseEntity.getBody();
        assertEquals(userDTOs.size(), responseUsers.size());
        for (int i = 0; i < userDTOs.size(); i++) {
            assertEquals(userDTOs.get(i), responseUsers.get(i));
        }
    }

    @Test
    public void testGetAllUsersByProjectIdByRole() {
        // Define a projectId and role for testing
        Long projectId = 1L;
        String role = "ADMIN"; // Example role

        // Create a list of UserDTO objects
        List<UserDTO> userDTOs = new ArrayList<>();
        userDTOs.add(new UserDTO(1L, "User 1", "user1@example.com", EnumRole.ADMIN));
        userDTOs.add(new UserDTO(2L, "User 2", "user2@example.com", EnumRole.ADMIN));

        // Mock the projectService to return the list of users by projectId and role
        when(projectService.getAllUsersByProjectIdAndRole(projectId, EnumRole.ADMIN)).thenReturn(userDTOs);

        // Call the getAllUsersByProjectIdByRole method
        ResponseEntity<Object> responseEntity = projectController.getAllUsersByProjectIdByRole(projectId, role);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body
        List<UserDTO> responseUsers = (List<UserDTO>) responseEntity.getBody();
        assertEquals(userDTOs.size(), responseUsers.size());
        for (int i = 0; i < userDTOs.size(); i++) {
            assertEquals(userDTOs.get(i), responseUsers.get(i));
        }
    }
    @Test
    public void testUpdateProject() {
        // Define a projectId for testing
        Long projectId = 1L;

        // Create a ProjectDTO object for the update
        ProjectDTO updatedProjectDTO = new ProjectDTO(projectId, "Updated Project", "Updated Description", null, false);

        // Mock the projectService to return the updated project
        when(projectService.updateProject(projectId, updatedProjectDTO)).thenReturn(updatedProjectDTO);

        // Call the updateProject method
        ResponseEntity<ProjectDTO> responseEntity = projectController.updateProject(projectId, updatedProjectDTO);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body
        ProjectDTO responseProject = responseEntity.getBody();
        assertEquals(updatedProjectDTO, responseProject);
    }

    @Test
    public void testDeleteProject() {
        // Define a projectId for testing
        Long projectId = 1L;

        // Mock the projectService to return a success response
        ResponseEntity<String> successResponse = new ResponseEntity<>("Project deleted successfully", HttpStatus.OK);
        when(projectService.deleteProject(projectId)).thenReturn(successResponse);

        // Call the deleteProject method
        ResponseEntity<String> responseEntity = projectController.deleteProject(projectId);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body message
        String responseBody = responseEntity.getBody();
        assertEquals("Project deleted successfully", responseBody);
    }

    @Test
    public void testAddUserToProject() {
        // Define a projectId and userId for testing
        Long projectId = 1L;
        Long userId = 2L;

        // Mock the projectService to return a success response
        ResponseEntity<Object> successResponse = new ResponseEntity<>("User added to project successfully", HttpStatus.OK);
        when(projectService.addUserToProject(projectId, userId)).thenReturn(successResponse);

        // Call the addUserToProject method
        ResponseEntity<Object> responseEntity = projectController.addUserToProject(projectId, userId);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body message
        String responseBody = (String) responseEntity.getBody();
        assertEquals("User added to project successfully", responseBody);
    }
    @Test
    void testGetProjectsWithoutFigmaURL() {
        // Arrange
        List<ProjectDTO> projectsWithoutFigmaURL = new ArrayList<>(); // You should set the projects here
        ResponseEntity<List<ProjectDTO>> responseEntity = ResponseEntity.ok(projectsWithoutFigmaURL);

        when(projectService.getProjectsWithoutFigmaURL()).thenReturn(projectsWithoutFigmaURL);

        // Act
        ResponseEntity<List<ProjectDTO>> response = projectController.getProjectsWithoutFigmaURL();

        // Assert
        verify(projectService, times(1)).getProjectsWithoutFigmaURL();
        assertEquals(responseEntity, response);
    }

    @Test
    void testGetProjectsWithoutGoogleDriveLink() {
        // Arrange
        List<ProjectDTO> projectsWithoutGoogleDriveLink = new ArrayList<>(); // You should set the projects here
        ResponseEntity<List<ProjectDTO>> responseEntity = ResponseEntity.ok(projectsWithoutGoogleDriveLink);

        when(projectService.getProjectsWithoutGoogleDriveLink()).thenReturn(projectsWithoutGoogleDriveLink);

        // Act
        ResponseEntity<List<ProjectDTO>> response = projectController.getProjectsWithoutGoogleDriveLink();

        // Assert
        verify(projectService, times(1)).getProjectsWithoutGoogleDriveLink();
        assertEquals(responseEntity, response);
    }
    @Test
    public void testRemoveUserFromProject() {
        // Define a projectId and userId for testing
        Long projectId = 1L;
        Long userId = 2L;

        // Mock the projectService to return a success response
        ResponseEntity<String> successResponse = new ResponseEntity<>("User removed from project successfully", HttpStatus.OK);
        when(projectService.removeUserFromProject(projectId, userId)).thenReturn(successResponse);

        // Call the removeUserFromProject method
        ResponseEntity<String> responseEntity = projectController.removeUserFromProject(projectId, userId);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body message
        String responseBody = responseEntity.getBody();
        assertEquals("User removed from project successfully", responseBody);
    }

    @Test
    public void testRemoveUserFromProjectAndRepo() {
        // Define a projectId, userId, and CollaboratorDTO for testing
        Long projectId = 1L;
        Long userId = 2L;
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO("owner-name", "repository-name", "username", "access-token");

        // Mock the projectService to return a success response
        ResponseEntity<String> successResponse = new ResponseEntity<>("User removed from project and repository successfully", HttpStatus.OK);
        when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO)).thenReturn(successResponse);

        // Call the removeUserFromProjectAndRepo method
        ResponseEntity<String> responseEntity = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body message
        String responseBody = responseEntity.getBody();
        assertEquals("User removed from project and repository successfully", responseBody);
    }
    @Test
    public void testGetUsersByProjectIdAndRole() {
        // Define a projectId and role for testing
        Long projectId = 1L;
        String role = "ADMIN"; // Example role

        // Create a list of UserDTO objects
        List<UserDTO> userDTOs = new ArrayList<>();
        userDTOs.add(new UserDTO(1L, "User 1", "user1@example.com", EnumRole.ADMIN));
        userDTOs.add(new UserDTO(2L, "User 2", "user2@example.com", EnumRole.ADMIN));

        // Mock the projectService to return the list of users by projectId and role
        when(projectService.getUsersByProjectIdAndRole(projectId, role)).thenReturn(userDTOs);

        // Call the getUsersByProjectIdAndRole method
        ResponseEntity<List<UserDTO>> responseEntity = projectController.getUsersByProjectIdAndRole(projectId, role);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body
        List<UserDTO> responseUsers = responseEntity.getBody();
        assertEquals(userDTOs.size(), responseUsers.size());
        for (int i = 0; i < userDTOs.size(); i++) {
            assertEquals(userDTOs.get(i), responseUsers.get(i));
        }
    }

    @Test
    public void testAddRepositoryToProject() {
        // Define a projectId and repoId for testing
        Long projectId = 1L;
        Long repoId = 2L;

        // Mock the projectService to return a success response
        ResponseEntity<Object> successResponse = new ResponseEntity<>("Repository added to project successfully", HttpStatus.OK);
        when(projectService.addRepositoryToProject(projectId, repoId)).thenReturn(successResponse);

        // Call the addRepositoryToProject method
        ResponseEntity<Object> responseEntity = projectController.addRepositoryToProject(projectId, repoId);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body message
        String responseBody = (String) responseEntity.getBody();
        assertEquals("Repository added to project successfully", responseBody);
    }

    @Test
    public void testCountAllPeopleByProjectIdAndName() {
        // Create a list of ProjectNamePeopleCountDTO objects with some sample data
        List<ProjectNamePeopleCountDTO> peopleCountDTOs = new ArrayList<>();
        ProjectNamePeopleCountDTO peopleCountDTO = new ProjectNamePeopleCountDTO(1L, "Project 1", 5);
        peopleCountDTOs.add(new ProjectNamePeopleCountDTO(1L, "Project 2", 3));


        // Mock the projectService to return the sample data
        when(projectService.getCountAllPeopleAndProjectName()).thenReturn(peopleCountDTOs);

        // Call the countAllPeopleByProjectIdAndName method
        ResponseEntity<List<ProjectNamePeopleCountDTO>> responseEntity = projectController.countAllPeopleByProjectIdAndName();

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body
        List<ProjectNamePeopleCountDTO> responseDTOs = responseEntity.getBody();
        assertEquals(peopleCountDTOs.size(), responseDTOs.size());
        for (int i = 0; i < peopleCountDTOs.size(); i++) {
            assertEquals(peopleCountDTOs.get(i), responseDTOs.get(i));
        }
    }
    @Test
    public void testCountAllProjects() {
        // Mock the projectService to return a count of projects (e.g., 5)
        when(projectService.getCountAllProjects()).thenReturn(5);

        // Call the countAllProjects method
        ResponseEntity<Object> responseEntity = projectController.countAllProjects();

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body (count of projects)
        Object responseBody = responseEntity.getBody();
        assertEquals(5, responseBody);
    }
    @Test
    public void testCountAllProjectsByRole() {
        // Mock role parameter and project count
        String role = "ADMIN"; // Replace with the role you want to test
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        int projectCount = 5;

        // Mock the projectService to return the count of projects by role
        when(projectService.getCountAllProjectsByRole(enumRole)).thenReturn(projectCount);

        // Call the countAllProjectsByRole method
        ResponseEntity<Object> responseEntity = projectController.countAllProjectsByRole(role);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body (count of projects)
        Object responseBody = responseEntity.getBody();
        assertEquals(projectCount, responseBody);
    }

    @Test
    public void testCountAllProjectsByUserId() {
        // Mock user ID parameter and project count
        Long userId = 123L; // Replace with the user ID you want to test
        int projectCount = 8; // Replace with the expected project count

        // Mock the projectService to return the count of projects by user ID
        when(projectService.getCountAllProjectsByUserId(userId)).thenReturn(projectCount);

        // Call the countAllProjectsByUserId method
        ResponseEntity<Object> responseEntity = projectController.countAllProjectsByUserId(userId);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body (count of projects)
        Object responseBody = responseEntity.getBody();
        assertEquals(projectCount, responseBody);
    }

    @Test
    public void testCountAllUsersByProjectId() {
        // Mock project ID parameter and user count
        Long projectId = 456L; // Replace with the project ID you want to test
        int userCount = 10; // Replace with the expected user count

        // Mock the projectService to return the count of users by project ID
        when(projectService.getCountAllUsersByProjectId(projectId)).thenReturn(userCount);

        // Call the countAllUsersByProjectId method
        ResponseEntity<Object> responseEntity = projectController.countAllUsersByProjectId(projectId);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body (count of users)
        Object responseBody = responseEntity.getBody();
        assertEquals(userCount, responseBody);
    }
    @Test
    public void testCountAllUsersByProjectIdByRole() {
        // Mock project ID and role parameters
        Long projectId = 789L; // Replace with the project ID you want to test
        String role = "ADMIN"; // Replace with the role you want to test
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        int userCount = 15; // Replace with the expected user count

        // Mock the projectService to return the count of users by project ID and role
        when(projectService.getCountAllUsersByProjectIdAndRole(projectId, enumRole)).thenReturn(userCount);

        // Call the countAllUsersByProjectIdByRole method
        ResponseEntity<Object> responseEntity = projectController.countAllUsersByProjectIdByRole(projectId, role);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body (count of users)
        Object responseBody = responseEntity.getBody();
        assertEquals(userCount, responseBody);
    }

    @Test
    public void testCountAllActiveProjects() {
        // Mock the projectService to return a count of active projects (e.g., 7)
        when(projectService.getCountAllActiveProjects()).thenReturn(7);

        // Call the countAllActiveProjects method
        ResponseEntity<Object> responseEntity = projectController.countAllActiveProjects();

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body (count of active projects)
        Object responseBody = responseEntity.getBody();
        assertEquals(7, responseBody);
    }

    @Test
    public void testCountAllInActiveProjects() {
        // Mock the projectService to return a count of inactive projects (e.g., 3)
        when(projectService.getCountAllInActiveProjects()).thenReturn(3);

        // Call the countAllInActiveProjects method
        ResponseEntity<Object> responseEntity = projectController.countAllInActiveProjects();

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body (count of inactive projects)
        Object responseBody = responseEntity.getBody();
        assertEquals(3, responseBody);
    }

    @Test
    public void testCountAllInActiveProjectsNoData() {
        // Mock the projectService to return 0 when there are no inactive projects
        when(projectService.getCountAllInActiveProjects()).thenReturn(0);

        // Call the countAllInActiveProjects method
        ResponseEntity<Object> responseEntity = projectController.countAllInActiveProjects();

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body (count of inactive projects)
        Object responseBody = responseEntity.getBody();
        assertEquals(0, responseBody);
    }


    @Test
    public void testGetProjectDetailsById() {
        // Mock the projectService to return a ProjectDTO when given a valid projectId
        Long projectId = 1L;
        ProjectDTO expectedProjectDetails = new ProjectDTO(/* Initialize with expected data */);
        when(projectService.getProjectDetailsById(projectId)).thenReturn(expectedProjectDetails);

        // Call the getProjectDetailsById method
        ResponseEntity<Object> responseEntity = projectController.getProjectDetailsById(projectId);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body (ProjectDTO)
        Object responseBody = responseEntity.getBody();
        assertEquals(expectedProjectDetails, responseBody);
    }

    @Test
    public void testGetProjectDetailsByIdInvalidId() {
        // Mock the projectService to return an empty optional when given an invalid projectId (e.g., project with ID 2 does not exist)
        Long invalidProjectId = 2L;
        when(projectService.getProjectDetailsById(invalidProjectId)).thenReturn(null);

        // Call the getProjectDetailsById method with an invalid projectId
        ResponseEntity<Object> responseEntity = projectController.getProjectDetailsById(invalidProjectId);

        // Assert the response status code (should be 404 NOT_FOUND)
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
