package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.dto.responsedto.UserProjectsDTO;
import com.example.devopsproj.model.User;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private JwtServiceImpl jwtService;

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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.saveUser(userCreationDTO)).thenReturn(user);

            ResponseEntity<Object> response = userController.saveUser(userCreationDTO, "valid-access-token");

            User user1 = (User) response.getBody();

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assert user1 != null;
            assertEquals("Ram", user1.getName());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testSaveUser_InvalidToken(){
            UserCreationDTO userCreationDTO = new UserCreationDTO();

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.saveUser(userCreationDTO, "invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class GetUserByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetUserById_Successful() {
            Long userId = 1L;
            String accessToken = "valid_token";

            User user = new User();
            user.setId(userId);
            user.setName("John Doe");
            user.setEmail("john@example.com");
            user.setEnumRole(EnumRole.USER);
            user.setLastUpdated(LocalDateTime.now());
            user.setLastLogout(LocalDateTime.now());

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(userService.getUserById(userId)).thenReturn(Optional.of(user));

            ResponseEntity<Object> response = userController.getUserById(userId, accessToken);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            UserDTO userDTO = (UserDTO) response.getBody();
            assertEquals(user.getId(), userDTO.getId());
            assertEquals(user.getName(), userDTO.getName());
            assertEquals(user.getEmail(), userDTO.getEmail());
            assertEquals(user.getEnumRole(), userDTO.getEnumRole());
            assertEquals(user.getLastUpdated(), userDTO.getLastUpdated());
            assertEquals(user.getLastLogout(), userDTO.getLastLogout());
        }

        @Test
        @DisplayName("Testing user not found case")
        void testGetUserById_UserNotFound() {
            Long userId = 1L;
            String accessToken = "valid_token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(userService.getUserById(userId)).thenReturn(Optional.empty());

            ResponseEntity<Object> response = userController.getUserById(userId, accessToken);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetUserById_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getUserById(1L, "invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.updateUser(userId, userDTO)).thenReturn(userDTO);

            ResponseEntity<Object> response = userController.updateUser(userId,userDTO,"valid-access-token");

            UserDTO user1 = (UserDTO)response.getBody();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assert user1 != null;
            assertEquals("johndoe1@gmail.com", user1.getEmail());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testUpdateUser_InvalidToken(){
            UserDTO userDTO = new UserDTO();

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.updateUser(1L,userDTO,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class DeleteUserByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testDeleteUserById_SuccessfulDeletion() {
            Long userId = 1L;
            String accessToken = "valid_token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(userService.existsById(userId)).thenReturn(true);
            when(userService.existsByIdIsDeleted(userId)).thenReturn(false);
            when(userService.softDeleteUser(userId)).thenReturn(true);

            ResponseEntity<String> response = userController.deleteUserById(userId, accessToken);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User successfully deleted", response.getBody());
        }

        @Test
        @DisplayName("Testing user not found case")
        void testDeleteUserById_UserNotFound() {
            Long userId = 1L;
            String accessToken = "valid_token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(userService.existsById(userId)).thenReturn(false);

            ResponseEntity<String> response = userController.deleteUserById(userId, accessToken);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing user already deleted case")
        void testDeleteUserById_UserAlreadySoftDeleted() {
            Long userId = 1L;
            String accessToken = "valid_token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(userService.existsById(userId)).thenReturn(true);
            when(userService.existsByIdIsDeleted(userId)).thenReturn(true);

            ResponseEntity<String> response = userController.deleteUserById(userId, accessToken);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User doesn't exist", response.getBody());
        }

        @Test
        @DisplayName("Testing user not deleted case")
        void testDeleteUserById_UserNotDeleted() {
            Long userId = 1L;
            String accessToken = "valid_token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(userService.existsById(userId)).thenReturn(true);
            when(userService.existsByIdIsDeleted(userId)).thenReturn(false);

            ResponseEntity<String> response = userController.deleteUserById(userId, accessToken);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testDeleteUserById_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<String> response = userController.deleteUserById(1L,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class GetUserByRoleIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetUserByRoleId_Successful() {
            String role = "USER";
            String accessToken = "valid_token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

            EnumRole userRole = EnumRole.USER;
            List<User> users = Arrays.asList(new User(), new User());
            when(userService.getUsersByRole(userRole)).thenReturn(users);

            ResponseEntity<Object> response = userController.getUserByRoleId(role, accessToken);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody() instanceof List<?>);
            assertEquals(users.size(), ((List<?>) response.getBody()).size());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetUserByRoleId_InvalidToken(){
            String role = EnumRole.USER.getEnumRole();

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getUserByRoleId(role,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getCountAllUsers()).thenReturn(userList.size());

            ResponseEntity<Object> response = userController.getCountAllUsers("valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(3, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetCountAllUsers_ValidToken_Empty(){
            // userList is empty
            List<User> userList = new ArrayList<>();

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getCountAllUsers()).thenReturn(userList.size());

            ResponseEntity<Object> response = userController.getCountAllUsers("valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetCountAllUsers_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getCountAllUsers("invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getCountAllUsersByRole(EnumRole.ADMIN)).thenReturn(userList.size());

            ResponseEntity<Object> response = userController.getCountAllUsersByRole(role, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(3, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetCountAllUsersByRole_ValidToken_Empty(){
            String role = EnumRole.USER.getEnumRole();

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getCountAllUsersByRole(EnumRole.USER)).thenReturn(0); //user list is empty = 0

            ResponseEntity<Object> response = userController.getCountAllUsersByRole(role,"valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetCountAllUsersByRole_InvalidToken(){
            String role = EnumRole.USER.getEnumRole();

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getCountAllUsersByRole(role, "invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getCountAllUsersByProjectId(projectId)).thenReturn(userList.size());

            ResponseEntity<Object> response = userController.getCountAllUsersByProjectId(projectId, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody());
        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetCountAllUsersByProjectId_ValidToken_Empty(){
            Long projectId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getCountAllUsersByProjectId(projectId)).thenReturn(0); //list is empty = 0

            ResponseEntity<Object> response = userController.getCountAllUsersByProjectId(projectId, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetCountAllUsersByProjectId_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getCountAllUsersByProjectId(1L, "invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getAllProjectsAndRepositoriesByUserId(userId)).thenReturn(projectList);

            ResponseEntity<Object> response = userController.getAllProjectsByUserId(userId, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(projectList, response.getBody());
        }

        @Test
        @DisplayName("Testing empty projects list case")
        void testGetAllProjectsByUserId_ValidToken_Empty(){
            Long userId = 1L;

            //projectList is empty
            List<ProjectDTO> projectList = new ArrayList<>();

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getAllProjectsAndRepositoriesByUserId(userId)).thenReturn(projectList);

            ResponseEntity<Object> response = userController.getAllProjectsByUserId(userId, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(projectList, response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetAllProjectsByUserId_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getAllProjectsByUserId(1L, "invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getProjectsByRoleAndUserId(userId, role)).thenReturn(mockResponse);

            ResponseEntity<Object> response = userController.getProjectsByRoleIdAndUserId(userId, role, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(projectDTOList, response.getBody());

        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetProjectsByRoleIdAndUserId_ValidToken_Empty(){
            Long userId = 1L;
            String role = EnumRole.USER.getEnumRole();

            ResponseEntity<Object> mockResponse = ResponseEntity.noContent().build();

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getProjectsByRoleAndUserId(userId, role)).thenReturn(mockResponse);

            ResponseEntity<Object> response = userController.getProjectsByRoleIdAndUserId(userId, role, "valid-access-token");

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetProjectsByRoleIdAndUserId_InvalidToken(){
            String role = EnumRole.USER.getEnumRole();

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getProjectsByRoleIdAndUserId(1L,role, "invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getAllUsers()).thenReturn(userDTOList);

            ResponseEntity<Object> response = userController.getAllUsers("valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(userDTOList, response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetAllUsers_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getAllUsers("invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getAllUsersWithProjects()).thenReturn(userProjectsDTOList);

            ResponseEntity<Object> response = userController.getAllUsersWithProjects("valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(userProjectsDTOList, response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetAllUsersWithProjects_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getAllUsersWithProjects("invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getUsersWithMultipleProjects()).thenReturn(userProjectsDTOList);

            ResponseEntity<Object> response = userController.getUsersWithMultipleProjects("valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(userProjectsDTOList, response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetUsersWithMultipleProjects_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getUsersWithMultipleProjects("invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.getAllUsersWithoutProjects(EnumRole.ADMIN, projectId)).thenReturn(userDTOList);

            ResponseEntity<Object> response = userController.getUserWithoutProject(EnumRole.ADMIN.getEnumRole(), projectId,"valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(userDTOList, response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetUserWithoutProject_InvalidToken(){
            String role = EnumRole.USER.getEnumRole();

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = userController.getUserWithoutProject(role, 1L,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class UserLogoutTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testUserLogout_Success_ValidToken(){
            String response1 = "User logged out successfully";
            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.userLogout(1L)).thenReturn(response1);

            ResponseEntity<String> response = userController.userLogout(1L, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User logged out successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing unsuccessful logout case")
        void testUserLogout_Failure_ValidToken(){
            String response1 = "Log out unsuccessful";
            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(userService.userLogout(1L)).thenReturn(response1);

            ResponseEntity<String> response = userController.userLogout(1L, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Log out unsuccessful", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testUserLogout_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<String> response = userController.userLogout(1L,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }
}
