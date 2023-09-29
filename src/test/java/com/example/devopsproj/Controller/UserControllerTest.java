package com.example.devopsproj.Controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.controller.UserController;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.dto.responsedto.UserProjectsDTO;
import com.example.devopsproj.model.User;
import com.example.devopsproj.service.implementations.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@SpringBootApplication
@EnableWebMvc
public class UserControllerTest {

    private final UserServiceImpl userService = mock(UserServiceImpl.class);
    private final UserController userController = new UserController(userService);


    @Test
    public void testSaveUser_Success() {
        // Create a sample UserCreationDTO
        UserCreationDTO userCreationDTO = new UserCreationDTO();
        userCreationDTO.setName("John Doe");
        userCreationDTO.setEmail("john.doe@example.com");

        // Create a sample User object that will be returned by the mock service
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John Doe");
        savedUser.setEmail("john.doe@example.com");

        // Mock the behavior of the userService
        when(userService.saveUser(any(UserCreationDTO.class))).thenReturn(savedUser);

        // Call the saveUser method in the controller
        ResponseEntity<Object> responseEntity = userController.saveUser(userCreationDTO);

        // Verify that the service method was called with the correct argument
        verify(userService).saveUser(userCreationDTO);

        // Verify the response status and content
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(savedUser, responseEntity.getBody());
    }
    @Test
    public void testGetUserById_UserFound() {
        // Create a sample User object
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setEnumRole(EnumRole.USER); // Set the role

        // Mock the behavior of userServiceImpl.getUserById(userId)
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Call the getUserById method in the controller
        ResponseEntity<Object> responseEntity = userController.getUserById(1L);

        // Verify that the service method was called with the correct argument
        verify(userService).getUserById(1L);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify the response content
        UserDTO expectedUserDTO = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getEnumRole(),
                user.getLastUpdated(),
                user.getLastLogout()
        );

