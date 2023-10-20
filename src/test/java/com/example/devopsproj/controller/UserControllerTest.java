package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.dto.responsedto.UserProjectsDTO;
import com.example.devopsproj.model.User;
import com.example.devopsproj.service.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class SaveUserTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testSaveUser_ValidToken(){
            UserCreationDTO userCreationDTO = new UserCreationDTO();

            User user = new User();
            user.setName("Ram");

            when(userService.saveUser(userCreationDTO)).thenReturn(user);

            ResponseEntity<Object> response = userController.saveUser(userCreationDTO);

            User user1 = (User) response.getBody();

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assert user1 != null;
            assertEquals("Ram", user1.getName());
        }
    }

    @Nested
    class GetUserByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetUserById_Successful() {
            Long userId = 1L;

            User user = new User();
            user.setId(userId);
            user.setName("John Doe");
            user.setEmail("john@example.com");
            user.setEnumRole(EnumRole.USER);
            user.setLastUpdated(LocalDateTime.now());
            user.setLastLogout(LocalDateTime.now());

            when(userService.getUserById(userId)).thenReturn(Optional.of(user));

            ResponseEntity<Object> response = userController.getUserById(userId);

            assertAll("All Assertions",
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                    () -> assertNotNull(response.getBody(), "Response body should not be null"),
                    () -> {
                        UserDTO userDTO = (UserDTO) response.getBody();
                        assert userDTO != null;
                        assertEquals(user.getId(), userDTO.getId(), "User ID should match");
                        assertEquals(user.getName(), userDTO.getName(), "User name should match");
                        assertEquals(user.getEmail(), userDTO.getEmail(), "User email should match");
                        assertEquals(user.getEnumRole(), userDTO.getEnumRole(), "User role should match");
                        assertEquals(user.getLastUpdated(), userDTO.getLastUpdated(), "User last updated should match");
                        assertEquals(user.getLastLogout(), userDTO.getLastLogout(), "User last logout should match");
                    }
            );
        }

        @Test
        @DisplayName("Testing user not found case")
        void testGetUserById_UserNotFound() {
            Long userId = 1L;

            when(userService.getUserById(userId)).thenReturn(Optional.empty());

            ResponseEntity<Object> response = userController.getUserById(userId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Nested
    class UpdateUserTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testUpdateUser_ValidToken(){
            Long userId = 1L;

            UserDTO userDTO = new UserDTO();
            userDTO.setEmail("johndoe1@gmail.com");

            when(userService.updateUser(userId, userDTO)).thenReturn(userDTO);

            ResponseEntity<Object> response = userController.updateUser(userId,userDTO);

            UserDTO user1 = (UserDTO)response.getBody();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assert user1 != null;
            assertEquals("johndoe1@gmail.com", user1.getEmail());
        }
    }

    @Nested
    class DeleteUserByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testDeleteUserById_SuccessfulDeletion() {
            Long userId = 1L;

            when(userService.existsById(userId)).thenReturn(true);
            when(userService.existsByIdIsDeleted(userId)).thenReturn(false);
            when(userService.softDeleteUser(userId)).thenReturn(true);

            ResponseEntity<String> response = userController.deleteUserById(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User successfully deleted", response.getBody());
        }

        @Test
        @DisplayName("Testing user not found case")
        void testDeleteUserById_UserNotFound() {
            Long userId = 1L;

            when(userService.existsById(userId)).thenReturn(false);

            ResponseEntity<String> response = userController.deleteUserById(userId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing user already deleted case")
        void testDeleteUserById_UserAlreadySoftDeleted() {
            Long userId = 1L;

            when(userService.existsById(userId)).thenReturn(true);
            when(userService.existsByIdIsDeleted(userId)).thenReturn(true);

            ResponseEntity<String> response = userController.deleteUserById(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User doesn't exist", response.getBody());
        }

        @Test
        @DisplayName("Testing user not deleted case")
        void testDeleteUserById_UserNotDeleted() {
            Long userId = 1L;

            when(userService.existsById(userId)).thenReturn(true);
            when(userService.existsByIdIsDeleted(userId)).thenReturn(false);

            ResponseEntity<String> response = userController.deleteUserById(userId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    class GetUserByRoleIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetUserByRoleId_Successful() {
            String role = "USER";

            EnumRole userRole = EnumRole.USER;
            List<User> users = Arrays.asList(new User(), new User());
            when(userService.getUsersByRole(userRole)).thenReturn(users);

            ResponseEntity<Object> response = userController.getUserByRoleId(role);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody() instanceof List<?>);
            assertEquals(users.size(), ((List<?>) response.getBody()).size());
        }
    }

    @Nested
    class GetCountAllUsersTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetCountAllUsers_ValidToken_NotEmpty(){
            List<User> userList = new ArrayList<>();
            User user1 = new User();
            User user2 = new User();
            User user3 = new User();
            userList.add(user1);
            userList.add(user2);
            userList.add(user3);

            when(userService.getCountAllUsers()).thenReturn(userList.size());

            ResponseEntity<Object> response = userController.getCountAllUsers();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(3, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetCountAllUsers_ValidToken_Empty(){
            // userList is empty
            List<User> userList = new ArrayList<>();

            when(userService.getCountAllUsers()).thenReturn(userList.size());

            ResponseEntity<Object> response = userController.getCountAllUsers();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }
    }

    @Nested
    class GetCountAllUsersByRoleTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetCountAllUsersByRole_ValidToken_NotEmpty(){
            String role = EnumRole.ADMIN.getEnumRole();

            List<User> userList = new ArrayList<>();
            User user1 = new User();
            User user2 = new User();
            User user3 = new User();
            userList.add(user1);
            userList.add(user2);
            userList.add(user3);

            when(userService.getCountAllUsersByRole(EnumRole.ADMIN)).thenReturn(userList.size());

            ResponseEntity<Object> response = userController.getCountAllUsersByRole(role);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(3, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetCountAllUsersByRole_ValidToken_Empty(){
            String role = EnumRole.USER.getEnumRole();

            when(userService.getCountAllUsersByRole(EnumRole.USER)).thenReturn(0); //user list is empty = 0

            ResponseEntity<Object> response = userController.getCountAllUsersByRole(role);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }
    }

    @Nested
    class GetCountAllUsersByProjectIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetCountAllUsersByProjectId_ValidToken_NotEmpty(){
            Long projectId = 1L;

            List<User> userList = new ArrayList<>();
            User user1 = new User();
            User user2 = new User();
            userList.add(user1);
            userList.add(user2);

            when(userService.getCountAllUsersByProjectId(projectId)).thenReturn(userList.size());

            ResponseEntity<Object> response = userController.getCountAllUsersByProjectId(projectId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetCountAllUsersByProjectId_ValidToken_Empty(){
            Long projectId = 1L;

            when(userService.getCountAllUsersByProjectId(projectId)).thenReturn(0); //list is empty = 0

            ResponseEntity<Object> response = userController.getCountAllUsersByProjectId(projectId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }
    }

    @Nested
    class GetAllProjectsByUserIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllProjectsByUserId_ValidToken_NotEmpty(){
            Long userId = 1L;

            List<ProjectDTO> projectList = new ArrayList<>();
            ProjectDTO project1 = new ProjectDTO();
            ProjectDTO project2 = new ProjectDTO();
            projectList.add(project1);
            projectList.add(project2);

            when(userService.getAllProjectsAndRepositoriesByUserId(userId)).thenReturn(projectList);

            ResponseEntity<Object> response = userController.getAllProjectsByUserId(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(projectList, response.getBody());
        }

        @Test
        @DisplayName("Testing empty projects list case")
        void testGetAllProjectsByUserId_ValidToken_Empty(){
            Long userId = 1L;

            //projectList is empty
            List<ProjectDTO> projectList = new ArrayList<>();

            when(userService.getAllProjectsAndRepositoriesByUserId(userId)).thenReturn(projectList);

            ResponseEntity<Object> response = userController.getAllProjectsByUserId(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(projectList, response.getBody());
        }
    }

    @Nested
    class GetProjectsByRoleIdAndUserIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetProjectsByRoleIdAndUserId_ValidToken_NotEmpty(){
            Long userId = 1L;
            String role = EnumRole.USER.getEnumRole();

            List<ProjectDTO> projectDTOList = new ArrayList<>();
            //projectDTOList is not empty
            projectDTOList.add(new ProjectDTO(1L, "Project1", "Description1",
                    null, null, null));
            ResponseEntity<Object> mockResponse = ResponseEntity.ok(projectDTOList);

            when(userService.getProjectsByRoleAndUserId(userId, role)).thenReturn(mockResponse);

            ResponseEntity<Object> response = userController.getProjectsByRoleIdAndUserId(userId, role);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(projectDTOList, response.getBody());

        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetProjectsByRoleIdAndUserId_ValidToken_Empty(){
            Long userId = 1L;
            String role = EnumRole.USER.getEnumRole();

            ResponseEntity<Object> mockResponse = ResponseEntity.noContent().build();

            when(userService.getProjectsByRoleAndUserId(userId, role)).thenReturn(mockResponse);

            ResponseEntity<Object> response = userController.getProjectsByRoleIdAndUserId(userId, role);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Nested
    class GetAllUsersTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllUsers_ValidToken(){
            List<UserDTO> userDTOList = new ArrayList<>();
            UserDTO user1 = new UserDTO();
            UserDTO user2 = new UserDTO();
            userDTOList.add(user1);
            userDTOList.add(user2);

            when(userService.getAllUsers()).thenReturn(userDTOList);

            ResponseEntity<Object> response = userController.getAllUsers();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(userDTOList, response.getBody());
        }
    }

    @Nested
    class GetAllUsersWithProjectsTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllUsersWithProjects_ValidToken(){
            List<UserProjectsDTO> userProjectsDTOList = new ArrayList<>();
            UserProjectsDTO user1 = new UserProjectsDTO();
            userProjectsDTOList.add(user1);

            when(userService.getAllUsersWithProjects()).thenReturn(userProjectsDTOList);

            ResponseEntity<Object> response = userController.getAllUsersWithProjects();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(userProjectsDTOList, response.getBody());
        }
    }

    @Nested
    class GetUsersWithMultipleProjectsTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetUsersWithMultipleProjects_ValidToken(){
            List<UserProjectsDTO> userProjectsDTOList = new ArrayList<>();
            UserProjectsDTO user1 = new UserProjectsDTO();
            UserProjectsDTO user2 = new UserProjectsDTO();
            userProjectsDTOList.add(user1);
            userProjectsDTOList.add(user2);

            when(userService.getUsersWithMultipleProjects()).thenReturn(userProjectsDTOList);

            ResponseEntity<Object> response = userController.getUsersWithMultipleProjects();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(userProjectsDTOList, response.getBody());
        }
    }

    @Nested
    class GetUserWithoutProjectTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetUserWithoutProject_ValidToken(){
            Long projectId = 1L;

            List<UserDTO> userDTOList = new ArrayList<>();
            UserDTO user1 = new UserDTO();
            UserDTO user2 = new UserDTO();
            userDTOList.add(user1);
            userDTOList.add(user2);

            when(userService.getAllUsersWithoutProjects(EnumRole.ADMIN, projectId)).thenReturn(userDTOList);

            ResponseEntity<Object> response = userController.getUserWithoutProject(EnumRole.ADMIN.getEnumRole(), projectId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(userDTOList, response.getBody());
        }
    }

    @Nested
    class UserLogoutTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testUserLogout_Success_ValidToken(){
            String response1 = "User logged out successfully";
            when(userService.userLogout(1L)).thenReturn(response1);

            ResponseEntity<String> response = userController.userLogout(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User logged out successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing unsuccessful logout case")
        void testUserLogout_Failure_ValidToken(){
            String response1 = "Log out unsuccessful";
            when(userService.userLogout(1L)).thenReturn(response1);

            ResponseEntity<String> response = userController.userLogout(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Log out unsuccessful", response.getBody());
        }
    }
}
