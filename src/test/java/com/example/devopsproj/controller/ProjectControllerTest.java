package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.exceptions.ConflictException;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
import com.example.devopsproj.service.implementations.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;
    @Mock
    private ProjectServiceImpl projectService;
    @Mock
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    String INTERNAL_SERVER_ERROR = "Something went wrong";

    // ----- SUCCESS (For VALID TOKEN)-----

    @Test
    void testCreateProject_ValidToken(){
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        userList.add(user1);
        userList.add(user2);

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(1L);
        projectDTO.setProjectName("P1");
        projectDTO.setProjectDescription("P1 description");
        projectDTO.setStatus(true);
        projectDTO.setUsers(userList);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.createProject(projectDTO)).thenReturn(projectDTO);

        ResponseEntity<Object> response = projectController.createProject(projectDTO, "valid-access-token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Retrieve the projectDTO from the response
        ProjectDTO responseProjectDTO = (ProjectDTO) response.getBody();

        // Assert that the projectName is "P1"
        assert responseProjectDTO != null;
        assertEquals("P1", responseProjectDTO.getProjectName());
    }

    @Test
    void testGetProjectById_ValidToken_ProjectFound(){
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("P1");
        projectDTO.setProjectDescription("P1 description");

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getProject(anyLong())).thenReturn(ResponseEntity.ok(projectDTO));

        // Act
        ResponseEntity<Object> response = projectController.getProjectById(1L, "valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDTO, response.getBody());
    }

    @Test
    void testGetProjectById_ValidToken_ProjectNotFound() {
        // Arrange
        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getProject(anyLong())).thenThrow(new NotFoundException("Project not found"));

        ResponseEntity<Object> response = projectController.getProjectById(1L, "valid-access-token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAll_ValidToken_ProjectsFound() {
        when(jwtService.isTokenTrue(anyString())).thenReturn(true);

        List<Project> projectList = new ArrayList<>();
        Project project1 = new Project();
        project1.setProjectName("P1");
        project1.setProjectId(1L);
        projectList.add(project1);

        when(projectService.getAll()).thenReturn(projectList);

        ResponseEntity<Object> response = projectController.getAll("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ProjectDTO> responseDTOs = (List<ProjectDTO>) response.getBody();

        assert responseDTOs != null;
        assertEquals(projectList.size(), responseDTOs.size());
    }

    @Test
    void testGetAll_ValidToken_ProjectsNotFound() {
        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getAll()).thenThrow(new NotFoundException("Project not found"));

        ResponseEntity<Object> response = projectController.getAll("valid-access-token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllProjectsWithUsers_ValidToken_ProjectWithUsersFound(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(true);

        List<ProjectWithUsersDTO> projectWithUsersDTOS = new ArrayList<>();
        ProjectWithUsersDTO project = new ProjectWithUsersDTO();
        project.setProjectName("P1");
        project.setProjectId(1L);
        projectWithUsersDTOS.add(project);

        when(projectService.getAllProjectsWithUsers()).thenReturn(projectWithUsersDTOS);

        ResponseEntity<Object> response = projectController.getAllProjectsWithUsers("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ProjectWithUsersDTO> responseDTOs = (List<ProjectWithUsersDTO>) response.getBody();

        assert responseDTOs != null;
        assertEquals(projectWithUsersDTOS.size(), responseDTOs.size());
    }

    @Test
    void testGetAllProjectsWithUsers_ValidToken_ProjectWithUsersNotFound(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getAllProjectsWithUsers()).thenThrow(new NotFoundException("Not found"));

        ResponseEntity<Object> response = projectController.getAllProjectsWithUsers("valid-access-token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllUsersByProjectId_Successful() {
        Long projectId = 1L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

        List<UserDTO> userDTOList = new ArrayList<>();
        userDTOList.add(new UserDTO());
        when(projectService.getAllUsersByProjectId(projectId)).thenReturn(userDTOList);

        ResponseEntity<Object> response = projectController.getAllUsersByProjectId(projectId, accessToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        assertEquals(userDTOList, response.getBody());
    }

    @Test
    void testGetAllUsersByProjectId_NoUsersFound() {
        Long projectId = 1L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

        when(projectService.getAllUsersByProjectId(projectId)).thenReturn(Collections.emptyList());

        ResponseEntity<Object> response = projectController.getAllUsersByProjectId(projectId, accessToken);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetAllUsersByProjectId_NotFoundException() {
        Long projectId = 1L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

        when(projectService.getAllUsersByProjectId(projectId)).thenThrow(new NotFoundException("Project not found"));

        ResponseEntity<Object> response = projectController.getAllUsersByProjectId(projectId, accessToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllUsersByProjectId_InternalServerError() {
        Long projectId = 1L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

        when(projectService.getAllUsersByProjectId(projectId)).thenThrow(new RuntimeException("Something went wrong"));

        ResponseEntity<Object> response = projectController.getAllUsersByProjectId(projectId, accessToken);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetAllUsersByProjectIdByRole_Successful() {
        Long projectId = 1L;
        String role = "USER";
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

        EnumRole enumRole = EnumRole.USER;
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        when(projectService.getAllUsersByProjectIdAndRole(projectId, enumRole)).thenReturn(userList);

        ResponseEntity<Object> response = projectController.getAllUsersByProjectIdByRole(projectId, role, accessToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
    }

    @Test
    void testGetAllUsersByProjectIdByRole_NoUsersFound() {
        Long projectId = 1L;
        String role = "USER";
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

        EnumRole enumRole = EnumRole.USER;
        when(projectService.getAllUsersByProjectIdAndRole(projectId, enumRole)).thenReturn(Collections.emptyList());

        ResponseEntity<Object> response = projectController.getAllUsersByProjectIdByRole(projectId, role, accessToken);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetAllUsersByProjectIdByRole_InternalServerError() {
        Long projectId = 1L;
        String role = "USER";
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

        EnumRole enumRole = EnumRole.USER;
        when(projectService.getAllUsersByProjectIdAndRole(projectId, enumRole))
                .thenThrow(new RuntimeException("Something went wrong"));

        ResponseEntity<Object> response = projectController.getAllUsersByProjectIdByRole(projectId, role, accessToken);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetAllUsersByProjectIdByRole_UsernamesNull() {
        Long projectId = 1L;
        String role = "USER";
        String accessToken = "valid_token";

        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        List<User> userList = new ArrayList<>();

        User userWithNullUsernames = new User();
        userWithNullUsernames.setId(1L);
        userWithNullUsernames.setName("User Name");
        userWithNullUsernames.setEmail("user@example.com");
        userWithNullUsernames.setEnumRole(enumRole);
        userWithNullUsernames.setUserNames(null); // Set usernames to null

        userList.add(userWithNullUsernames);

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.getAllUsersByProjectIdAndRole(projectId, enumRole)).thenReturn(userList);

        ResponseEntity<Object> response = projectController.getAllUsersByProjectIdByRole(projectId, role, accessToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<UserDTO> userDTOList = (List<UserDTO>) response.getBody();
        assertEquals(1, userDTOList.size());
        assertEquals(1, userDTOList.size());

        UserDTO userDTO = userDTOList.get(0);
        assertEquals(userWithNullUsernames.getId(), userDTO.getId());
        assertEquals(userWithNullUsernames.getName(), userDTO.getName());
        assertEquals(userWithNullUsernames.getEmail(), userDTO.getEmail());
        assertEquals(userWithNullUsernames.getEnumRole(), userDTO.getEnumRole());
        assertNull(userDTO.getGitHubUsername()); // Username should be null in the response
    }

    @Test
    void testUpdateProject_SuccessfulUpdate() {
        Long projectId = 1L;
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("Updated Project");
        projectDTO.setProjectDescription("Updated Description");
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

        Project existingProject = new Project();
        existingProject.setProjectId(projectId);
        existingProject.setProjectName("Old Project");
        existingProject.setProjectDescription("Old Description");

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectService.updateProject(existingProject)).thenReturn(existingProject);

        ResponseEntity<Object> response = projectController.updateProject(projectId, projectDTO, accessToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ProjectDTO);
        ProjectDTO updatedProjectDTO = (ProjectDTO) response.getBody();
        assertEquals(projectDTO.getProjectName(), updatedProjectDTO.getProjectName());
        assertEquals(projectDTO.getProjectDescription(), updatedProjectDTO.getProjectDescription());
    }

    @Test
    void testUpdateProject_ProjectNotFound() {
        Long projectId = 1L;
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("Updated Project");
        projectDTO.setProjectDescription("Updated Description");
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.getProjectById(projectId)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = projectController.updateProject(projectId, projectDTO, accessToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateProject_InternalServerError() {
        Long projectId = 1L;
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("Updated Project");
        projectDTO.setProjectDescription("Updated Description");
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.getProjectById(projectId)).thenThrow(new RuntimeException("Something went wrong"));

        ResponseEntity<Object> response = projectController.updateProject(projectId, projectDTO, accessToken);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDeleteProject_SuccessfulDeletion() {
        Long projectId = 1L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.existsProjectById(projectId)).thenReturn(true);
        when(projectService.existsByIdIsDeleted(projectId)).thenReturn(false);
        when(projectService.softDeleteProject(projectId)).thenReturn(true);

        ResponseEntity<String> response = projectController.deleteProject(projectId, accessToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deleted project successfully", response.getBody());
    }

    @Test
    void testDeleteProject_FailedDeletion() {
        Long projectId = 1L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.existsProjectById(projectId)).thenReturn(true);
        when(projectService.existsByIdIsDeleted(projectId)).thenReturn(false);
        when(projectService.softDeleteProject(projectId)).thenReturn(false);

        ResponseEntity<String> response = projectController.deleteProject(projectId, accessToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteProject_ProjectNotFound() {
        Long projectId = 1L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.existsProjectById(projectId)).thenReturn(false);

        ResponseEntity<String> response = projectController.deleteProject(projectId, accessToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteProject_AlreadyDeleted() {
        Long projectId = 1L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.existsProjectById(projectId)).thenReturn(true);
        when(projectService.existsByIdIsDeleted(projectId)).thenReturn(true);

        ResponseEntity<String> response = projectController.deleteProject(projectId, accessToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Project doesn't exist", response.getBody());
    }

    @Test
    void testAddUserToProject_SuccessfulAddition() {
        Long projectId = 1L;
        Long userId = 2L;
        String accessToken = "valid_token";

        ResponseEntity<Object> successfulResponse = new ResponseEntity<>("User added successfully", HttpStatus.OK);

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.addUserToProjectByUserIdAndProjectId(projectId, userId)).thenReturn(successfulResponse);

        ResponseEntity<Object> response = projectController.addUserToProject(projectId, userId, accessToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User added successfully", response.getBody());
    }

    @Test
    void testAddUserToProject_ResourceNotFound() {
        Long projectId = 1L;
        Long userId = 2L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.addUserToProjectByUserIdAndProjectId(projectId, userId))
                .thenThrow(new NotFoundException("Resource not found"));

        ResponseEntity<Object> response = projectController.addUserToProject(projectId, userId, accessToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody());
    }

    @Test
    void testAddUserToProject_Conflict() {
        Long projectId = 1L;
        Long userId = 2L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.addUserToProjectByUserIdAndProjectId(projectId, userId))
                .thenThrow(new ConflictException("User already exists in the project"));

        ResponseEntity<Object> response = projectController.addUserToProject(projectId, userId, accessToken);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists in the project", response.getBody());
    }

    @Test
    void testAddUserToProject_InternalServerError() {
        Long projectId = 1L;
        Long userId = 2L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.addUserToProjectByUserIdAndProjectId(projectId, userId))
                .thenThrow(new RuntimeException("Something went wrong"));

        ResponseEntity<Object> response = projectController.addUserToProject(projectId, userId, accessToken);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong", response.getBody());
    }

    @Test
    void testRemoveUserFromProject_SuccessfulRemoval() {
        Long projectId = 1L;
        Long userId = 2L;
        String accessToken = "valid_token";

        ResponseEntity<String> successfulResponse = new ResponseEntity<>("User removed successfully", HttpStatus.OK);

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId)).thenReturn(successfulResponse);

        ResponseEntity<String> response = projectController.removeUserFromProject(projectId, userId, accessToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User removed successfully", response.getBody());
    }

    @Test
    void testRemoveUserFromProject_ResourceNotFound() {
        Long projectId = 1L;
        Long userId = 2L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId))
                .thenThrow(new NotFoundException("Resource not found"));

        ResponseEntity<String> response = projectController.removeUserFromProject(projectId, userId, accessToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody());
    }

    @Test
    void testRemoveUserFromProject_InternalServerError() {
        Long projectId = 1L;
        Long userId = 2L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId))
                .thenThrow(new RuntimeException("Something went wrong"));

        ResponseEntity<String> response = projectController.removeUserFromProject(projectId, userId, accessToken);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong", response.getBody());
    }

    @Test
    void testRemoveUserFromProjectAndRepo_SuccessfulRemoval() {
        Long projectId = 1L;
        Long userId = 2L;
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "valid_token";

        ResponseEntity<String> successfulResponse = new ResponseEntity<>("User removed successfully", HttpStatus.OK);

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO)).thenReturn(successfulResponse);

        ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO, accessToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User removed successfully", response.getBody());
    }

    @Test
    void testRemoveUserFromProjectAndRepo_UserNotFound() {
        Long projectId = 1L;
        Long userId = 2L;
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO))
                .thenThrow(new NotFoundException("User not found"));

        ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO, accessToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Project or User not found", response.getBody());
    }

    @Test
    void testRemoveUserFromProjectAndRepo_ProjectNotFound() {
        Long projectId = 1L;
        Long userId = 2L;
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO))
                .thenThrow(new NotFoundException("Project not found"));

        ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO, accessToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Project or User not found", response.getBody());
    }

    @Test
    void testRemoveUserFromProjectAndRepo_BadRequest() {
        Long projectId = 1L;
        Long userId = 2L;
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "valid_token";

        ResponseEntity<String> badRequestResponse = new ResponseEntity<>("Unable to remove user", HttpStatus.BAD_REQUEST);

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO)).thenReturn(badRequestResponse);

        ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO, accessToken);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unable to remove user", response.getBody());
    }

    @Test
    void testRemoveUserFromProjectAndRepo_InternalServerError() {
        Long projectId = 1L;
        Long userId = 2L;
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO))
                .thenThrow(new RuntimeException("Something went wrong"));

        ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO, accessToken);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong", response.getBody());
    }

    @Test
    void testAddRepositoryToProject_SuccessfulAddition() {
        Long projectId = 1L;
        Long repoId = 2L;
        String accessToken = "valid_token";

        ResponseEntity<Object> successfulResponse = ResponseEntity.ok("Stored successfully");

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.addRepositoryToProject(projectId, repoId)).thenReturn(successfulResponse);

        ResponseEntity<Object> response = projectController.addRepositoryToProject(projectId, repoId, accessToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Stored successfully", response.getBody());
    }

    @Test
    void testAddRepositoryToProject_RepositoryNotFound() {
        Long projectId = 1L;
        Long repoId = 2L;
        String accessToken = "valid_token";

        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repository or Project not found");

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.addRepositoryToProject(projectId, repoId)).thenReturn(notFoundResponse);

        ResponseEntity<Object> response = projectController.addRepositoryToProject(projectId, repoId, accessToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Repository or Project not found", response.getBody());
    }

    @Test
    void testAddRepositoryToProject_InternalServerError() {
        Long projectId = 1L;
        Long repoId = 2L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.addRepositoryToProject(projectId, repoId)).thenThrow(new RuntimeException("Something went wrong"));

        ResponseEntity<Object> response = projectController.addRepositoryToProject(projectId, repoId, accessToken);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetUsersByProjectIdAndRole_ValidToken(){
        List<UserDTO> userDTOList = new ArrayList<>();
        UserDTO user1 = new UserDTO();
        UserDTO user2 = new UserDTO();
        userDTOList.add(user1);
        userDTOList.add(user2);

        Long projectId = 1L;
        String role = EnumRole.USER.getEnumRole();

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getUsersByProjectIdAndRole(projectId, role)).thenReturn(userDTOList);

        ResponseEntity<Object> response = projectController.getUsersByProjectIdAndRole(projectId, role, "valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTOList, response.getBody());
    }

    @Test
    void testGetProjectsWithoutFigmaURL_ValidToken(){
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        ProjectDTO projectDTO1 = new ProjectDTO();
        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTOList.add(projectDTO1);
        projectDTOList.add(projectDTO2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getProjectsWithoutFigmaURL()).thenReturn(projectDTOList);

        ResponseEntity<Object> response = projectController.getProjectsWithoutFigmaURL("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDTOList, response.getBody());
    }

    @Test
    void testGetProjectsWithoutGoogleDriveLink_ValidToken(){
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        ProjectDTO projectDTO1 = new ProjectDTO();
        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTOList.add(projectDTO1);
        projectDTOList.add(projectDTO2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getProjectsWithoutGoogleDriveLink()).thenReturn(projectDTOList);

        ResponseEntity<Object> response = projectController.getProjectsWithoutGoogleDriveLink("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDTOList, response.getBody());
    }

    @Test
    void testCountAllPeopleByProjectIdAndName_NotEmpty_ValidToken(){

        List<ProjectNamePeopleCountDTO> mockDtoList = new ArrayList<>();
        ProjectNamePeopleCountDTO peopleCountDTO1 = new ProjectNamePeopleCountDTO();
        peopleCountDTO1.setProjectName("p1");

        ProjectNamePeopleCountDTO peopleCountDTO2 = new ProjectNamePeopleCountDTO();
        peopleCountDTO2.setProjectName("p2");

        mockDtoList.add(peopleCountDTO1);
        mockDtoList.add(peopleCountDTO2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllPeopleAndProjectName()).thenReturn(mockDtoList);

        // Act
        ResponseEntity<Object> response = projectController.countAllPeopleByProjectIdAndName("valid-access-token");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockDtoList, response.getBody());
    }

    @Test
    void testCountAllPeopleByProjectIdAndName_Empty_ValidToken(){
        // mockDtoList is empty
        List<ProjectNamePeopleCountDTO> mockDtoList = new ArrayList<>();

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);

        // Add test data to mockDtoList
        when(projectService.getCountAllPeopleAndProjectName()).thenReturn(mockDtoList);

        ResponseEntity<Object> response = projectController.countAllPeopleByProjectIdAndName("valid-access-token");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Empty", response.getBody());
    }

    @Test
    void testCountAllProjects_NotEmpty_ValidToken(){
        List<ProjectDTO> mockDtoList = new ArrayList<>();
        ProjectDTO projectDTO1 = new ProjectDTO();
        projectDTO1.setProjectName("p1");

        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTO2.setProjectName("p2");

        mockDtoList.add(projectDTO1);
        mockDtoList.add(projectDTO2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllProjects()).thenReturn(mockDtoList.size());

        ResponseEntity<Object> response = projectController.countAllProjects("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, mockDtoList.size());
        assertEquals(mockDtoList.size(), response.getBody());
    }

    @Test
    void testCountAllProjects_Empty_ValidToken(){
        // mockDtoList is empty
        List<ProjectDTO> mockDtoList = new ArrayList<>();

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllProjects()).thenReturn(mockDtoList.size());

        ResponseEntity<Object> response = projectController.countAllProjects("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, mockDtoList.size());
        assertEquals(mockDtoList.size(), response.getBody());
    }

    @Test
    void testCountAllProjectsByRole_NotEmpty_ValidToken(){
        List<ProjectDTO> mockDtoList = new ArrayList<>();
        ProjectDTO projectDTO1 = new ProjectDTO();
        projectDTO1.setProjectName("p1");

        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTO2.setProjectName("p2");

        mockDtoList.add(projectDTO1);
        mockDtoList.add(projectDTO2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllProjectsByRole(EnumRole.ADMIN)).thenReturn(mockDtoList.size());

        ResponseEntity<Object> response = projectController.countAllProjectsByRole(EnumRole.ADMIN.getEnumRole(),
                "valid-access-token");

        assertEquals(2, mockDtoList.size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody());
    }

    @Test
    void testCountAllProjectsByRole_Empty_ValidToken(){

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllProjectsByRole(EnumRole.ADMIN)).thenReturn(0);

        String role = EnumRole.ADMIN.getEnumRole();

        ResponseEntity<Object> response = projectController.countAllProjectsByRole(role,"valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody());
    }

    @Test
    void testCountAllProjectsByUserId_NotEmpty_ValidToken(){

        List<ProjectDTO> mockDtoList = new ArrayList<>();
        ProjectDTO projectDTO1 = new ProjectDTO();
        projectDTO1.setProjectName("p1");

        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTO2.setProjectName("p2");

        mockDtoList.add(projectDTO1);
        mockDtoList.add(projectDTO2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllProjectsByUserId(1L)).thenReturn(mockDtoList.size());

        ResponseEntity<Object> response = projectController.countAllProjectsByUserId(1L, "valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody());
        assertEquals(2, mockDtoList.size());
    }

    @Test
    void testCountAllProjectsByUserId_Empty_ValidToken(){

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllProjectsByUserId(1L)).thenReturn(0);

        ResponseEntity<Object> response = projectController.countAllProjectsByUserId(1L, "valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody());
    }

    @Test
    void testCountAllUsersByProjectId_NotEmpty_ValidToken(){
        List<ProjectDTO> mockDtoList = new ArrayList<>();

        ProjectDTO projectDTO1 = new ProjectDTO();
        projectDTO1.setProjectName("P1");
        projectDTO1.setProjectDescription("P1 description");

        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTO2.setProjectName("P2");
        projectDTO2.setProjectDescription("P2 description");

        mockDtoList.add(projectDTO1);
        mockDtoList.add(projectDTO2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllUsersByProjectId(1L)).thenReturn(mockDtoList.size());

        // Act
        ResponseEntity<Object> response = projectController.countAllUsersByProjectId(1L,"valid-access-token");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody());
    }

    @Test
    void testCountAllUsersByProjectId_Empty_ValidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllUsersByProjectId(1L)).thenReturn(0);

        // Act
        ResponseEntity<Object> response = projectController.countAllUsersByProjectId(1L,"valid-access-token");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody());
    }

    @Test
    void testCountAllUsersByProjectIdByRole_NotEmpty_ValidToken(){
        List<ProjectDTO> mockDtoList = new ArrayList<>();

        ProjectDTO projectDTO1 = new ProjectDTO();
        projectDTO1.setProjectName("P1");
        projectDTO1.setProjectDescription("P1 description");

        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTO2.setProjectName("P2");
        projectDTO2.setProjectDescription("P2 description");

        mockDtoList.add(projectDTO1);
        mockDtoList.add(projectDTO2);

        String role = EnumRole.ADMIN.getEnumRole();

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllUsersByProjectIdAndRole(1L, EnumRole.ADMIN)).thenReturn(mockDtoList.size());

        ResponseEntity<Object> response = projectController.countAllUsersByProjectIdByRole(1L, role,"valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody());
    }

    @Test
    void testCountAllUsersByProjectIdByRole_Empty_ValidToken(){
        String role = EnumRole.ADMIN.getEnumRole();

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllUsersByProjectIdAndRole(1L, EnumRole.ADMIN)).thenReturn(0);

        ResponseEntity<Object> response = projectController.countAllUsersByProjectIdByRole(1L, role,"valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody());
    }

    @Test
    void testCountAllActiveProjects_NotEmpty_ValidToken(){
        List<ProjectDTO> mockDtoList = new ArrayList<>();

        ProjectDTO projectDTO1 = new ProjectDTO();
        projectDTO1.setProjectName("P1");
        projectDTO1.setProjectDescription("P1 description");

        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTO2.setProjectName("P2");
        projectDTO2.setProjectDescription("P2 description");

        mockDtoList.add(projectDTO1);
        mockDtoList.add(projectDTO2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllActiveProjects()).thenReturn(mockDtoList.size());

        ResponseEntity<Object> response = projectController.countAllActiveProjects("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody());
    }

    @Test
    void testCountAllActiveProjects_Empty_ValidToken(){

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllActiveProjects()).thenReturn(0);

        ResponseEntity<Object> response = projectController.countAllActiveProjects("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody());
    }

    @Test
    void testCountAllInactiveProjects_NotEmpty_ValidToken(){
        List<ProjectDTO> mockDtoList = new ArrayList<>();

        ProjectDTO projectDTO1 = new ProjectDTO();
        projectDTO1.setProjectName("P1");
        projectDTO1.setProjectDescription("P1 description");

        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTO2.setProjectName("P2");
        projectDTO2.setProjectDescription("P2 description");

        mockDtoList.add(projectDTO1);
        mockDtoList.add(projectDTO2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllInActiveProjects()).thenReturn(mockDtoList.size());

        ResponseEntity<Object> response = projectController.countAllInActiveProjects("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody());
    }

    @Test
    void testCountAllInactiveProjects_Empty_ValidToken(){

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getCountAllInActiveProjects()).thenReturn(0);

        ResponseEntity<Object> response = projectController.countAllInActiveProjects("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody());
    }

    @Test
    void testGetProjectDetailsById_NotEmpty_ValidToken(){
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        userList.add(user1);
        userList.add(user2);

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("P1");
        projectDTO.setProjectDescription("P1 Description");
        projectDTO.setUsers(userList);
        projectDTO.setStatus(true);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getProjectDetailsById(1L)).thenReturn(projectDTO);

        ResponseEntity<Object> response = projectController.getProjectDetailsById("valid-access-token", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDTO, response.getBody());
    }

    @Test
    void testGetProjectDetailsById_Empty_ValidToken(){
        ProjectDTO projectDTO = new ProjectDTO();

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getProjectDetailsById(1L)).thenReturn(projectDTO);

        ResponseEntity<Object> response = projectController.getProjectDetailsById("valid-access-token", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetProjectDetailsById_InternalServerError() {
        Long projectId = 1L;
        String accessToken = "valid_token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(projectService.getProjectDetailsById(projectId)).thenThrow(new RuntimeException("Something went wrong"));

        ResponseEntity<Object> response = projectController.getProjectDetailsById(accessToken, projectId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    // ----- FAILURE (For INVALID TOKEN)-----

    @Test
    void testCreateProject_InvalidToken(){
        // projectDTO is empty
        ProjectDTO projectDTO = new ProjectDTO();

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);
        when(projectService.createProject(projectDTO)).thenReturn(projectDTO);

        ResponseEntity<Object> response = projectController.createProject(projectDTO, "valid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetAll_InvalidToken() {
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.getAll("invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetProjectById_InvalidToken() {
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.getProjectById(1L, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetAllProjectsWithUsers_InvalidToken() {
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.getAllProjectsWithUsers("invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetAllUsersByProjectId_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.getAllUsersByProjectId(1L, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetAllUsersByProjectIdByRole_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);
        String role = EnumRole.USER.getEnumRole();

        ResponseEntity<Object> response = projectController.getAllUsersByProjectIdByRole(1L, role,"invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testUpdateProject_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ProjectDTO projectDTO = new ProjectDTO();

        ResponseEntity<Object> response = projectController.updateProject(1L, projectDTO,"invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testDeleteProject_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<String> response = projectController.deleteProject(1L, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testAddUserToProject_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);
        Long projectId = 1L;
        Long userId = 2L;

        ResponseEntity<Object> response = projectController.addUserToProject(projectId,userId, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testRemoveUserFromProject_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);
        Long projectId = 1L;
        Long userId = 2L;

        ResponseEntity<String> response = projectController.removeUserFromProject(projectId,userId, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testRemoveUserFromProjectAndRepo_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);
        Long projectId = 1L;
        Long userId = 2L;

        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

        ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId,userId,
                collaboratorDTO,"invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetUsersByProjectIdAndRole_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);
        String role = EnumRole.PROJECT_MANAGER.getEnumRole();

        ResponseEntity<Object> response = projectController.getUsersByProjectIdAndRole(1L,role,"invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testAddRepositoryToProject_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);
        Long projectId = 1L;
        Long repoId = 2L;

        ResponseEntity<Object> response = projectController.addRepositoryToProject(projectId,repoId,"invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetProjectsWithoutFigmaURL_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.getProjectsWithoutFigmaURL("invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetProjectsWithoutGoogleDriveLink_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.getProjectsWithoutGoogleDriveLink("invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testCountAllPeopleByProjectIdAndName_InvalidToken(){

        // Arrange
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        // Act
        ResponseEntity<Object> response = projectController.countAllPeopleByProjectIdAndName("invalid-access-token");

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testCountAllProjects_InvalidToken(){

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.countAllProjects("invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testCountAllProjectsByRole_InvalidToken(){

        String role = EnumRole.PROJECT_MANAGER.getEnumRole();
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.countAllProjectsByRole(role,"invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testCountAllProjectsByUserId_InvalidToken(){

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.countAllProjectsByUserId(1L, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testCountAllUsersByProjectId_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.countAllUsersByProjectId(1L, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testCountAllUsersByProjectIdByRole_InvalidToken(){
        String role = EnumRole.PROJECT_MANAGER.getEnumRole();
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.countAllUsersByProjectIdByRole(1L, role, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testCountAllActiveProjects_InvalidToken(){

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.countAllActiveProjects("invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testCountAllInactiveProjects_InvalidToken(){

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = projectController.countAllInActiveProjects("invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetProjectDetailsById_InvalidToken(){
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("P1");
        projectDTO.setProjectDescription("P1 Description");
        projectDTO.setStatus(true);

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);
        when(projectService.getProjectDetailsById(1L)).thenReturn(projectDTO);

        ResponseEntity<Object> response = projectController.getProjectDetailsById("invalid-access-token", 1L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }
}