        UserDTO actualUserDTO = (UserDTO) responseEntity.getBody();
        assertEquals(expectedUserDTO.getId(), actualUserDTO.getId());
        assertEquals(expectedUserDTO.getName(), actualUserDTO.getName());
        assertEquals(expectedUserDTO.getEmail(), actualUserDTO.getEmail());
        assertEquals(expectedUserDTO.getEnumRole(), actualUserDTO.getEnumRole());
        assertEquals(expectedUserDTO.getLastUpdated(), actualUserDTO.getLastUpdated());
        assertEquals(expectedUserDTO.getLastLogout(), actualUserDTO.getLastLogout());
    }


    @Test
    public void testGetUserById_UserNotFound() {
        // Mock the behavior of userServiceImpl.getUserById(userId) to return an empty Optional
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // Call the getUserById method in the controller
        ResponseEntity<Object> responseEntity = userController.getUserById(1L);

        // Verify that the service method was called with the correct argument
        verify(userService).getUserById(1L);

        // Verify the response status
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testGetProjectsByRoleIdAndUserId_ProjectsFound() {
        // Create a sample user ID and role
        Long userId = 1L;
        String role = "USER";

        // Create a sample list of ProjectDTO objects
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        projectDTOList.add(new ProjectDTO(1L, "Project1", "Description1", null, null));
        projectDTOList.add(new ProjectDTO(2L, "Project2", "Description2", null, null));

        // Mock the behavior of userServiceImpl.getProjectsByRoleIdAndUserId(userId, role)
        when(userService.getProjectsByRoleIdAndUserId(userId, role)).thenReturn(projectDTOList);

        // Call the getProjectsByRoleIdAndUserId method in the controller
        ResponseEntity<Object> responseEntity = userController.getProjectsByRoleIdAndUserId(userId, role);

        // Verify that the service method was called with the correct arguments
        verify(userService).getProjectsByRoleIdAndUserId(userId, role);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify the response content
        List<ProjectDTO> actualProjectDTOList = (List<ProjectDTO>) responseEntity.getBody();
        assertEquals(projectDTOList.size(), actualProjectDTOList.size());

        // Verify individual project details
        for (int i = 0; i < projectDTOList.size(); i++) {
            ProjectDTO expectedProjectDTO = projectDTOList.get(i);
            ProjectDTO actualProjectDTO = actualProjectDTOList.get(i);
            assertEquals(expectedProjectDTO.getProjectId(), actualProjectDTO.getProjectId());
            assertEquals(expectedProjectDTO.getProjectName(), actualProjectDTO.getProjectName());
            assertEquals(expectedProjectDTO.getProjectDescription(), actualProjectDTO.getProjectDescription());
            // Add comparisons for other fields as needed
        }
    }

    @Test
    public void testUpdateUser_UserUpdatedSuccessfully() {
        // Create a sample user ID
        Long userId = 1L;

        // Create a sample UserDTO object for the update
        UserDTO updatedUserDTO = new UserDTO(userId, "Updated Name", "updated.email@example.com", EnumRole.USER);

        // Mock the behavior of userServiceImpl.updateUser(id, userDTO)
        when(userService.updateUser(userId, updatedUserDTO)).thenReturn(updatedUserDTO);

        // Call the updateUser method in the controller
        ResponseEntity<Object> responseEntity = userController.updateUser(userId, updatedUserDTO);

        // Verify that the service method was called with the correct arguments
        verify(userService).updateUser(userId, updatedUserDTO);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify the response content
        UserDTO actualUserDTO = (UserDTO) responseEntity.getBody();
        assertEquals(updatedUserDTO.getId(), actualUserDTO.getId());
        assertEquals(updatedUserDTO.getName(), actualUserDTO.getName());
        assertEquals(updatedUserDTO.getEmail(), actualUserDTO.getEmail());
        assertEquals(updatedUserDTO.getEnumRole(), actualUserDTO.getEnumRole());
        // Add comparisons for other fields as needed
    }

    @Test
    public void testDeleteUserById_UserDeletedSuccessfully() {
        // Create a sample user ID
        Long userId = 1L;

        // Define the deletion result message
        String deletionResult = "User successfully deleted";

        // Mock the behavior of userServiceImpl.deleteUserById(userId)
        when(userService.deleteUserById(userId)).thenReturn(deletionResult);

        // Call the deleteUserById method in the controller
        ResponseEntity<String> responseEntity = userController.deleteUserById(userId);

        // Verify that the service method was called with the correct argument
        verify(userService).deleteUserById(userId);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify the response content
        assertEquals(deletionResult, responseEntity.getBody());
    }

    @Test
    public void testGetUserByRoleId_UsersFound() {
        // Define a sample role
        String role = "USER";

        // Create a sample list of UserDTOs
        List<UserDTO> userDTOList = new ArrayList<>();
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setName("John Doe");
        userDTO1.setEmail("john.doe@example.com");
        userDTOList.add(userDTO1);

        // Mock the behavior of userServiceImpl.getUserDTOsByRole
        when(userService.getUserDTOsByRole(EnumRole.USER)).thenReturn(userDTOList);

        // Call the getUserByRoleId method in the controller
        ResponseEntity<Object> responseEntity = userController.getUserByRoleId(role);

        // Verify that the service method was called with the correct argument
        verify(userService).getUserDTOsByRole(EnumRole.USER);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify the response content
        List<UserDTO> expectedUserDTOList = userDTOList;
        List<UserDTO> actualUserDTOList = (List<UserDTO>) responseEntity.getBody();
        assertEquals(expectedUserDTOList, actualUserDTOList);
    }

    @Test
    public void testGetUserByRoleId_NoUsersFound() {
        // Define a role that does not have any associated users
        String role = "ADMIN"; // You can choose a role that is not present in your sample data

        // Mock the behavior of userServiceImpl.getUserDTOsByRole
        when(userService.getUserDTOsByRole(EnumRole.ADMIN)).thenReturn(Collections.emptyList());

        // Call the getUserByRoleId method in the controller
        ResponseEntity<Object> responseEntity = userController.getUserByRoleId(role);

        // Verify that the service method was called with the correct argument
        verify(userService).getUserDTOsByRole(EnumRole.ADMIN);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // Verify that an empty list is returned when no users are found
        assertTrue(((List<UserDTO>) responseEntity.getBody()).isEmpty());
    }
    @Test
    public void testGetCountAllUsers_CountGreaterThanZero() {
        // Define a sample count
        int count = 5;

        // Mock the behavior of userServiceImpl.getCountAllUsers
        when(userService.getCountAllUsers()).thenReturn(count);

        // Call the getCountAllUsers method in the controller
        ResponseEntity<Object> responseEntity = userController.getCountAllUsers();

        // Verify that the service method was called
        verify(userService).getCountAllUsers();

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the count is returned as the response body
        assertEquals(count, responseEntity.getBody());
    }

    @Test
    public void testGetCountAllUsers_CountIsZero() {
        // Define a sample count of 0
        int count = 0;

        // Mock the behavior of userServiceImpl.getCountAllUsers
        when(userService.getCountAllUsers()).thenReturn(count);

        // Call the getCountAllUsers method in the controller
        ResponseEntity<Object> responseEntity = userController.getCountAllUsers();

        // Verify that the service method was called
        verify(userService).getCountAllUsers();

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that 0 is returned as the response body
        assertEquals(0, responseEntity.getBody());
    }
    @Test
    public void testGetCountAllUsersByRole_CountGreaterThanZero() {
        // Define a sample role and count
        String role = "USER";
        int count = 5;

        // Mock the behavior of userServiceImpl.getCountAllUsersByRole
        when(userService.getCountAllUsersByRole(EnumRole.USER)).thenReturn(count);

        // Call the getCountAllUsersByRole method in the controller
        ResponseEntity<Object> responseEntity = userController.getCountAllUsersByRole(role);

        // Verify that the service method was called
        verify(userService).getCountAllUsersByRole(EnumRole.USER);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the count is returned as the response body
        assertEquals(count, responseEntity.getBody());
    }

    @Test
    public void testGetCountAllUsersByRole_CountIsZero() {
        // Define a sample role and count of 0
        String role = "USER";
        int count = 0;

        // Mock the behavior of userServiceImpl.getCountAllUsersByRole
        when(userService.getCountAllUsersByRole(EnumRole.USER)).thenReturn(count);

        // Call the getCountAllUsersByRole method in the controller
        ResponseEntity<Object> responseEntity = userController.getCountAllUsersByRole(role);

        // Verify that the service method was called
        verify(userService).getCountAllUsersByRole(EnumRole.USER);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that 0 is returned as the response body
        assertEquals(0, responseEntity.getBody());
    }
    @Test
    public void testGetCountAllUsersByProjectId_CountGreaterThanZero() {
        // Define a sample project ID and count
        Long projectId = 1L;
        int count = 5;

        // Mock the behavior of userServiceImpl.getCountAllUsersByProjectId
        when(userService.getCountAllUsersByProjectId(projectId)).thenReturn(count);

        // Call the getCountAllUsersByProjectId method in the controller
        ResponseEntity<Object> responseEntity = userController.getCountAllUsersByProjectId(projectId);

        // Verify that the service method was called
        verify(userService).getCountAllUsersByProjectId(projectId);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the count is returned as the response body
        assertEquals(count, responseEntity.getBody());
    }

    @Test
    public void testGetCountAllUsersByProjectId_CountIsZero() {
        // Define a sample project ID and count of 0
        Long projectId = 2L;
        int count = 0;

        // Mock the behavior of userServiceImpl.getCountAllUsersByProjectId
        when(userService.getCountAllUsersByProjectId(projectId)).thenReturn(count);

        // Call the getCountAllUsersByProjectId method in the controller
        ResponseEntity<Object> responseEntity = userController.getCountAllUsersByProjectId(projectId);

        // Verify that the service method was called
        verify(userService).getCountAllUsersByProjectId(projectId);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that 0 is returned as the response body
        assertEquals(0, responseEntity.getBody());
    }

    @Test
    public void testGetAllProjectsByUserId_ProjectsFound() {
        // Define a sample user ID
        Long userId = 1L;

        // Mock the behavior of userServiceImpl.getAllProjectsAndRepositoriesByUserId
        List<ProjectDTO> projects = createSampleProjects(); // Create sample projects
        when(userService.getAllProjectsAndRepositoriesByUserId(userId)).thenReturn(projects);

        // Call the getAllProjectsByUserId method in the controller
        ResponseEntity<Object> responseEntity = userController.getAllProjectsByUserId(userId);

        // Verify that the service method was called
        verify(userService).getAllProjectsAndRepositoriesByUserId(userId);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the projects are returned as the response body
        assertEquals(projects, responseEntity.getBody());
    }

    @Test
    public void testGetAllProjectsByUserId_NoProjectsFound() {
        // Define a sample user ID
        Long userId = 2L;

        // Mock the behavior of userServiceImpl.getAllProjectsAndRepositoriesByUserId
        when(userService.getAllProjectsAndRepositoriesByUserId(userId)).thenReturn(Collections.emptyList());

        // Call the getAllProjectsByUserId method in the controller
        ResponseEntity<Object> responseEntity = userController.getAllProjectsByUserId(userId);

        // Verify that the service method was called
        verify(userService).getAllProjectsAndRepositoriesByUserId(userId);

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that an empty list is returned as the response body
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    // Helper method to create sample projects for testing
    private List<ProjectDTO> createSampleProjects() {
        // Create and return a list of sample ProjectDTO objects
        // Modify this method to create projects with different data as needed for testing
        List<ProjectDTO> projects = new ArrayList<>();
        projects.add(new ProjectDTO(/* project data */));
        projects.add(new ProjectDTO(/* project data */));
        return projects;
    }

    @Test
    public void testGetAllUsers_UsersFound() {
        // Mock the behavior of userServiceImpl.getAllUsers
        List<UserDTO> users = createSampleUsers(); // Create sample users
        when(userService.getAllUsers()).thenReturn(users);

        // Call the getAllUsers method in the controller
        ResponseEntity<Object> responseEntity = userController.getAllUsers();

        // Verify that the service method was called
        verify(userService).getAllUsers();

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the users are returned as the response body
        assertEquals(users, responseEntity.getBody());
    }

    @Test
    public void testGetAllUsers_NoUsersFound() {
        // Mock the behavior of userServiceImpl.getAllUsers
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Call the getAllUsers method in the controller
        ResponseEntity<Object> responseEntity = userController.getAllUsers();

        // Verify that the service method was called
        verify(userService).getAllUsers();

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that an empty list is returned as the response body
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    // Helper method to create sample users for testing
    private List<UserDTO> createSampleUsers() {
        // Create and return a list of sample UserDTO objects
        // Modify this method to create users with different data as needed for testing
        List<UserDTO> users = new ArrayList<>();
        users.add(new UserDTO(/* user data */));
        users.add(new UserDTO(/* user data */));
        return users;
    }
    @Test
    public void testGetAllUsersWithProjects_UserProjectsFound() {
        // Mock the behavior of userServiceImpl.getAllUsersWithProjects
        List<UserProjectsDTO> userProjectsDTOs = createSampleUserProjects(); // Create sample user projects
        when(userService.getAllUsersWithProjects()).thenReturn(userProjectsDTOs);

        // Call the getAllUsersWithProjects method in the controller
        ResponseEntity<Object> responseEntity = userController.getAllUsersWithProjects();

        // Verify that the service method was called
        verify(userService).getAllUsersWithProjects();

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the user projects are returned as the response body
        assertEquals(userProjectsDTOs, responseEntity.getBody());
    }

    @Test
    public void testGetAllUsersWithProjects_NoUserProjectsFound() {
        // Mock the behavior of userServiceImpl.getAllUsersWithProjects
        when(userService.getAllUsersWithProjects()).thenReturn(Collections.emptyList());

        // Call the getAllUsersWithProjects method in the controller
        ResponseEntity<Object> responseEntity = userController.getAllUsersWithProjects();

        // Verify that the service method was called
        verify(userService).getAllUsersWithProjects();

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that an empty list is returned as the response body
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    // Helper method to create sample user projects for testing
    private List<UserProjectsDTO> createSampleUserProjects() {
        // Create and return a list of sample UserProjectsDTO objects
        // Modify this method to create user projects with different data as needed for testing
        List<UserProjectsDTO> userProjectsDTOs = new ArrayList<>();
        userProjectsDTOs.add(new UserProjectsDTO(/* user project data */));
        userProjectsDTOs.add(new UserProjectsDTO(/* user project data */));
        return userProjectsDTOs;
    }

    @Test
    public void testGetUsersWithMultipleProjects_UsersFound() {
        // Mock the behavior of userServiceImpl.getUsersWithMultipleProjects
        List<UserProjectsDTO> usersWithMultipleProjects = createSampleUsersWithMultipleProjects(); // Create sample users with multiple projects
        when(userService.getUsersWithMultipleProjects()).thenReturn(usersWithMultipleProjects);

        // Call the getUsersWithMultipleProjects method in the controller
        ResponseEntity<Object> responseEntity = userController.getUsersWithMultipleProjects();

        // Verify that the service method was called
        verify(userService).getUsersWithMultipleProjects();

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the users with multiple projects are returned as the response body
        assertEquals(usersWithMultipleProjects, responseEntity.getBody());
    }

    @Test
    public void testGetUsersWithMultipleProjects_NoUsersFound() {
        // Mock the behavior of userServiceImpl.getUsersWithMultipleProjects
        when(userService.getUsersWithMultipleProjects()).thenReturn(Collections.emptyList());

        // Call the getUsersWithMultipleProjects method in the controller
        ResponseEntity<Object> responseEntity = userController.getUsersWithMultipleProjects();

        // Verify that the service method was called
        verify(userService).getUsersWithMultipleProjects();

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that an empty list is returned as the response body
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    // Helper method to create sample users with multiple projects for testing
    private List<UserProjectsDTO> createSampleUsersWithMultipleProjects() {
        // Create and return a list of sample UserProjectsDTO objects
        // Modify this method to create users with multiple projects with different data as needed for testing
        List<UserProjectsDTO> usersWithMultipleProjects = new ArrayList<>();
        usersWithMultipleProjects.add(new UserProjectsDTO(/* user with multiple projects data */));
        usersWithMultipleProjects.add(new UserProjectsDTO(/* user with multiple projects data */));
        return usersWithMultipleProjects;
    }

    @Test
    public void testGetUserWithoutProject_UsersFound() {
        // Mock the behavior of userServiceImpl.getAllUsersWithoutProjects
        List<UserDTO> usersWithoutProjects = createSampleUsersWithoutProjects(); // Create sample users without projects
        when(userService.getAllUsersWithoutProjects(any(EnumRole.class), any(Long.class))).thenReturn(usersWithoutProjects);

        // Call the getUserWithoutProject method in the controller
        ResponseEntity<Object> responseEntity = userController.getUserWithoutProject("USER", 1L); // Modify the arguments as needed

        // Verify that the service method was called with the correct arguments
        verify(userService).getAllUsersWithoutProjects(EnumRole.USER, 1L); // Modify the arguments as needed

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the users without projects are returned as the response body
        assertEquals(usersWithoutProjects, responseEntity.getBody());
    }

    @Test
    public void testGetUserWithoutProject_NoUsersFound() {
        // Mock the behavior of userServiceImpl.getAllUsersWithoutProjects to return an empty list
        when(userService.getAllUsersWithoutProjects(any(EnumRole.class), any(Long.class))).thenReturn(Collections.emptyList());

        // Call the getUserWithoutProject method in the controller
        ResponseEntity<Object> responseEntity = userController.getUserWithoutProject("USER", 1L); // Modify the arguments as needed

        // Verify that the service method was called with the correct arguments
        verify(userService).getAllUsersWithoutProjects(EnumRole.USER, 1L); // Modify the arguments as needed

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that an empty list is returned as the response body
        assertEquals(Collections.emptyList(), responseEntity.getBody());
    }

    // Helper method to create sample users without projects for testing
    private List<UserDTO> createSampleUsersWithoutProjects() {
        // Create and return a list of sample UserDTO objects
        // Modify this method to create users without projects with different data as needed for testing
        List<UserDTO> usersWithoutProjects = new ArrayList<>();
        usersWithoutProjects.add(new UserDTO(/* user without projects data */));
        usersWithoutProjects.add(new UserDTO(/* user without projects data */));
        return usersWithoutProjects;
    }


    @Test
    public void testUserLogout_Success() {
        // Mock the behavior of userServiceImpl.userLogout to return a success message
        when(userService.userLogout(1L)).thenReturn("User successfully logged out"); // Modify the user ID as needed

        // Call the userLogout method in the controller
        ResponseEntity<String> responseEntity = userController.userLogout(1L); // Modify the user ID as needed

        // Verify that the service method was called with the correct user ID
        verify(userService).userLogout(1L); // Modify the user ID as needed

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the success message is returned as the response body
        assertEquals("User successfully logged out", responseEntity.getBody());
    }

    @Test
    public void testUserLogout_Error() {
        // Mock the behavior of userServiceImpl.userLogout to return an error message
        when(userService.userLogout(1L)).thenReturn("Error: Logout failed"); // Modify the user ID as needed

        // Call the userLogout method in the controller
        ResponseEntity<String> responseEntity = userController.userLogout(1L); // Modify the user ID as needed

        // Verify that the service method was called with the correct user ID
        verify(userService).userLogout(1L); // Modify the user ID as needed

        // Verify the response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the error message is returned as the response body
        assertEquals("Error: Logout failed", responseEntity.getBody());
    }

}

