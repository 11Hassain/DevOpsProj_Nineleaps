package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.exceptions.ConflictException;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.model.UserNames;
import com.example.devopsproj.service.implementations.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;
    @Mock
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Testing success case with valid token")
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

        when(projectService.createProject(projectDTO)).thenReturn(projectDTO);

        ResponseEntity<Object> response = projectController.createProject(projectDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Retrieve the projectDTO from the response
        ProjectDTO responseProjectDTO = (ProjectDTO) response.getBody();

        assert responseProjectDTO != null;
        assertEquals("P1", responseProjectDTO.getProjectName());
    }

    @Nested
    class GetProjectByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetProjectById_ValidToken_ProjectFound(){
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectName("P1");
            projectDTO.setProjectDescription("P1 description");

            when(projectService.getProject(anyLong())).thenReturn(ResponseEntity.ok(projectDTO));

            ResponseEntity<Object> response = projectController.getProjectById(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(projectDTO, response.getBody());
        }

        @Test
        @DisplayName("Testing project not found case")
        void testGetProjectById_ValidToken_ProjectNotFound() {
            when(projectService.getProject(anyLong())).thenThrow(new NotFoundException("Project not found"));

            ResponseEntity<Object> response = projectController.getProjectById(1L);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    class GetAllTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAll_ValidToken_ProjectsFound() {
            List<Project> projectList = new ArrayList<>();
            Project project1 = new Project();
            project1.setProjectName("P1");
            project1.setProjectId(1L);
            projectList.add(project1);

            when(projectService.getAll()).thenReturn(projectList);

            ResponseEntity<Object> response = projectController.getAll();

            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<ProjectDTO> responseDTOs = (List<ProjectDTO>) response.getBody();

            assert responseDTOs != null;
            assertEquals(projectList.size(), responseDTOs.size());
        }

        @Test
        @DisplayName("Testing project not found case")
        void testGetAll_ValidToken_ProjectsNotFound() {
            when(projectService.getAll()).thenThrow(new NotFoundException("Project not found"));

            ResponseEntity<Object> response = projectController.getAll();

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    class GetAllProjectsWithUsersTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllProjectsWithUsers_ValidToken_ProjectWithUsersFound(){

            List<ProjectWithUsersDTO> projectWithUsersDTOS = new ArrayList<>();
            ProjectWithUsersDTO project = new ProjectWithUsersDTO();
            project.setProjectName("P1");
            project.setProjectId(1L);
            projectWithUsersDTOS.add(project);

            when(projectService.getAllProjectsWithUsers()).thenReturn(projectWithUsersDTOS);

            ResponseEntity<Object> response = projectController.getAllProjectsWithUsers();

            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<ProjectWithUsersDTO> responseDTOs = (List<ProjectWithUsersDTO>) response.getBody();

            assert responseDTOs != null;
            assertEquals(projectWithUsersDTOS.size(), responseDTOs.size());
        }

        @Test
        @DisplayName("Testing projects with users not found case")
        void testGetAllProjectsWithUsers_ValidToken_ProjectWithUsersNotFound(){
            when(projectService.getAllProjectsWithUsers()).thenThrow(new NotFoundException("Not found"));

            ResponseEntity<Object> response = projectController.getAllProjectsWithUsers();

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    class GetAllUsersByProjectIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllUsersByProjectId_Successful() {
            Long projectId = 1L;

            List<UserDTO> userDTOList = new ArrayList<>();
            userDTOList.add(new UserDTO());
            when(projectService.getAllUsersByProjectId(projectId)).thenReturn(userDTOList);

            ResponseEntity<Object> response = projectController.getAllUsersByProjectId(projectId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody() instanceof List);
            assertEquals(userDTOList, response.getBody());
        }

        @Test
        @DisplayName("Testing Users not found case")
        void testGetAllUsersByProjectId_NoUsersFound() {
            Long projectId = 1L;

            when(projectService.getAllUsersByProjectId(projectId)).thenReturn(Collections.emptyList());

            ResponseEntity<Object> response = projectController.getAllUsersByProjectId(projectId);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing project not found case")
        void testGetAllUsersByProjectId_NotFoundException() {
            Long projectId = 1L;

            when(projectService.getAllUsersByProjectId(projectId)).thenThrow(new NotFoundException("Project not found"));

            ResponseEntity<Object> response = projectController.getAllUsersByProjectId(projectId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing failure case (internal server error)")
        void testGetAllUsersByProjectId_InternalServerError() {
            Long projectId = 1L;

            when(projectService.getAllUsersByProjectId(projectId)).thenThrow(new RuntimeException("Something went wrong"));

            ResponseEntity<Object> response = projectController.getAllUsersByProjectId(projectId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }

    @Nested
    class GetAllUsersByProjectIdByRoleTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllUsersByProjectIdByRole_Successful() {
            Long projectId = 1L;
            String role = "USER";

            EnumRole enumRole = EnumRole.USER;
            List<User> userList = new ArrayList<>();
            userList.add(new User());
            when(projectService.getAllUsersByProjectIdAndRole(projectId, enumRole)).thenReturn(userList);

            ResponseEntity<Object> response = projectController.getAllUsersByProjectIdByRole(projectId, role);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody() instanceof List);
        }

        @Test
        @DisplayName("Testing users not found case")
        void testGetAllUsersByProjectIdByRole_NoUsersFound() {
            Long projectId = 1L;
            String role = "USER";

            EnumRole enumRole = EnumRole.USER;
            when(projectService.getAllUsersByProjectIdAndRole(projectId, enumRole)).thenReturn(Collections.emptyList());

            ResponseEntity<Object> response = projectController.getAllUsersByProjectIdByRole(projectId, role);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing failure case (internal server error)")
        void testGetAllUsersByProjectIdByRole_InternalServerError() {
            Long projectId = 1L;
            String role = "USER";

            EnumRole enumRole = EnumRole.USER;
            when(projectService.getAllUsersByProjectIdAndRole(projectId, enumRole))
                    .thenThrow(new RuntimeException("Something went wrong"));

            ResponseEntity<Object> response = projectController.getAllUsersByProjectIdByRole(projectId, role);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing username null case")
        void testGetAllUsersByProjectIdByRole_UsernameNull() {
            Long projectId = 1L;
            String role = "USER";

            // Create a user with null username
            User userWithNullUsername = new User();
            userWithNullUsername.setId(1L);
            userWithNullUsername.setName("User1");
            userWithNullUsername.setEmail("user1@example.com");
            userWithNullUsername.setEnumRole(EnumRole.USER);

            List<User> userList = Collections.singletonList(userWithNullUsername);

            when(projectService.getAllUsersByProjectIdAndRole(projectId, EnumRole.USER)).thenReturn(userList);

            ResponseEntity<Object> response = projectController.getAllUsersByProjectIdByRole(projectId, role);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<UserDTO> userDTOList = (List<UserDTO>) response.getBody();

            assertNotNull(userDTOList);
            assertFalse(userDTOList.isEmpty());

            UserDTO userDTO = userDTOList.get(0);
            assertNull(userDTO.getGitHubUsername());
        }

        @Test
        @DisplayName("Testing username not null case")
        void testGetAllUsersByProjectIdByRole_UsernameNotNull() {
            Long projectId = 1L;
            String role = "USER";

            // Create a user with a non-null username
            User userWithUsername = new User();
            userWithUsername.setId(1L);
            userWithUsername.setName("User1");
            userWithUsername.setEmail("user1@example.com");
            userWithUsername.setEnumRole(EnumRole.USER);

            UserNames usernames = new UserNames();
            usernames.setUsername("user1_username");
            userWithUsername.setUserNames(usernames);

            List<User> userList = Collections.singletonList(userWithUsername);

            when(projectService.getAllUsersByProjectIdAndRole(projectId, EnumRole.USER)).thenReturn(userList);

            ResponseEntity<Object> response = projectController.getAllUsersByProjectIdByRole(projectId, role);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<UserDTO> userDTOList = (List<UserDTO>) response.getBody();

            assertNotNull(userDTOList);
            assertFalse(userDTOList.isEmpty());

            UserDTO userDTO = userDTOList.get(0);
            assertNotNull(userDTO.getGitHubUsername());
            assertEquals("user1_username", userDTO.getGitHubUsername());
        }
    }

    @Nested
    class UpdateProjectTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testUpdateProject_SuccessfulUpdate() {
            Long projectId = 1L;
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectName("Updated Project");
            projectDTO.setProjectDescription("Updated Description");

            Project existingProject = new Project();
            existingProject.setProjectId(projectId);
            existingProject.setProjectName("Old Project");
            existingProject.setProjectDescription("Old Description");

            when(projectService.getProjectById(projectId)).thenReturn(Optional.of(existingProject));
            when(projectService.updateProject(existingProject)).thenReturn(existingProject);

            ResponseEntity<Object> response = projectController.updateProject(projectId, projectDTO);

            assertAll("All Assertions",
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                    () -> assertTrue(response.getBody() instanceof ProjectDTO, "Response body should be an instance of ProjectDTO"),
                    () -> {
                        ProjectDTO updatedProjectDTO = (ProjectDTO) response.getBody();
                        assert updatedProjectDTO != null;
                        assertEquals(projectDTO.getProjectName(), updatedProjectDTO.getProjectName(), "Project name should match");
                        assertEquals(projectDTO.getProjectDescription(), updatedProjectDTO.getProjectDescription(), "Project description should match");
                    }
            );
        }

        @Test
        @DisplayName("Testing project not found case")
        void testUpdateProject_ProjectNotFound() {
            Long projectId = 1L;
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectName("Updated Project");
            projectDTO.setProjectDescription("Updated Description");

            when(projectService.getProjectById(projectId)).thenReturn(Optional.empty());

            ResponseEntity<Object> response = projectController.updateProject(projectId, projectDTO);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing failure case (internal server error)")
        void testUpdateProject_InternalServerError() {
            Long projectId = 1L;
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectName("Updated Project");
            projectDTO.setProjectDescription("Updated Description");

            when(projectService.getProjectById(projectId)).thenThrow(new RuntimeException("Something went wrong"));

            ResponseEntity<Object> response = projectController.updateProject(projectId, projectDTO);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }

    @Nested
    class DeleteProjectTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testDeleteProject_SuccessfulDeletion() {
            Long projectId = 1L;

            when(projectService.existsProjectById(projectId)).thenReturn(true);
            when(projectService.existsByIdIsDeleted(projectId)).thenReturn(false);
            when(projectService.softDeleteProject(projectId)).thenReturn(true);

            ResponseEntity<String> response = projectController.deleteProject(projectId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Deleted project successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case (not found)")
        void testDeleteProject_FailedDeletion() {
            Long projectId = 1L;

            when(projectService.existsProjectById(projectId)).thenReturn(true);
            when(projectService.existsByIdIsDeleted(projectId)).thenReturn(false);
            when(projectService.softDeleteProject(projectId)).thenReturn(false);

            ResponseEntity<String> response = projectController.deleteProject(projectId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing project not found case")
        void testDeleteProject_ProjectNotFound() {
            Long projectId = 1L;

            when(projectService.existsProjectById(projectId)).thenReturn(false);

            ResponseEntity<String> response = projectController.deleteProject(projectId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing already deleted project case")
        void testDeleteProject_AlreadyDeleted() {
            Long projectId = 1L;

            when(projectService.existsProjectById(projectId)).thenReturn(true);
            when(projectService.existsByIdIsDeleted(projectId)).thenReturn(true);

            ResponseEntity<String> response = projectController.deleteProject(projectId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Project doesn't exist", response.getBody());
        }
    }

    @Nested
    class AddUserToProjectTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testAddUserToProject_SuccessfulAddition() {
            Long projectId = 1L;
            Long userId = 2L;

            ResponseEntity<Object> successfulResponse = new ResponseEntity<>("User added successfully", HttpStatus.OK);

            when(projectService.addUserToProjectByUserIdAndProjectId(projectId, userId)).thenReturn(successfulResponse);

            ResponseEntity<Object> response = projectController.addUserToProject(projectId, userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User added successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing resource not found case")
        void testAddUserToProject_ResourceNotFound() {
            Long projectId = 1L;
            Long userId = 2L;

            when(projectService.addUserToProjectByUserIdAndProjectId(projectId, userId))
                    .thenThrow(new NotFoundException("Resource not found"));

            ResponseEntity<Object> response = projectController.addUserToProject(projectId, userId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Resource not found", response.getBody());
        }

        @Test
        @DisplayName("Testing user already exists in project case")
        void testAddUserToProject_Conflict() {
            Long projectId = 1L;
            Long userId = 2L;

            when(projectService.addUserToProjectByUserIdAndProjectId(projectId, userId))
                    .thenThrow(new ConflictException("User already exists in the project"));

            ResponseEntity<Object> response = projectController.addUserToProject(projectId, userId);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("User already exists in the project", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case (internal server error)")
        void testAddUserToProject_InternalServerError() {
            Long projectId = 1L;
            Long userId = 2L;

            when(projectService.addUserToProjectByUserIdAndProjectId(projectId, userId))
                    .thenThrow(new RuntimeException("Something went wrong"));

            ResponseEntity<Object> response = projectController.addUserToProject(projectId, userId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Something went wrong", response.getBody());
        }
    }

    @Nested
    class RemoveUserFromProjectTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testRemoveUserFromProject_SuccessfulRemoval() {
            Long projectId = 1L;
            Long userId = 2L;

            ResponseEntity<String> successfulResponse = new ResponseEntity<>("User removed successfully", HttpStatus.OK);

            when(projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId)).thenReturn(successfulResponse);

            ResponseEntity<String> response = projectController.removeUserFromProject(projectId, userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User removed successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing resource not found case")
        void testRemoveUserFromProject_ResourceNotFound() {
            Long projectId = 1L;
            Long userId = 2L;

            when(projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId))
                    .thenThrow(new NotFoundException("Resource not found"));

            ResponseEntity<String> response = projectController.removeUserFromProject(projectId, userId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Resource not found", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case (internal server error)")
        void testRemoveUserFromProject_InternalServerError() {
            Long projectId = 1L;
            Long userId = 2L;

            when(projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId))
                    .thenThrow(new RuntimeException("Something went wrong"));

            ResponseEntity<String> response = projectController.removeUserFromProject(projectId, userId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Something went wrong", response.getBody());
        }
    }

    @Nested
    class RemoveUserFromProjectAndRepoTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testRemoveUserFromProjectAndRepo_SuccessfulRemoval() {
            Long projectId = 1L;
            Long userId = 2L;
            CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

            ResponseEntity<String> successfulResponse = new ResponseEntity<>("User removed successfully", HttpStatus.OK);

            when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO)).thenReturn(successfulResponse);

            ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User removed successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing user not found case")
        void testRemoveUserFromProjectAndRepo_UserNotFound() {
            Long projectId = 1L;
            Long userId = 2L;
            CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

            when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO))
                    .thenThrow(new NotFoundException("User not found"));

            ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Project or User not found", response.getBody());
        }

        @Test
        @DisplayName("Testing project not found case")
        void testRemoveUserFromProjectAndRepo_ProjectNotFound() {
            Long projectId = 1L;
            Long userId = 2L;
            CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

            when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO))
                    .thenThrow(new NotFoundException("Project not found"));

            ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Project or User not found", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case (Bad request)")
        void testRemoveUserFromProjectAndRepo_BadRequest() {
            Long projectId = 1L;
            Long userId = 2L;
            CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

            ResponseEntity<String> badRequestResponse = new ResponseEntity<>("Unable to remove user", HttpStatus.BAD_REQUEST);

            when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO)).thenReturn(badRequestResponse);

            ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Unable to remove user", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case (internal server error)")
        void testRemoveUserFromProjectAndRepo_InternalServerError() {
            Long projectId = 1L;
            Long userId = 2L;
            CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

            when(projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO))
                    .thenThrow(new RuntimeException("Something went wrong"));

            ResponseEntity<String> response = projectController.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Something went wrong", response.getBody());
        }
    }

    @Test
    @DisplayName("Testing success case with valid token")
    void testGetUsersByProjectIdAndRole_ValidToken(){
        List<UserDTO> userDTOList = new ArrayList<>();
        UserDTO user1 = new UserDTO();
        UserDTO user2 = new UserDTO();
        userDTOList.add(user1);
        userDTOList.add(user2);

        Long projectId = 1L;
        String role = EnumRole.USER.getEnumRole();

        when(projectService.getUsersByProjectIdAndRole(projectId, role)).thenReturn(userDTOList);

        ResponseEntity<Object> response = projectController.getUsersByProjectIdAndRole(projectId, role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTOList, response.getBody());
    }

    @Nested
    class AddRepositoryToProjectTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testAddRepositoryToProject_SuccessfulAddition() {
            Long projectId = 1L;
            Long repoId = 2L;

            ResponseEntity<Object> successfulResponse = ResponseEntity.ok("Stored successfully");

            when(projectService.addRepositoryToProject(projectId, repoId)).thenReturn(successfulResponse);

            ResponseEntity<Object> response = projectController.addRepositoryToProject(projectId, repoId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Stored successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing repository not found case")
        void testAddRepositoryToProject_RepositoryNotFound() {
            Long projectId = 1L;
            Long repoId = 2L;

            ResponseEntity<Object> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repository or Project not found");

            when(projectService.addRepositoryToProject(projectId, repoId)).thenReturn(notFoundResponse);

            ResponseEntity<Object> response = projectController.addRepositoryToProject(projectId, repoId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Repository or Project not found", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case (internal server error)")
        void testAddRepositoryToProject_InternalServerError() {
            Long projectId = 1L;
            Long repoId = 2L;

            when(projectService.addRepositoryToProject(projectId, repoId)).thenThrow(new RuntimeException("Something went wrong"));

            ResponseEntity<Object> response = projectController.addRepositoryToProject(projectId, repoId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }

    @Test
    @DisplayName("Testing success case with valid token")
    void testGetProjectsWithoutFigmaURL_ValidToken(){
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        ProjectDTO projectDTO1 = new ProjectDTO();
        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTOList.add(projectDTO1);
        projectDTOList.add(projectDTO2);

        when(projectService.getProjectsWithoutFigmaURL()).thenReturn(projectDTOList);

        ResponseEntity<Object> response = projectController.getProjectsWithoutFigmaURL();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDTOList, response.getBody());
    }

    @Test
    @DisplayName("Testing success case with valid token")
    void testGetProjectsWithoutGoogleDriveLink_ValidToken(){
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        ProjectDTO projectDTO1 = new ProjectDTO();
        ProjectDTO projectDTO2 = new ProjectDTO();
        projectDTOList.add(projectDTO1);
        projectDTOList.add(projectDTO2);

        when(projectService.getProjectsWithoutGoogleDriveLink()).thenReturn(projectDTOList);

        ResponseEntity<Object> response = projectController.getProjectsWithoutGoogleDriveLink();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDTOList, response.getBody());
    }


    @Nested
    class CountAllPeopleByProjectIdAndNameTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testCountAllPeopleByProjectIdAndName_NotEmpty_ValidToken(){

            List<ProjectNamePeopleCountDTO> mockDtoList = new ArrayList<>();
            ProjectNamePeopleCountDTO peopleCountDTO1 = new ProjectNamePeopleCountDTO();
            peopleCountDTO1.setProjectName("p1");

            ProjectNamePeopleCountDTO peopleCountDTO2 = new ProjectNamePeopleCountDTO();
            peopleCountDTO2.setProjectName("p2");

            mockDtoList.add(peopleCountDTO1);
            mockDtoList.add(peopleCountDTO2);

            when(projectService.getCountAllPeopleAndProjectName()).thenReturn(mockDtoList);

            ResponseEntity<Object> response = projectController.countAllPeopleByProjectIdAndName();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockDtoList, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testCountAllPeopleByProjectIdAndName_Empty_ValidToken(){
            // mockDtoList is empty
            List<ProjectNamePeopleCountDTO> mockDtoList = new ArrayList<>();

            when(projectService.getCountAllPeopleAndProjectName()).thenReturn(mockDtoList);

            ResponseEntity<Object> response = projectController.countAllPeopleByProjectIdAndName();

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertEquals("Empty", response.getBody());
        }
    }

    @Nested
    class CountAllProjectsTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testCountAllProjects_NotEmpty_ValidToken(){
            List<ProjectDTO> mockDtoList = new ArrayList<>();
            ProjectDTO projectDTO1 = new ProjectDTO();
            projectDTO1.setProjectName("p1");

            ProjectDTO projectDTO2 = new ProjectDTO();
            projectDTO2.setProjectName("p2");

            mockDtoList.add(projectDTO1);
            mockDtoList.add(projectDTO2);

            when(projectService.getCountAllProjects()).thenReturn(mockDtoList.size());

            ResponseEntity<Object> response = projectController.countAllProjects();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, mockDtoList.size());
            assertEquals(mockDtoList.size(), response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testCountAllProjects_Empty_ValidToken(){
            // mockDtoList is empty
            List<ProjectDTO> mockDtoList = new ArrayList<>();

            when(projectService.getCountAllProjects()).thenReturn(mockDtoList.size());

            ResponseEntity<Object> response = projectController.countAllProjects();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, mockDtoList.size());
            assertEquals(mockDtoList.size(), response.getBody());
        }
    }

    @Nested
    class CountAllProjectsByRoleTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testCountAllProjectsByRole_NotEmpty_ValidToken(){
            List<ProjectDTO> mockDtoList = new ArrayList<>();
            ProjectDTO projectDTO1 = new ProjectDTO();
            projectDTO1.setProjectName("p1");

            ProjectDTO projectDTO2 = new ProjectDTO();
            projectDTO2.setProjectName("p2");

            mockDtoList.add(projectDTO1);
            mockDtoList.add(projectDTO2);

            when(projectService.getCountAllProjectsByRole(EnumRole.ADMIN)).thenReturn(mockDtoList.size());

            ResponseEntity<Object> response = projectController.countAllProjectsByRole(EnumRole.ADMIN.getEnumRole());

            assertEquals(2, mockDtoList.size());
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testCountAllProjectsByRole_Empty_ValidToken(){
            when(projectService.getCountAllProjectsByRole(EnumRole.ADMIN)).thenReturn(0);

            String role = EnumRole.ADMIN.getEnumRole();

            ResponseEntity<Object> response = projectController.countAllProjectsByRole(role);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }
    }

    @Nested
    class CountAllProjectsByUserIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testCountAllProjectsByUserId_NotEmpty_ValidToken(){

            List<ProjectDTO> mockDtoList = new ArrayList<>();
            ProjectDTO projectDTO1 = new ProjectDTO();
            projectDTO1.setProjectName("p1");

            ProjectDTO projectDTO2 = new ProjectDTO();
            projectDTO2.setProjectName("p2");

            mockDtoList.add(projectDTO1);
            mockDtoList.add(projectDTO2);

            when(projectService.getCountAllProjectsByUserId(1L)).thenReturn(mockDtoList.size());

            ResponseEntity<Object> response = projectController.countAllProjectsByUserId(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody());
            assertEquals(2, mockDtoList.size());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testCountAllProjectsByUserId_Empty_ValidToken(){

            when(projectService.getCountAllProjectsByUserId(1L)).thenReturn(0);

            ResponseEntity<Object> response = projectController.countAllProjectsByUserId(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }
    }

    @Nested
    class CountAllUsersByProjectIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
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

            when(projectService.getCountAllUsersByProjectId(1L)).thenReturn(mockDtoList.size());

            ResponseEntity<Object> response = projectController.countAllUsersByProjectId(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testCountAllUsersByProjectId_Empty_ValidToken(){
            when(projectService.getCountAllUsersByProjectId(1L)).thenReturn(0);

            ResponseEntity<Object> response = projectController.countAllUsersByProjectId(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }
    }

    @Nested
    class CountAllUsersByProjectIdByRoleTest {
        @Test
        @DisplayName("Testing success case with valid token")
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

            when(projectService.getCountAllUsersByProjectIdAndRole(1L, EnumRole.ADMIN)).thenReturn(mockDtoList.size());

            ResponseEntity<Object> response = projectController.countAllUsersByProjectIdByRole(1L, role);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testCountAllUsersByProjectIdByRole_Empty_ValidToken(){
            String role = EnumRole.ADMIN.getEnumRole();

            when(projectService.getCountAllUsersByProjectIdAndRole(1L, EnumRole.ADMIN)).thenReturn(0);

            ResponseEntity<Object> response = projectController.countAllUsersByProjectIdByRole(1L, role);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }
    }

    @Nested
    class CountAllActiveProjectsTest {
        @Test
        @DisplayName("Testing success case with valid token")
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

            when(projectService.getCountAllActiveProjects()).thenReturn(mockDtoList.size());

            ResponseEntity<Object> response = projectController.countAllActiveProjects();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testCountAllActiveProjects_Empty_ValidToken(){

            when(projectService.getCountAllActiveProjects()).thenReturn(0);

            ResponseEntity<Object> response = projectController.countAllActiveProjects();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }
    }

    @Nested
    class CountAllInActiveProjectsTest {
        @Test
        @DisplayName("Testing success case with valid token")
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

            when(projectService.getCountAllInActiveProjects()).thenReturn(mockDtoList.size());

            ResponseEntity<Object> response = projectController.countAllInActiveProjects();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testCountAllInactiveProjects_Empty_ValidToken(){
            when(projectService.getCountAllInActiveProjects()).thenReturn(0);

            ResponseEntity<Object> response = projectController.countAllInActiveProjects();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }
    }

    @Nested
    class GetProjectDetailsByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
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

            when(projectService.getProjectDetailsById(1L)).thenReturn(projectDTO);

            ResponseEntity<Object> response = projectController.getProjectDetailsById(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(projectDTO, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetProjectDetailsById_Empty_ValidToken(){
            ProjectDTO projectDTO = new ProjectDTO();

            when(projectService.getProjectDetailsById(1L)).thenReturn(projectDTO);

            ResponseEntity<Object> response = projectController.getProjectDetailsById(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("Testing failure case (internal server error)")
        void testGetProjectDetailsById_InternalServerError() {
            Long projectId = 1L;

            when(projectService.getProjectDetailsById(projectId)).thenThrow(new RuntimeException("Something went wrong"));

            ResponseEntity<Object> response = projectController.getProjectDetailsById(projectId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }
}
