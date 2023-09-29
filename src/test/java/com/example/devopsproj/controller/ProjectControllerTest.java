package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
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

import java.util.ArrayList;
import java.util.List;

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
    void testGetProjectById_ValidToken_BadRequest() {
        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(projectService.getProject(anyLong())).thenThrow(new IllegalArgumentException());

        ResponseEntity<Object> response = projectController.getProjectById(1L, "valid-access-token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
