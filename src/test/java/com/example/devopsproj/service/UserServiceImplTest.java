package com.example.devopsproj.service;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.dto.responsedto.UserProjectsDTO;
import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
import com.example.devopsproj.service.implementations.ProjectServiceImpl;
import com.example.devopsproj.service.implementations.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectServiceImpl projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private JwtServiceImpl jwtServiceImpl;
    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser_Success(){
        UserCreationDTO userCreationDTO = new UserCreationDTO();
        userCreationDTO.setId(1L);
        userCreationDTO.setName("John Doe");
        userCreationDTO.setEmail("johndoe@example.com");
        userCreationDTO.setEnumRole(EnumRole.USER);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John Doe");
        savedUser.setEmail("johndoe@example.com");
        savedUser.setEnumRole(EnumRole.USER);
        savedUser.setLastUpdated(LocalDateTime.now());
        savedUser.setLastLogout(LocalDateTime.now());

        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setId(userCreationDTO.getId());
        expectedDTO.setName(userCreationDTO.getName());
        expectedDTO.setEmail(userCreationDTO.getEmail());
        expectedDTO.setEnumRole(userCreationDTO.getEnumRole());
        expectedDTO.setLastLogout(LocalDateTime.now());
        expectedDTO.setLastUpdated(LocalDateTime.now());

        when(userRepository.save(savedUser)).thenReturn(savedUser);
        when(modelMapper.map(any(), eq(UserDTO.class))).thenReturn(expectedDTO);

        UserDTO userDTO = userService.saveUser(userCreationDTO);

        assertEquals(expectedDTO, userDTO);
    }

    @Nested
    class UpdateUserTest {
        @Test
        @DisplayName("Testing success case for updating user")
        void testUpdateUser_Success() {
            Long userId = 1L;

            UserDTO updatedUserDTO = new UserDTO();
            updatedUserDTO.setName("Updated Name");
            updatedUserDTO.setEnumRole(EnumRole.ADMIN);

            User existingUser = new User();
            existingUser.setId(userId);
            existingUser.setName("Original Name");
            existingUser.setEnumRole(EnumRole.USER);

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

            User updatedUser = new User();
            updatedUser.setId(userId);
            updatedUser.setName(updatedUserDTO.getName());
            updatedUser.setEnumRole(updatedUserDTO.getEnumRole());
            updatedUser.setLastUpdated(LocalDateTime.now());

            when(userRepository.save(existingUser)).thenReturn(updatedUser);

            UserDTO resultUserDTO = userService.updateUser(userId, updatedUserDTO);

            assertEquals(updatedUserDTO.getName(), resultUserDTO.getName());
            assertEquals(updatedUserDTO.getEnumRole(), resultUserDTO.getEnumRole());
            assertNotNull(resultUserDTO.getLastUpdated());
        }

        @Test
        @DisplayName("Testing user not found case")
        void testUpdateUser_UserNotFound() {
            Long userId = 1L;

            UserDTO updatedUserDTO = new UserDTO();
            updatedUserDTO.setName("Updated Name");
            updatedUserDTO.setEnumRole(EnumRole.ADMIN);

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // Attempt to update a non-existent user should throw EntityNotFoundException
            assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userId, updatedUserDTO));
        }
    }

    @Nested
    class GetUserByIdTest {
        @Test
        @DisplayName("Testing success case - user found")
        void testGetUserById_UserFound() {
            Long userId = 1L;

            User mockUser = new User();
            mockUser.setId(userId);
            mockUser.setName("John Doe");
            mockUser.setEmail("johndoe@example.com");
            mockUser.setEnumRole(EnumRole.USER);

            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

            Optional<User> result = userService.getUserById(userId);

            assertTrue(result.isPresent());
            assertEquals(mockUser, result.get());
        }

        @Test
        @DisplayName("Testing user not found case")
        void testGetUserById_UserNotFound() {
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            Optional<User> result = userService.getUserById(userId);

            assertFalse(result.isPresent());
        }
    }

    @Nested
    class ExistsByIdIsDeletedTest {
        @Test
        @DisplayName("Testing success case - user present")
        void testExistsByIdIsDeleted_UserFoundNotDeleted() {
            Long userId = 1L;

            User mockUser = new User();
            mockUser.setId(userId);
            mockUser.setDeleted(false); // User is not deleted

            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

            boolean result = userService.existsByIdIsDeleted(userId);

            assertFalse(result);
        }

        @Test
        @DisplayName("Testing user not found case")
        void testExistsByIdIsDeleted_UserNotFoundOrDeleted() {
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            boolean result = userService.existsByIdIsDeleted(userId);

            assertTrue(result);
        }
    }

    @Nested
    class SoftDeleteUserTest {
        @Test
        @DisplayName("Testing success case for soft delete")
        void testSoftDeleteUser_Successful() {
            Long userId = 1L;

            doNothing().when(userRepository).softDelete(userId);

            boolean result = userService.softDeleteUser(userId);

            assertTrue(result);
        }

        @Test
        @DisplayName("Testing failure case")
        void testSoftDeleteUser_Failure() {
            Long userId = 1L;

            doThrow(DataIntegrityViolationException.class).when(userRepository).softDelete(userId);

            boolean result = userService.softDeleteUser(userId);

            assertFalse(result);
        }
    }

    @Nested
    class ExistsByIdTest {
        @Test
        @DisplayName("Testing success case - user exists")
        void testExistsById_EntityExists() {
            Long existingUserId = 1L;

            when(userRepository.existsById(existingUserId)).thenReturn(true);

            boolean result = userService.existsById(existingUserId);

            assertTrue(result);
        }

        @Test
        @DisplayName("Testing failure case - user does not exist")
        void testExistsById_EntityDoesNotExist() {
            Long nonExistingUserId = 2L;

            when(userRepository.existsById(nonExistingUserId)).thenReturn(false);

            boolean result = userService.existsById(nonExistingUserId);

            assertFalse(result);
        }
    }

    @Nested
    class GetUsersByRoleTest {
        @Test
        @DisplayName("Testing success case - user matches role")
        void testGetUsersByRole_WithMatchingUsers() {
            EnumRole testRole = EnumRole.USER;

            List<User> usersWithRole = new ArrayList<>();
            User user1 = new User();
            user1.setId(1L);
            user1.setName("User 1");
            user1.setEmail("user1@example.com");
            user1.setEnumRole(testRole);
            usersWithRole.add(user1);

            User user2 = new User();
            user2.setId(2L);
            user2.setName("User 2");
            user2.setEmail("user2@example.com");
            user2.setEnumRole(testRole);
            usersWithRole.add(user2);

            when(userRepository.findByRole(testRole)).thenReturn(usersWithRole);

            List<User> result = userService.getUsersByRole(testRole);

            assertEquals(usersWithRole, result);
        }

        @Test
        @DisplayName("Testing failure case - empty list")
        void testGetUsersByRole_WithNoMatchingUsers() {
            EnumRole testRole = EnumRole.ADMIN;

            when(userRepository.findByRole(testRole)).thenReturn(Collections.emptyList());

            List<User> result = userService.getUsersByRole(testRole);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testGetCountAllUsers() {
        int expectedCount = 10;

        when(userRepository.countAllUsers()).thenReturn(expectedCount);

        int result = userService.getCountAllUsers();

        assertEquals(expectedCount, result);
    }

    @Test
    void testGetCountAllUsersByRole() {
        int expectedCount = 5;
        EnumRole role = EnumRole.USER;

        when(userRepository.countAllUsersByRole(role)).thenReturn(expectedCount);

        int result = userService.getCountAllUsersByRole(role);

        assertEquals(expectedCount, result);
    }

    @Nested
    class GetCountAllUsersByProjectIdTest {
        @Test
        @DisplayName("Testing success case - project exists")
        void testGetCountAllUsersByProjectId_ProjectExists() {
            int expectedCount = 5;
            Long projectId = 1L;

            Project mockProject = new Project();
            mockProject.setProjectId(projectId);

            when(projectService.getProjectById(projectId)).thenReturn(Optional.of(mockProject));
            when(userRepository.countAllUsersByProjectId(projectId)).thenReturn(expectedCount);

            int result = userService.getCountAllUsersByProjectId(projectId);

            assertEquals(expectedCount, result);
        }

        @Test
        @DisplayName("Testing project does not exist case")
        void testGetCountAllUsersByProjectId_ProjectDoesNotExist() {
            Long projectId = 1L;

            when(projectService.getProjectById(projectId)).thenReturn(Optional.empty());

            int result = userService.getCountAllUsersByProjectId(projectId);

            assertEquals(0, result);
        }
    }

    @Test
    void testGetAllUsersWithProjects() {
        List<User> users = new ArrayList<>();
        List<Project> projects1 = new ArrayList<>();
        List<Project> projects2 = new ArrayList<>();

        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        user1.setEnumRole(EnumRole.USER);

        Project project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("Project A");
        project1.setDeleted(false);

        Project project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project B");
        project2.setDeleted(true);

        projects1.add(project1);
        projects1.add(project2);

        user1.setProjects(projects1);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        user2.setEnumRole(EnumRole.ADMIN);

        Project project3 = new Project();
        project3.setProjectId(3L);
        project3.setProjectName("Project C");
        project3.setDeleted(false);

        projects2.add(project3);

        user2.setProjects(projects2);

        users.add(user1);
        users.add(user2);

        when(userRepository.findAllUsers()).thenReturn(users);

        List<UserProjectsDTO> result = userService.getAllUsersWithProjects();

        assertEquals(2, result.size());

        UserProjectsDTO userProjectsDTO1 = result.get(0);
        assertEquals(1L, userProjectsDTO1.getUserId());
        assertEquals("User 1", userProjectsDTO1.getUserName());
        assertEquals(List.of("Project A"), userProjectsDTO1.getProjectNames());

        UserProjectsDTO userProjectsDTO2 = result.get(1);
        assertEquals(2L, userProjectsDTO2.getUserId());
        assertEquals("User 2", userProjectsDTO2.getUserName());
        assertEquals(List.of("Project C"), userProjectsDTO2.getProjectNames());
    }

    @Nested
    class GetAllUsersWithoutProjectsTest {
        @Test
        @DisplayName("Testing no user test case")
        void testGetAllUsersWithoutProjects_NoUsers() {
            EnumRole role = EnumRole.USER;
            Long projectId = 1L;

            when(userRepository.findAllUsersByRole(role)).thenReturn(Collections.emptyList());

            List<UserDTO> result = userService.getAllUsersWithoutProjects(role, projectId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Testing success case - users list")
        void testGetAllUsersWithoutProjects_UsersWithoutProject() {
            EnumRole role = EnumRole.USER;
            Long projectId = 1L;

            User userWithoutProject = new User();
            userWithoutProject.setId(2L);
            userWithoutProject.setName("User Without Project");
            userWithoutProject.setEmail("userWithoutProject@example.com");
            userWithoutProject.setEnumRole(role);
            userWithoutProject.setProjects(new ArrayList<>());

            when(userRepository.findAllUsersByRole(role)).thenReturn(Collections.singletonList(userWithoutProject));

            List<UserDTO> result = userService.getAllUsersWithoutProjects(role, projectId);

            assertNotNull(result);
            assertEquals(1, result.size());

            UserDTO userDTO = result.get(0);
            assertEquals(userWithoutProject.getId(), userDTO.getId());
            assertEquals(userWithoutProject.getName(), userDTO.getName());
            assertEquals(userWithoutProject.getEmail(), userDTO.getEmail());
            assertEquals(role, userDTO.getEnumRole());
        }

        @Test
        @DisplayName("Testing no users without project case")
        void testGetAllUsersWithoutProjects_NoUsersWithoutProject() {
            EnumRole role = EnumRole.USER;
            Long projectId = 1L;

            User userWithProject = new User();
            userWithProject.setId(1L);
            userWithProject.setName("User With Project");
            userWithProject.setEmail("userWithProject@gmail.com");
            userWithProject.setEnumRole(role);

            Project project = new Project();
            project.setProjectId(projectId);
            userWithProject.setProjects(Collections.singletonList(project));

            when(userRepository.findAllUsersByRole(role)).thenReturn(Collections.singletonList(userWithProject));

            List<UserDTO> result = userService.getAllUsersWithoutProjects(role, projectId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetUsersWithMultipleProjectsTest {
        @Test
        @DisplayName("Testing no users case")
        void testGetUsersWithMultipleProjects_NoUsers() {
            when(projectRepository.findAllProjects()).thenReturn(Collections.emptyList());

            List<UserProjectsDTO> result = userService.getUsersWithMultipleProjects();

            assertEquals(Collections.emptyList(), result);
        }

        @Test
        @DisplayName("Testing no multiple projects case")
        void testGetUsersWithMultipleProjects_NoMultipleProjects() {
            Project project1 = new Project();
            project1.setProjectName("Project1");

            Project project2 = new Project();
            project2.setProjectName("Project2");

            List<Project> projects = Arrays.asList(project1, project2);

            when(projectRepository.findAllProjects()).thenReturn(projects);

            List<UserProjectsDTO> result = userService.getUsersWithMultipleProjects();

            assertEquals(Collections.emptyList(), result);
        }
    }

    @Nested
    class GetAllUsersTest {
        @Test
        @DisplayName("Testing success case for getting all users")
        void testGetAllUsers_WithData() {
            List<User> users = new ArrayList<>();
            User user1 = new User();
            user1.setId(1L);
            user1.setName("User 1");
            user1.setEmail("user1@gmail.com");
            user1.setEnumRole(EnumRole.USER);

            User user2 = new User();
            user2.setId(2L);
            user2.setName("User 2");
            user2.setEmail("user2@gmail.com");
            user2.setEnumRole(EnumRole.ADMIN);

            users.add(user1);
            users.add(user2);

            when(userRepository.findAll()).thenReturn(users);

            List<UserDTO> result = userService.getAllUsers();

            assertNotNull(result);
            assertEquals(2, result.size());

            UserDTO userDTO1 = result.get(0);
            assertEquals(user1.getId(), userDTO1.getId());
            assertEquals(user1.getName(), userDTO1.getName());
            assertEquals(user1.getEmail(), userDTO1.getEmail());
            assertEquals(user1.getEnumRole(), userDTO1.getEnumRole());

            UserDTO userDTO2 = result.get(1);
            assertEquals(user2.getId(), userDTO2.getId());
            assertEquals(user2.getName(), userDTO2.getName());
            assertEquals(user2.getEmail(), userDTO2.getEmail());
            assertEquals(user2.getEnumRole(), userDTO2.getEnumRole());
        }

        @Test
        @DisplayName("Testing empty users list case")
        void testGetAllUsers_EmptyData() {
            List<User> users = new ArrayList<>();

            when(userRepository.findAll()).thenReturn(users);

            List<UserDTO> result = userService.getAllUsers();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetProjectsByRoleAndUserIdTest {
        @Test
        @DisplayName("Testing success case - projects found")
        void testGetProjectsByRoleAndUserId_ProjectsFound() {
            Long userId = 1L;
            String role = "USER";
            EnumRole userRole = EnumRole.USER;
            List<Project> projects = new ArrayList<>();

            Project project1 = new Project();
            project1.setProjectId(101L);
            project1.setProjectName("Project A");

            GitRepository repo1 = new GitRepository();
            repo1.setRepoId(1001L);
            repo1.setName("Repo 1");

            project1.setRepositories(Collections.singletonList(repo1));
            projects.add(project1);

            when(userRepository.findByRoleAndUserId(userId, userRole)).thenReturn(projects);

            ResponseEntity<Object> response = userService.getProjectsByRoleAndUserId(userId, role);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            List<ProjectDTO> projectDTOList = (List<ProjectDTO>) response.getBody();
            assertNotNull(projectDTOList);
            assertEquals(projects.size(), projectDTOList.size());
        }

        @Test
        @DisplayName("Testing no projects found case")
        void testGetProjectsByRoleAndUserId_NoProjectsFound() {
            Long userId = 2L;
            String role = "USER";
            EnumRole userRole = EnumRole.USER;
            List<Project> projects = new ArrayList<>();

            when(userRepository.findByRoleAndUserId(userId, userRole)).thenReturn(projects);

            ResponseEntity<Object> response = userService.getProjectsByRoleAndUserId(userId, role);

            assertNotNull(response);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Testing valid Figma and GDrive case")
        void testGetProjectsByRoleAndUserIdWithFigmaAndDrive() {
            User user = new User();
            user.setId(1L);
            EnumRole userRole = EnumRole.USER;
            Project project = new Project();
            Figma figma = new Figma();
            figma.setFigmaURL("https://example.com/figma");
            project.setFigma(figma);
            project.setRepositories(new ArrayList<>());
            GoogleDrive googleDrive = new GoogleDrive();
            googleDrive.setDriveLink("https://example.com/drive");
            project.setGoogleDrive(googleDrive);
            List<Project> projects = Collections.singletonList(project);

            when(userRepository.findByRoleAndUserId(1L, userRole)).thenReturn(projects);

            ResponseEntity<Object> response = userService.getProjectsByRoleAndUserId(1L, "user");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("Testing null Figma and null GDrive case")
        void testGetProjectsByRoleAndUserIdWithNullFigmaAndDrive() {
            User user = new User();
            user.setId(1L);
            EnumRole userRole = EnumRole.USER;
            Project project = new Project();
            project.setRepositories(new ArrayList<>());
            List<Project> projects = Collections.singletonList(project);

            when(userRepository.findByRoleAndUserId(1L, userRole)).thenReturn(projects);

            ResponseEntity<Object> response = userService.getProjectsByRoleAndUserId(1L, "user");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Nested
    class GetAllProjectsAndRepositoriesByUserIdTest {
        @Test
        @DisplayName("Testing success case")
        void testGetAllProjectsAndRepositoriesByUserId() {
            User user = new User();
            user.setId(1L);
            user.setName("John");
            user.setProjects(new ArrayList<>());

            Project project1 = new Project();
            project1.setProjectId(101L);
            project1.setProjectName("Project A");
            project1.setRepositories(new ArrayList<>());

            Project project2 = new Project();
            project2.setProjectId(102L);
            project2.setProjectName("Project B");
            project2.setRepositories(new ArrayList<>());

            GitRepository repo1 = new GitRepository();
            repo1.setRepoId(201L);
            repo1.setName("Repo 1");

            GitRepository repo2 = new GitRepository();
            repo2.setRepoId(202L);
            repo2.setName("Repo 2");

            project1.getRepositories().add(repo1);
            project1.getRepositories().add(repo2);

            user.getProjects().add(project1);
            user.getProjects().add(project2);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            List<ProjectDTO> result = userService.getAllProjectsAndRepositoriesByUserId(1L);

            assertNotNull(result);
            assertEquals(2, result.size());

            ProjectDTO projectDTO1 = result.get(0);
            assertEquals(101L, projectDTO1.getProjectId());
            assertEquals("Project A", projectDTO1.getProjectName());
            assertEquals(2, projectDTO1.getRepositories().size());

            ProjectDTO projectDTO2 = result.get(1);
            assertEquals(102L, projectDTO2.getProjectId());
            assertEquals("Project B", projectDTO2.getProjectName());
            assertEquals(0, projectDTO2.getRepositories().size());

            GitRepositoryDTO repoDTO1 = projectDTO1.getRepositories().get(0);
            assertEquals(201L, repoDTO1.getRepoId());
            assertEquals("Repo 1", repoDTO1.getName());

            GitRepositoryDTO repoDTO2 = projectDTO1.getRepositories().get(1);
            assertEquals(202L, repoDTO2.getRepoId());
            assertEquals("Repo 2", repoDTO2.getName());

            verify(userRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Testing non-existing user case")
        void testGetAllProjectsAndRepositoriesByUserId_NonExistingUser() {
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> {
                userService.getAllProjectsAndRepositoriesByUserId(userId);
            });
        }
    }

    @Nested
    class LoginVerificationTest {
        @Test
        @DisplayName("Testing user not found case")
        void testLoginVerification_Success() {
            String email = "test@example.com";
            User user = new User();
            user.setId(1L);
            user.setName("Test User");
            user.setEmail(email);
            user.setEnumRole(EnumRole.USER);
            user.setLastUpdated(LocalDateTime.now());

            when(userRepository.existsByEmail(email)).thenReturn(user);
            when(jwtServiceImpl.generateToken(user)).thenReturn("valid_token");

            UserDTO userDTO = userService.loginVerification(email);

            assertNotNull(userDTO);
            assertEquals(1L, userDTO.getId());
            assertEquals("Test User", userDTO.getName());
            assertEquals(email, userDTO.getEmail());
            assertEquals(EnumRole.USER, userDTO.getEnumRole());
            assertEquals(user.getLastUpdated(), userDTO.getLastUpdated());
            assertEquals("valid_token", userDTO.getToken());
        }


        @Test
        @DisplayName("Testing failure case for non-existent user")
        void testLoginVerification_Unsuccessful_UserNotFound() {
            String userEmail = "nonexistent@example.com";

            when(userRepository.existsByEmail(userEmail)).thenReturn(null);

            UserDTO result = userService.loginVerification(userEmail);

            assertNull(result);
        }
    }

    @Nested
    class UserLogoutTest {
        @Test
        @DisplayName("Testing success case for user logout")
        void testUserLogout_Successful() {
            Long userId = 1L;
            User user = new User();
            user.setId(userId);
            user.setName("John Doe");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            String result = userService.userLogout(userId);

            assertEquals("User logged out successfully", result);
            assertNotNull(user.getLastLogout());
        }

        @Test
        @DisplayName("Testing user not found case - failure")
        void testUserLogout_Unsuccessful_UserNotFound() {
            Long userId = 2L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            String result = userService.userLogout(userId);

            assertEquals("Log out unsuccessful", result);
        }
    }

    @Test
    void testGetUserViaPhoneNumber() {
        String phoneNumber = "1234567890";

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setPhoneNumber(phoneNumber);

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(user);

        User result = userService.getUserViaPhoneNumber(phoneNumber);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    void testGetUserByMail() {
        String userEmail = "johndoe@example.com";

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail(userEmail);
        user.setPhoneNumber("1234567890");

        when(userRepository.findByEmail(userEmail)).thenReturn(user);

        User result = userService.getUserByMail(userEmail);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
    }
}
