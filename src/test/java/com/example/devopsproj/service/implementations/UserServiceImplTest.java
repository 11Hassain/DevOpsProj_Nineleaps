package com.example.devopsproj.service.implementations;

import static org.mockito.Mockito.*;
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
import com.example.devopsproj.utils.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
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
    private JwtServiceImpl jwtService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private ModelMapper modelMapper; // Mock the ModelMapper


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testSaveUser() {
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

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User savedUserResult = userService.saveUser(userCreationDTO);

        assertEquals(userCreationDTO.getId(), savedUserResult.getId());
        assertEquals(userCreationDTO.getName(), savedUserResult.getName());
        assertEquals(userCreationDTO.getEmail(), savedUserResult.getEmail());
        assertEquals(userCreationDTO.getEnumRole(), savedUserResult.getEnumRole());
        assertNotNull(savedUserResult.getLastUpdated());
        assertNotNull(savedUserResult.getLastLogout());
    }


    @Test
    void testUpdateUser_NonExistingUser() {
        Long userId = 1L;

        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setName("Updated Name");
        updatedUserDTO.setEnumRole(EnumRole.USER);

        // Mock the userRepository.findById method to return an empty Optional
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Attempt to update a non-existing user should throw EntityNotFoundException
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userId, updatedUserDTO));

        // Verify that userRepository.findById was called
        verify(userRepository, times(1)).findById(userId);

        // Ensure that userRepository.save was not called
        verify(userRepository, never()).save(any());
    }
    @Test
    void testUpdateUser() {
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

        when(userRepository.save((existingUser))).thenReturn(updatedUser);

        UserDTO resultUserDTO = userService.updateUser(userId, updatedUserDTO);

        assertEquals(updatedUserDTO.getName(), resultUserDTO.getName());
        assertEquals(updatedUserDTO.getEnumRole(), resultUserDTO.getEnumRole());
        assertNotNull(resultUserDTO.getLastUpdated());
    }
    @Test
    void testGetUserById_ExistingUser() {
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setName("John Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(expectedUser.getId(), result.get().getId());
        assertEquals(expectedUser.getName(), result.get().getName());
    }

    @Test
    void testGetUserById_NonExistingUser() {
        Long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByIdIsDeleted_UserExistsAndNotDeleted() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setDeleted(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        boolean result = userService.existsByIdIsDeleted(userId);

        assertFalse(result);
    }

    @Test
    void testExistsByIdIsDeleted_UserExistsAndIsDeleted() {
        Long userId = 2L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setDeleted(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        boolean result = userService.existsByIdIsDeleted(userId);

        assertTrue(result);
    }

    @Test
    void testExistsByIdIsDeleted_UserDoesNotExist() {
        Long userId = 3L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userService.existsByIdIsDeleted(userId);

        assertTrue(result);
    }

    @Test
    void testSoftDeleteUser_SuccessfulDeletion() {
        Long userId = 1L;

        // Mock the softDelete method to indicate successful deletion
        doNothing().when(userRepository).softDelete(userId);

        boolean result = userService.softDeleteUser(userId);

        assertTrue(result);
    }

    @Test
    void testSoftDeleteUser_FailedDeletion() {
        Long userId = 2L;

        // Mock the softDelete method to indicate failed deletion
        doThrow(new DataAccessException("Failed to delete") {}).when(userRepository).softDelete(userId);

        boolean result = userService.softDeleteUser(userId);

        assertFalse(result);
    }

    @Test
    void testSoftDeleteUser_ExceptionThrown() {
        Long userId = 3L;

        // Mock the softDelete method to throw an exception
        doThrow(new RuntimeException("Something went wrong")).when(userRepository).softDelete(userId);

        boolean result = userService.softDeleteUser(userId);

        assertFalse(result);
    }
    @Test
    void testExistsById_UserExists() {
        Long userId = 1L;

        // Mock the userRepository.existsById method to indicate the user exists
        when(userRepository.existsById(userId)).thenReturn(true);

        boolean result = userService.existsById(userId);

        assertTrue(result);
    }

    @Test
    void testExistsById_UserDoesNotExist() {
        Long userId = 2L;

        // Mock the userRepository.existsById method to indicate the user does not exist
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = userService.existsById(userId);

        assertFalse(result);
    }

    @Test
    void testExistsById_CustomRepository_UserExists() {
        Long userId = 3L;

        // Mock the custom repository method to indicate the user exists
        when(userRepository.existsById(userId)).thenReturn(true);

        boolean result = userService.existsById(userId);

        assertTrue(result);
    }

    @Test
    void testExistsById_CustomRepository_UserDoesNotExist() {
        Long userId = 4L;

        // Mock the custom repository method to indicate the user does not exist
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = userService.existsById(userId);

        assertFalse(result);
    }

    @Test
    void testGetUsersByRole() {
        // Define the role you want to test
        EnumRole enumRole = EnumRole.USER;

        // Create some sample users with the specified role
        User user1 = new User();
        user1.setId(1L);
        user1.setEnumRole(enumRole);

        User user2 = new User();
        user2.setId(2L);
        user2.setEnumRole(enumRole);

        // Mock the userRepository.findByRole method to return the sample users
        when(userRepository.findByRole(enumRole)).thenReturn(Arrays.asList(user1, user2));

        // Call the getUsersByRole method
        List<User> users = userService.getUsersByRole(enumRole);

        // Assert that the returned list contains the expected users
        assertEquals(2, users.size());
        assertEquals(1L, users.get(0).getId());
        assertEquals(2L, users.get(1).getId());
        assertEquals(enumRole, users.get(0).getEnumRole());
        assertEquals(enumRole, users.get(1).getEnumRole());
    }

    @Test
    void testGetUsersByRole_NoUsersFound() {
        // Define the role you want to test
        EnumRole enumRole = EnumRole.ADMIN;

        // Mock the userRepository.findByRole method to return an empty list
        when(userRepository.findByRole(enumRole)).thenReturn(Arrays.asList());

        // Call the getUsersByRole method
        List<User> users = userService.getUsersByRole(enumRole);

        // Assert that the returned list is empty
        assertTrue(users.isEmpty());
    }

    @Test
    void testGetUserDTOsByRole() {
        // Define the role you want to test
        EnumRole enumRole = EnumRole.USER;

        // Create some sample users with the specified role
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        user1.setEnumRole(enumRole);
        user1.setLastUpdated(LocalDateTime.now());
        user1.setLastLogout(LocalDateTime.now());

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        user2.setEnumRole(enumRole);
        user2.setLastUpdated(LocalDateTime.now());
        user2.setLastLogout(LocalDateTime.now());

        // Mock the userRepository.findByEnumRole method to return the sample users
        when(userRepository.findByEnumRole(enumRole)).thenReturn(Arrays.asList(user1, user2));

        // Call the getUserDTOsByRole method
        List<UserDTO> userDTOs = userService.getUserDTOsByRole(enumRole);

        // Assert that the returned list contains the expected UserDTOs
        assertEquals(2, userDTOs.size());
        assertEquals(1L, userDTOs.get(0).getId());
        assertEquals(2L, userDTOs.get(1).getId());
        assertEquals("User1", userDTOs.get(0).getName());
        assertEquals("User2", userDTOs.get(1).getName());
        assertEquals("user1@example.com", userDTOs.get(0).getEmail());
        assertEquals("user2@example.com", userDTOs.get(1).getEmail());
        assertEquals(enumRole, userDTOs.get(0).getEnumRole());
        assertEquals(enumRole, userDTOs.get(1).getEnumRole());
        assertNotNull(userDTOs.get(0).getLastUpdated());
        assertNotNull(userDTOs.get(1).getLastUpdated());
        assertNotNull(userDTOs.get(0).getLastLogout());
        assertNotNull(userDTOs.get(1).getLastLogout());
    }

    @Test
    void testGetUserDTOsByRole_NoUsersFound() {
        // Define the role you want to test
        EnumRole enumRole = EnumRole.ADMIN;

        // Mock the userRepository.findByEnumRole method to return an empty list
        when(userRepository.findByEnumRole(enumRole)).thenReturn(new ArrayList<>());

        // Call the getUserDTOsByRole method
        List<UserDTO> userDTOs = userService.getUserDTOsByRole(enumRole);

        // Assert that the returned list is empty
        assertTrue(userDTOs.isEmpty());
    }
    @Test
    void testGetCountAllUsers() {
        // Define the expected count of users
        int expectedCount = 10;

        // Mock the userRepository.countAllUsers method to return the expected count
        when(userRepository.countAllUsers()).thenReturn(expectedCount);

        // Call the getCountAllUsers method
        int actualCount = userService.getCountAllUsers();

        // Assert that the actual count matches the expected count
        assertEquals(expectedCount, actualCount);
    }

    @Test
    void testGetCountAllUsersByRole() {
        // Define the expected count of users for a specific role
        int expectedCount = 5;
        EnumRole role = EnumRole.USER;

        // Mock the userRepository.countAllUsersByRole method to return the expected count
        when(userRepository.countAllUsersByRole(role)).thenReturn(expectedCount);

        // Call the getCountAllUsersByRole method
        int actualCount = userService.getCountAllUsersByRole(role);

        // Assert that the actual count matches the expected count
        assertEquals(expectedCount, actualCount);
    }

    @Test
    void testGetCountAllUsersByProjectId() {
        // Define the expected count of users for a specific project
        int expectedCount = 5;
        Long projectId = 1L;

        // Mock the projectRepository.countAllUsersByProjectId method to return the expected count
        when(projectRepository.countAllUsersByProjectId(projectId)).thenReturn(expectedCount);

        // Call the getCountAllUsersByProjectId method
        int actualCount = userService.getCountAllUsersByProjectId(projectId);

        // Assert that the actual count matches the expected count
        assertEquals(expectedCount, actualCount);
    }

    @Test
    void testGetCountAllUsersByProjectId_NullCount() {
        // Define the scenario where the projectRepository.countAllUsersByProjectId returns null
        Long projectId = 1L;

        // Mock the projectRepository.countAllUsersByProjectId method to return null
        when(projectRepository.countAllUsersByProjectId(projectId)).thenReturn(null);

        // Call the getCountAllUsersByProjectId method
        int actualCount = userService.getCountAllUsersByProjectId(projectId);

        // Assert that the actual count is 0 when the repository returns null
        assertEquals(0, actualCount);
    }
    @Test
    void testGetAllUsersWithProjects() {
        // Create a list of users with projects
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");
        user1.setProjects(new ArrayList<>());

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setProjects(new ArrayList<>());

        // Mock the userRepository.findAllUsers() method to return the list of users
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        when(userRepository.findAllUsers()).thenReturn(users);

        // Create test projects for users
        Project project1 = new Project();
        project1.setProjectName("Project 1");
        project1.setDeleted(false);

        Project project2 = new Project();
        project2.setProjectName("Project 2");
        project2.setDeleted(true); // Deleted project

        // Add projects to users
        user1.getProjects().add(project1);
        user1.getProjects().add(project2); // Deleted project should be filtered out
        user2.getProjects().add(project1);

        // Call the method to be tested
        List<UserProjectsDTO> userProjectsDTOs = userService.getAllUsersWithProjects();

        // Verify the result
        assertEquals(2, userProjectsDTOs.size()); // Two users
        assertEquals(1, userProjectsDTOs.get(0).getProjectNames().size()); // User 1 has one project
        assertEquals(1, userProjectsDTOs.get(1).getProjectNames().size()); // User 2 has one project
        assertEquals("User 1", userProjectsDTOs.get(0).getUserName());
        assertEquals("User 2", userProjectsDTOs.get(1).getUserName());
    }

    @Test
    void testGetAllUsersWithoutProjects() {
        // Create a list of users with projects
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        user1.setEnumRole(EnumRole.USER);
        user1.setProjects(new ArrayList<>());

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        user2.setEnumRole(EnumRole.ADMIN);
        user2.setProjects(new ArrayList<>());

        // Mock the userRepository.findAllUsersByRole() method to return the list of users
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        when(userRepository.findAllUsersByRole(EnumRole.USER)).thenReturn(users);

        // Create test projects for users
        Project project1 = new Project();
        project1.setProjectId(101L); // Project ID 101
        project1.setProjectName("Project 1");
        project1.setDeleted(false);

        Project project2 = new Project();
        project2.setProjectId(102L); // Project ID 102
        project2.setProjectName("Project 2");
        project2.setDeleted(false);

        // Add projects to users
        user1.getProjects().add(project1);
        user2.getProjects().add(project2);

        // Call the method to be tested
        List<UserDTO> userDTOs = userService.getAllUsersWithoutProjects(EnumRole.USER, 101L);

        // Verify the result
        assertEquals(1, userDTOs.size()); // Only one user (User 2) has no project with ID 101
        assertEquals("User 2", userDTOs.get(0).getName());
        assertEquals("user2@example.com", userDTOs.get(0).getEmail());
        assertEquals(EnumRole.ADMIN, userDTOs.get(0).getEnumRole());
    }

    @Test
    void testGetAllUsersWithoutProjectsNoUsers() {
        // Mock the userRepository.findAllUsersByRole() method to return an empty list
        when(userRepository.findAllUsersByRole(EnumRole.USER)).thenReturn(new ArrayList<>());

        // Call the method to be tested
        List<UserDTO> userDTOs = userService.getAllUsersWithoutProjects(EnumRole.USER, 101L);

        // Verify the result when there are no users
        assertTrue(userDTOs.isEmpty());
    }

    @Test
    void testProjectExists_WhenProjectExists_ShouldReturnTrue() {
        // Arrange
        String projectNameToCheck = "ExistingProject";
        List<Project> mockProjects = new ArrayList<>();
        Project existingProject = new Project();
        existingProject.setProjectName(projectNameToCheck);
        mockProjects.add(existingProject);

        when(projectRepository.findAllProjects()).thenReturn(mockProjects);

        // Act
        boolean result = userService.projectExists(projectNameToCheck);

        // Assert
        assertTrue(result);
    }

    @Test
    void testProjectExists_WhenProjectDoesNotExist_ShouldReturnFalse() {
        // Arrange
        String projectNameToCheck = "NonExistentProject";
        List<Project> mockProjects = new ArrayList<>();
        Project existingProject = new Project();
        existingProject.setProjectName("ExistingProject");
        mockProjects.add(existingProject);

        when(projectRepository.findAllProjects()).thenReturn(mockProjects);

        // Act
        boolean result = userService.projectExists(projectNameToCheck);

        // Assert
        assertFalse(result);
    }
    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> mockUsers = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@example.com");
        user1.setEnumRole(EnumRole.USER);
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Alice");
        user2.setEmail("alice@example.com");
        user2.setEnumRole(EnumRole.ADMIN);
        mockUsers.add(user1);
        mockUsers.add(user2);

        when(userRepository.findAll()).thenReturn(mockUsers);

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("John", result.get(0).getName());
        assertEquals("john@example.com", result.get(0).getEmail());
        assertEquals(EnumRole.USER, result.get(0).getEnumRole());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Alice", result.get(1).getName());
        assertEquals("alice@example.com", result.get(1).getEmail());
        assertEquals(EnumRole.ADMIN, result.get(1).getEnumRole());
    }

    @Test
    void testGetAllProjectsAndRepositoriesByUserId_ExistingUserWithProjects() {
        Long userId = 1L;

        // Create a sample user with associated projects
        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setEnumRole(EnumRole.USER);

        Project project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("Project 1");

        Project project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project 2");

        // Initialize the repositories property for the projects
        project1.setRepositories(Collections.emptyList());
        project2.setRepositories(Collections.emptyList());

        user.setProjects(Arrays.asList(project1, project2));

        // Mock the UserRepository to return the sample user
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock the ModelMapper to return a ProjectDTO
        when(modelMapper.map(project1, ProjectDTO.class)).thenReturn(new ProjectDTO());
        when(modelMapper.map(project2, ProjectDTO.class)).thenReturn(new ProjectDTO());

        // Mock any other necessary dependencies

        List<ProjectDTO> result = userService.getAllProjectsAndRepositoriesByUserId(userId);

        // Assert that the result contains the expected ProjectDTO objects
        assertEquals(2, result.size());

        // You can add more detailed assertions here to ensure correctness
        // For example, verify that the project names and repository information are correctly mapped.
    }

    @Test
    void testGetAllProjectsAndRepositoriesByUserId_ExistingUserWithNoProjects() {
        Long userId = 1L;

        // Create a sample user with no associated projects (empty list)
        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setEnumRole(EnumRole.USER);
        user.setProjects(Collections.emptyList()); // Set an empty list for projects

        // Mock the UserRepository to return the sample user
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock any necessary mappings (e.g., with modelMapper)

        List<ProjectDTO> result = userService.getAllProjectsAndRepositoriesByUserId(userId);

        // Assert that the result is an empty list
        assertTrue(result.isEmpty());
    }


    @Test
    void testGetUsersByRoleAndUserId_ExistingUserWithMatchingRoleAndProjects() {
        Long userId = 1L;
        EnumRole userRole = EnumRole.USER;

        // Create a sample user with matching role and associated projects
        User user = new User();
        user.setId(userId);
        user.setEnumRole(userRole);
        user.setProjects(Arrays.asList(new Project(), new Project()));

        // Mock the UserRepository to return the sample user
        when(userRepository.findByRoleAndUserId(userId, userRole)).thenReturn(user.getProjects());

        List<Project> result = userService.getUsersByRoleAndUserId(userId, userRole);

        // Assert that the result contains the expected projects
        assertEquals(2, result.size());
    }

    @Test
    void testGetUsersByRoleAndUserId_ExistingUserWithMatchingRoleButNoProjects() {
        Long userId = 1L;
        EnumRole userRole = EnumRole.USER;

        // Create a sample user with matching role but no associated projects
        User user = new User();
        user.setId(userId);
        user.setEnumRole(userRole);

        // Mock the UserRepository to return an empty list
        when(userRepository.findByRoleAndUserId(userId, userRole)).thenReturn(Collections.emptyList());

        List<Project> result = userService.getUsersByRoleAndUserId(userId, userRole);

        // Assert that the result is an empty list
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUsersByRoleAndUserId_NonExistingUser() {
        Long userId = 1L;
        EnumRole userRole = EnumRole.USER;

        // Mock the UserRepository to return an empty list
        when(userRepository.findByRoleAndUserId(userId, userRole)).thenReturn(Collections.emptyList());

        List<Project> result = userService.getUsersByRoleAndUserId(userId, userRole);

        // Assert that the result is an empty list
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProjectsByRoleIdAndUserId_ExistingUserWithMatchingRoleAndProjects() {
        Long userId = 1L;
        String role = "USER";

        // Create a sample user with matching role and associated projects
        EnumRole userRole = EnumRole.USER;
        User user = new User();
        user.setId(userId);
        user.setEnumRole(userRole);

        Project project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("Project 1");

        Project project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project 2");

        // Mock the UserRepository to return the sample user
        when(userRepository.findByRoleAndUserId(userId, userRole)).thenReturn(Arrays.asList(project1, project2));

        List<ProjectDTO> result = userService.getProjectsByRoleIdAndUserId(userId, role);

        // Assert that the result contains the expected ProjectDTO objects
        assertEquals(2, result.size());
        // You can add more detailed assertions here to ensure correctness
    }


    @Test
    void testGetProjectsByRoleIdAndUserId_ExistingUserWithMatchingRoleButNoProjects() {
        Long userId = 1L;
        String role = "USER";

        // Create a sample user with matching role but no associated projects
        EnumRole userRole = EnumRole.USER;
        User user = new User();
        user.setId(userId);
        user.setEnumRole(userRole);

        // Mock the UserRepository to return an empty list
        when(userRepository.findByRoleAndUserId(userId, userRole)).thenReturn(Collections.emptyList());

        List<ProjectDTO> result = userService.getProjectsByRoleIdAndUserId(userId, role);

        // Assert that the result is an empty list
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProjectsByRoleIdAndUserId_NonExistingUser() {
        Long userId = 1L;
        String role = "USER";

        // Mock the UserRepository to return an empty list
        EnumRole userRole = EnumRole.USER;
        when(userRepository.findByRoleAndUserId(userId, userRole)).thenReturn(Collections.emptyList());

        List<ProjectDTO> result = userService.getProjectsByRoleIdAndUserId(userId, role);

        // Assert that the result is an empty list
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserViaPhoneNumber() {
        // Arrange
        String phoneNumber = "1234567890";
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setPhoneNumber(phoneNumber);

        // Mock the userRepository.findByPhoneNumber method to return the expectedUser
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(expectedUser);

        // Act
        User actualUser = userService.getUserViaPhoneNumber(phoneNumber);

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getPhoneNumber(), actualUser.getPhoneNumber());

        // Verify that userRepository.findByPhoneNumber was called with the correct argument
        verify(userRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    @Test
    void testGetUserByMail() {
        // Arrange
        String userEmail = "user@example.com";
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setEmail(userEmail);

        // Mock the userRepository.findByEmail method to return the expectedUser
        when(userRepository.findByEmail(userEmail)).thenReturn(expectedUser);

        // Act
        User actualUser = userService.getUserByMail(userEmail);

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());

        // Verify that userRepository.findByEmail was called with the correct argument
        verify(userRepository, times(1)).findByEmail(userEmail);
    }


     @ParameterizedTest
     @ValueSource(longs = {1L, 2L, 3L})
     void testDeleteUserById_InvalidUserId(Long userId) {
    // Mock userRepository.findById to return an empty Optional (user not found)
     Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

     // Act
     String result = userService.deleteUserById(userId);

     // Assert
     assertEquals("Invalid user ID", result);
    }
    @Test
    void testDeleteUserById_UserAlreadyDeleted() {
        // Arrange
        Long userId = 1L;
        User deletedUser = new User();
        deletedUser.setDeleted(true);

        // Mock userRepository.findById to return the deleted user
        when(userRepository.findById(userId)).thenReturn(Optional.of(deletedUser));

        // Act
        String result = userService.deleteUserById(userId);

        // Assert
        assertEquals("User doesn't exist", result);
    }
    @Test
    void testSaveUser_Success() {
        // Arrange
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

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User savedUserResult = userService.saveUser(userCreationDTO);

        // Assert
        assertEquals(userCreationDTO.getId(), savedUserResult.getId());
        assertEquals(userCreationDTO.getName(), savedUserResult.getName());
        assertEquals(userCreationDTO.getEmail(), savedUserResult.getEmail());
        assertEquals(userCreationDTO.getEnumRole(), savedUserResult.getEnumRole());
        assertNotNull(savedUserResult.getLastUpdated());
        assertNotNull(savedUserResult.getLastLogout());
    }

    @Test
    void testGetUsersByRole_WithNoMatchingUsers() {
        EnumRole testRole = EnumRole.ADMIN;

        when(userRepository.findByRole(testRole)).thenReturn(Collections.emptyList());

        List<User> result = userService.getUsersByRole(testRole);

        assertTrue(result.isEmpty());
    }


    @Test
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

    @Test
    void testProjectExists_ProjectDoesNotExist() {
        String projectName = "Project A";
        List<Project> projects = new ArrayList<>();

        when(projectRepository.findAllProjects()).thenReturn(projects);

        boolean result = userService.projectExists(projectName);

        assertFalse(result);
    }

    @Test
    void testGetAllUsers_EmptyData() {
        List<User> users = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testLoginVerification_Successful() {
        String userEmail = "johndoe@example.com";
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail(userEmail);
        user.setEnumRole(EnumRole.USER);

        when(userRepository.existsByEmail(userEmail)).thenReturn(user);

        String mockToken = "mocked-jwt-token";
        when(jwtService.generateToken(user)).thenReturn(mockToken);

        doNothing().when(jwtUtils).saveUserToken(user, mockToken);

        UserDTO result = userService.loginVerification(userEmail);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getEnumRole(), result.getEnumRole());
        assertNotNull(result.getLastUpdated());
        assertEquals(mockToken, result.getToken());
    }

    @Test
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
    void testLoginVerification_UserNotFound() {
        String userEmail = "nonexistent@example.com";
        when(userRepository.existsByEmail(userEmail)).thenReturn(null);

        UserDTO result = userService.loginVerification(userEmail);

        assertNull(result);
    }


    @Test
    void testUserLogout_Unsuccessful_UserNotFound() {
        Long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String result = userService.userLogout(userId);

        assertEquals("Log out unsuccessful", result);
    }

    @Test
    void testLoginVerification_Unsuccessful_UserNotFound() {
        String userEmail = "nonexistent@example.com";

        when(userRepository.existsByEmail(userEmail)).thenReturn(null);

        UserDTO result = userService.loginVerification(userEmail);

        assertNull(result);
    }



    @Test
    void testDeleteUserById_UserExistsAndIsDeleted() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setDeleted(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        String result = userService.deleteUserById(userId);

        assertEquals("User doesn't exist", result);
    }

    @Test
    void testDeleteUserById_UserDoesNotExist() {
        Long userId = 3L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String result = userService.deleteUserById(userId);

        assertEquals("Invalid user ID", result);
    }
    @Test
    void testGetUsersWithMultipleProjects_NoUsers() {
        when(projectRepository.findAllProjects()).thenReturn(Collections.emptyList());

        List<UserProjectsDTO> result = userService.getUsersWithMultipleProjects();

        assertEquals(Collections.emptyList(), result);
    }

    @Test
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


    @Test
    void testDeleteUserById() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setDeleted(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        String result = userService.deleteUserById(userId);

        // Assert
        assertEquals("User successfully deleted", result);
    }

    @Test
    void testDeleteUserById_InvalidUserID() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        String result = userService.deleteUserById(userId);

        // Assert
        assertEquals("Invalid user ID", result);
    }

    @Test
    void testDeleteUsersById_UserAlreadyDeleted() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setDeleted(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        String result = userService.deleteUserById(userId);

        // Assert
        assertEquals("User doesn't exist", result);
    }

    @Test
    void testDeleteUserById_SoftDeleteFailure() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setDeleted(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Use doNothing to specify that softDeleteUser doesn't throw an exception
        doNothing().when(userRepository).softDelete(userId);

        // Act
        String result = userService.deleteUserById(userId);

        // Assert
        assertEquals("User successfully deleted", result);
    }



    @Test
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

        List<ProjectDTO> response = userService.getProjectsByRoleIdAndUserId(1L, "user");

        assertNotNull(response);
    }

    @Test
    void testGetProjectsByRoleAndUserIdWithNullFigmaAndDrive() {
        User user = new User();
        user.setId(1L);
        EnumRole userRole = EnumRole.USER;
        Project project = new Project();
        project.setRepositories(new ArrayList<>());
        List<Project> projects = Collections.singletonList(project);

        when(userRepository.findByRoleAndUserId(1L, userRole)).thenReturn(projects);

        List<ProjectDTO> response = userService.getProjectsByRoleIdAndUserId(1L, "user");
;
        assertNotNull(response);
    }

    @Test
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
    void testGetAllProjectsAndRepositoriesByUserId_NonExistingUser() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getAllProjectsAndRepositoriesByUserId(userId);
        });
    }

}
