package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.exceptions.ProjectNotFoundException;
import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.repository.UserRepository;

import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private GitRepositoryRepository gitRepositoryRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateProject_Success() {
        // Arrange
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(1L);
        projectDTO.setProjectName("Test Project");
        projectDTO.setProjectDescription("Test Description");

        // Mock the repository save method to return the same project
        Project savedProject = new Project();
        Mockito.when(projectRepository.save(Mockito.any(Project.class))).thenReturn(savedProject);

        // Mock the modelMapper.map method to return the same projectDTO
        Mockito.when(modelMapper.map(Mockito.any(Project.class), Mockito.eq(ProjectDTO.class))).thenReturn(projectDTO);

        // Act
        ProjectDTO createdProject = projectService.createProject(projectDTO);

        // Assert
        assertNotNull(createdProject);
        assertEquals(projectDTO.getProjectId(), createdProject.getProjectId());
        assertEquals(projectDTO.getProjectName(), createdProject.getProjectName());
        assertEquals(projectDTO.getProjectDescription(), createdProject.getProjectDescription());
    }
    @Test
    public void testCreateProject_SaveError() {
        // Arrange
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("Test Project");

        // Mock the repository save method to throw an exception
        Mockito.when(projectRepository.save(Mockito.any(Project.class))).thenThrow(new RuntimeException("Save failed"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> projectService.createProject(projectDTO));
    }

    @Test
    public void testGetProjectById_Success() {
        // Arrange
        Long projectId = 1L;
        Project project = new Project();
        project.setProjectId(projectId);

        // Mock the repository to return the project when findById is called
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        ProjectDTO projectDTO = projectService.getProjectById(projectId);

        // Assert
        assertNotNull(projectDTO);
        assertEquals(projectId, projectDTO.getProjectId());
    }

    @Test
    public void testGetProjectById_NotFound() {
        // Arrange
        Long projectId = 2L;

        // Mock the repository to return an empty Optional when findById is called
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act and Assert
        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.getProjectById(projectId);
        });

        assertEquals("Project not found with ID: " + projectId, exception.getMessage());
    }

    @Test
    public void testGetAll_Success() {
        // Arrange
        List<Project> mockProjects = new ArrayList<>();
        mockProjects.add(new Project(1L, "Project1", "Description1", LocalDateTime.now(), false));
        mockProjects.add(new Project(2L, "Project2", "Description2", LocalDateTime.now(), false));

        // Mock the repository to return the list of projects
        when(projectRepository.findAll()).thenReturn(mockProjects);

        // Act
        List<ProjectDTO> projectDTOs = projectService.getAll();

        // Assert
        assertNotNull(projectDTOs);
        assertEquals(2, projectDTOs.size()); // Assuming 2 projects are returned
    }


    @Test
    public void testGetAll_ServiceException() {
        // Arrange
        // Mock the repository to throw an exception
        when(projectRepository.findAll()).thenThrow(new RuntimeException("Something went wrong"));

        // Act and Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            projectService.getAll();
        });

        assertEquals("An error occurred while fetching projects", exception.getMessage());
        assertNotNull(exception.getCause());
    }


    @Test
    public void testGetAllProjectsWithUsers_NoProjectsFound() {
        // Arrange
        // Mock the repository to return an empty list of projects
        when(projectRepository.findAllProjects()).thenReturn(new ArrayList<>());

        // Act
        List<ProjectWithUsersDTO> projectsWithUsers = projectService.getAllProjectsWithUsers();

        // Assert
        assertNotNull(projectsWithUsers);
        assertTrue(projectsWithUsers.isEmpty()); // No projects, so the result should be an empty list
    }
    @Test
    public void testGetAllProjects_Success() {
        // Arrange
        List<Project> mockProjects = new ArrayList<>();
        Project project1 = new Project(1L, "Project1", "Description1", LocalDateTime.now(), false);
        Project project2 = new Project(2L, "Project2", "Description2", LocalDateTime.now(), false);
        mockProjects.add(project1);
        mockProjects.add(project2);

        // Mock the repository to return the list of projects
        when(projectRepository.findAllProjects()).thenReturn(mockProjects);

        // Act
        List<Project> projects = projectService.getAllProjects();

        // Assert
        assertNotNull(projects);
        assertEquals(2, projects.size()); // Assuming 2 projects are returned
        assertEquals(project1, projects.get(0)); // Ensure project1 is in the list
        assertEquals(project2, projects.get(1)); // Ensure project2 is in the list
    }

    @Test
    public void testGetAllProjects_EmptyList() {
        // Arrange
        // Mock the repository to return an empty list of projects
        when(projectRepository.findAllProjects()).thenReturn(new ArrayList<>());

        // Act
        List<Project> projects = projectService.getAllProjects();

        // Assert
        assertNotNull(projects);
        assertTrue(projects.isEmpty()); // No projects, so the result should be an empty list
    }
    @Test
    public void testUpdateProject_Success() {
        // Arrange
        Project projectToUpdate = new Project(1L, "UpdatedProject", "UpdatedDescription", LocalDateTime.now(), false);

        // Mock the repository to return the updated project when saved
        when(projectRepository.save(eq(projectToUpdate))).thenReturn(projectToUpdate);

        // Act
        Project updatedProject = projectService.updateProject(projectToUpdate);

        // Assert
        assertNotNull(updatedProject);
        assertEquals(projectToUpdate.getProjectId(), updatedProject.getProjectId());
        assertEquals(projectToUpdate.getProjectName(), updatedProject.getProjectName());
        assertEquals(projectToUpdate.getProjectDescription(), updatedProject.getProjectDescription());
        assertEquals(projectToUpdate.getLastUpdated(), updatedProject.getLastUpdated());
        assertEquals(projectToUpdate.getDeleted(), updatedProject.getDeleted());
    }

    @Test
    public void testUpdateProject_NullInput() {
        // Arrange
        // Mock the repository to return null when trying to save a null project
        when(projectRepository.save(null)).thenReturn(null);

        // Act
        Project updatedProject = projectService.updateProject(null);

        // Assert
        assertNull(updatedProject); // The method should return null for a null input
    }
    @Test
    public void testGetAllUsersByProjectId_Success() {
        // Arrange
        Long projectId = 1L;
        List<User> mockUsers = new ArrayList<>();
        User user1 = new User(1L, "User1", "user1@example.com", EnumRole.USER);
        User user2 = new User(2L, "User2", "user2@example.com", EnumRole.USER);
        mockUsers.add(user1);
        mockUsers.add(user2);

        // Mock the repository to return the list of users for the project
        when(projectRepository.findAllUsersByProjectId(projectId)).thenReturn(mockUsers);

        // Act
        List<UserDTO> userDTOs = projectService.getAllUsersByProjectId(projectId);

        // Assert
        assertNotNull(userDTOs);
        assertEquals(2, userDTOs.size()); // Assuming 2 users are associated with the project
        assertEquals(new UserDTO(1L, "User1", "user1@example.com", EnumRole.USER), userDTOs.get(0)); // Ensure user1 is in the list
        assertEquals(new UserDTO(2L, "User2", "user2@example.com", EnumRole.USER), userDTOs.get(1)); // Ensure user2 is in the list
    }

    @Test
    public void testGetAllUsersByProjectId_NoUsersFound() {
        // Arrange
        Long projectId = 1L;

        // Mock the repository to return an empty list of users for the project
        when(projectRepository.findAllUsersByProjectId(projectId)).thenReturn(new ArrayList<>());

        // Act and Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            projectService.getAllUsersByProjectId(projectId);
        });

        assertEquals("No users found for project with ID: " + projectId, exception.getMessage());
    }
    @Test
    public void testGetAllUsersByProjectIdAndRole_Success() {
        // Arrange
        Long projectId = 1L;
        EnumRole role = EnumRole.USER;

        List<User> mockUsers = new ArrayList<>();
        User user1 = new User(1L, "User1", "user1@example.com", EnumRole.USER);
        User user2 = new User(2L, "User2", "user2@example.com", EnumRole.USER);
        mockUsers.add(user1);
        mockUsers.add(user2);

        // Mock the repository to return the list of users for the project and role
        when(projectRepository.findAllUsersByProjectIdAndRole(projectId, role)).thenReturn(mockUsers);

        // Act
        List<UserDTO> userDTOs = projectService.getAllUsersByProjectIdAndRole(projectId, role);

        // Assert
        assertNotNull(userDTOs);
        assertEquals(2, userDTOs.size()); // Assuming 2 users with the specified role are associated with the project
        assertEquals(new UserDTO(1L, "User1", "user1@example.com", EnumRole.USER, null), userDTOs.get(0)); // Ensure user1 is in the list
        assertEquals(new UserDTO(2L, "User2", "user2@example.com", EnumRole.USER, null), userDTOs.get(1)); // Ensure user2 is in the list
    }

    @Test
    public void testGetAllUsersByProjectIdAndRole_NoUsersFound() {
        // Arrange
        Long projectId = 1L;
        EnumRole role = EnumRole.USER;

        // Mock the repository to return an empty list of users
        when(projectRepository.findAllUsersByProjectIdAndRole(projectId, role)).thenReturn(new ArrayList<>());

        // Act and Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            projectService.getAllUsersByProjectIdAndRole(projectId, role);
        });

        assertEquals("No users found for project with ID: 1 and role: USER", exception.getMessage());
    }



    //    @Test
//    public void testGetAllUsersByProjectIdAndRole_InternalServerError() {
//        // Arrange
//        Long projectId = 1L;
//        EnumRole role = EnumRole.USER;
//
//        // Mock the repository to throw an exception
//        when(projectRepository.findAllUsersByProjectIdAndRole(projectId, role)).thenThrow(new RuntimeException("Database error"));
//
//        // Act and Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            projectService.getAllUsersByProjectIdAndRole(projectId, role);
//        });
//
//        assertEquals("Internal server error.", exception.getMessage());
//    }
    @Test
    public void testUpdateProject_Successs() {
        // Arrange
        Long projectId = 1L;
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("New Project Name");
        projectDTO.setProjectDescription("New Project Description");

        Project existingProject = new Project();
        existingProject.setProjectId(projectId);
        existingProject.setProjectName("Old Project Name");
        existingProject.setProjectDescription("Old Project Description");

        // Mock the repository to return the existing project when findById is called
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));

        // Mock the repository to return the updated project when save is called
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);

        // Act
        ProjectDTO updatedProjectDTO = projectService.updateProject(projectId, projectDTO);

        // Assert
        assertNotNull(updatedProjectDTO);
        assertEquals("New Project Name", updatedProjectDTO.getProjectName());
        assertEquals("New Project Description", updatedProjectDTO.getProjectDescription());
        assertNotNull(updatedProjectDTO.getLastUpdated());
    }

    @Test
    public void testUpdateProject_ProjectNotFound() {
        // Arrange
        Long projectId = 1L;
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("New Project Name");
        projectDTO.setProjectDescription("New Project Description");

        // Mock the repository to return an empty Optional when findById is called
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act and Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            projectService.updateProject(projectId, projectDTO);
        });

        assertEquals("Project with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testDeleteProject_Success() {
        // Arrange
        Long projectId = 1L;
        Project existingProject = new Project(projectId, "Project1", "Description1", LocalDateTime.now(), false);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));

        // Act
        ResponseEntity<String> response = projectService.deleteProject(projectId);

        // Assert
        assertNotNull(response);
        assertEquals("Deleted project successfully", response.getBody());
    }

    @Test
    public void testDeleteProject_ProjectNotFound() {
        // Arrange
        Long projectId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act and Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            projectService.deleteProject(projectId);
        });

        assertEquals("Project with ID " + projectId + " not found", exception.getMessage());
    }



    @Test
    public void testAddUserToProject_UserAlreadyExists() {
        // Arrange
        Long projectId = 1L;
        Long userId = 2L;

        Project project = new Project();
        project.setProjectId(projectId);
        project.setProjectName("Project1");
        project.setProjectDescription("Description1");

        User user = new User();
        user.setId(userId);
        user.setName("User1");
        user.setEmail("user1@example.com");
        user.setEnumRole(EnumRole.USER);

        // Mock the behavior of projectRepository.findById to return the project
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Mock the behavior of userRepository.findById to return the user
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock the behavior of projectRepository.existUserInProject to return a list with the user
        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(projectRepository.existUserInProject(projectId, userId)).thenReturn(userList);

        // Act
        ResponseEntity<Object> response = projectService.addUserToProject(projectId, userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }


    @Test
    public void testAddUserToProject_ProjectOrUserNotFound() {
        // Arrange
        Long projectId = 1L;
        Long userId = 2L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = projectService.addUserToProject(projectId, userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddUserToProject_InternalServerError() {
        // Arrange
        Long projectId = 1L;
        Long userId = 2L;

        when(projectRepository.findById(projectId)).thenThrow(new RuntimeException("Simulated error"));

        // Act
        ResponseEntity<Object> response = projectService.addUserToProject(projectId, userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    public void testRemoveUserFromProject_UserSuccessfullyRemoved() {
        // Arrange
        Long projectId = 1L;
        Long userId = 2L;

        Project project = new Project();
        project.setProjectId(projectId);

        User user = new User();
        user.setId(userId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.save(project)).thenReturn(project);

        // Act
        ResponseEntity<String> response = projectService.removeUserFromProject(projectId, userId);

        // Assert
        assertNotNull(response);
        //   assertEquals(HttpStatus.OK, response.getStatusCode());
        //   assertEquals("User removed", response.getBody());
    }


    @Test
    public void testRemoveUserFromProject_UserNotFound() {
        // Arrange
        Long projectId = 1L;
        Long userId = 2L;

        Project project = new Project();
        project.setProjectId(projectId);
        project.setProjectName("Project1");
        project.setProjectDescription("Description1");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = projectService.removeUserFromProject(projectId, userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Project or User not found", response.getBody());
        verify(projectRepository, never()).save(any());
    }

    @Test
    public void testRemoveUserFromProject_ExceptionDuringSave() {
        // Arrange
        Long projectId = 1L;
        Long userId = 2L;

        Project project = new Project();
        project.setProjectId(projectId);
        project.setProjectName("Project1");
        project.setProjectDescription("Description1");

        User user = new User();
        user.setId(userId);
        user.setName("User1");
        user.setEmail("user1@example.com");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.save(project)).thenThrow(new RuntimeException("Save failed"));

        // Act
        ResponseEntity<String> response = projectService.removeUserFromProject(projectId, userId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unable to remove user", response.getBody());
    }

    @Test
    public void testSoftDeleteProject_Success() {
        // Arrange
        Long projectId = 1L;

        // Mock the softDeleteProject method to perform a soft delete successfully
        doNothing().when(projectRepository).softDeleteProject(projectId);

        // Act
        boolean result = projectService.softDeleteProject(projectId);

        // Assert
        assertTrue(result);
        verify(projectRepository, times(1)).softDeleteProject(projectId);
    }

    @Test
    public void testSoftDeleteProject_Failure() {
        // Arrange
        Long projectId = 1L;

        // Mock the softDeleteProject method to throw an exception
        doThrow(new RuntimeException("Failed to delete project")).when(projectRepository).softDeleteProject(projectId);

        // Act
        boolean result = projectService.softDeleteProject(projectId);

        // Assert
        assertFalse(result);
        verify(projectRepository, times(1)).softDeleteProject(projectId);
    }

    @Test
    public void testExistsProjectById_Exists() {
        // Arrange
        Long projectId = 1L;

        // Mock the existsById method of the projectRepository to return true
        when(projectRepository.existsById(projectId)).thenReturn(true);

        // Act
        boolean exists = projectService.existsProjectById(projectId);

        // Assert
        assertTrue(exists);
        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    public void testExistsProjectById_NotExists() {
        // Arrange
        Long projectId = 1L;

        // Mock the existsById method of the projectRepository to return false
        when(projectRepository.existsById(projectId)).thenReturn(false);

        // Act
        boolean exists = projectService.existsProjectById(projectId);

        // Assert
        assertFalse(exists);
        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    public void testGetCountAllProjects_NullCount() {
        // Arrange
        when(projectRepository.countAllProjects()).thenReturn(null);

        // Act
        Integer count = projectService.getCountAllProjects();

        // Assert
        assertEquals(0, count); // It should return 0 when count is null
        verify(projectRepository, times(1)).countAllProjects();
    }

    @Test
    public void testGetCountAllProjects_ZeroCount() {
        // Arrange
        when(projectRepository.countAllProjects()).thenReturn(0);

        // Act
        Integer count = projectService.getCountAllProjects();

        // Assert
        assertEquals(0, count); // It should return 0 when count is 0
        verify(projectRepository, times(1)).countAllProjects();
    }

    @Test
    public void testGetCountAllProjects_PositiveCount() {
        // Arrange
        int expectedCount = 5;
        when(projectRepository.countAllProjects()).thenReturn(expectedCount);

        // Act
        Integer count = projectService.getCountAllProjects();

        // Assert
        assertEquals(expectedCount, count); // It should return the positive count
        verify(projectRepository, times(1)).countAllProjects();
    }
    @Test
    public void testGetCountAllProjectsByRole_NullCount() {
        // Arrange
        EnumRole role = EnumRole.USER;
        when(projectRepository.countAllProjectsByRole(role)).thenReturn(null);

        // Act
        Integer count = projectService.getCountAllProjectsByRole(role);

        // Assert
        assertEquals(0, count); // It should return 0 when count is null
        verify(projectRepository, times(1)).countAllProjectsByRole(role);
    }

    @Test
    public void testGetCountAllProjectsByRole_ZeroCount() {
        // Arrange
        EnumRole role = EnumRole.USER;
        when(projectRepository.countAllProjectsByRole(role)).thenReturn(0);

        // Act
        Integer count = projectService.getCountAllProjectsByRole(role);

        // Assert
        assertEquals(0, count); // It should return 0 when count is 0
        verify(projectRepository, times(1)).countAllProjectsByRole(role);
    }

    @Test
    public void testGetCountAllProjectsByRole_PositiveCount() {
        // Arrange
        EnumRole role = EnumRole.USER;
        int expectedCount = 5;
        when(projectRepository.countAllProjectsByRole(role)).thenReturn(expectedCount);

        // Act
        Integer count = projectService.getCountAllProjectsByRole(role);

        // Assert
        assertEquals(expectedCount, count); // It should return the positive count
        verify(projectRepository, times(1)).countAllProjectsByRole(role);
    }

    @Test
    public void testGetCountAllProjectsByUserId_NullCount() {
        // Arrange
        Long userId = 1L;
        when(projectRepository.countAllProjectsByUserId(userId)).thenReturn(null);

        // Act
        Integer count = projectService.getCountAllProjectsByUserId(userId);

        // Assert
        assertEquals(0, count); // It should return 0 when count is null
        verify(projectRepository, times(1)).countAllProjectsByUserId(userId);
    }

    @Test
    public void testGetCountAllProjectsByUserId_ZeroCount() {
        // Arrange
        Long userId = 1L;
        when(projectRepository.countAllProjectsByUserId(userId)).thenReturn(0);

        // Act
        Integer count = projectService.getCountAllProjectsByUserId(userId);

        // Assert
        assertEquals(0, count); // It should return 0 when count is 0
        verify(projectRepository, times(1)).countAllProjectsByUserId(userId);
    }

    @Test
    public void testGetCountAllProjectsByUserId_PositiveCount() {
        // Arrange
        Long userId = 1L;
        int expectedCount = 5;
        when(projectRepository.countAllProjectsByUserId(userId)).thenReturn(expectedCount);

        // Act
        Integer count = projectService.getCountAllProjectsByUserId(userId);

        // Assert
        assertEquals(expectedCount, count); // It should return the positive count
        verify(projectRepository, times(1)).countAllProjectsByUserId(userId);
    }

    @Test
    public void testGetCountAllUsersByProjectId_NullCount() {
        // Arrange
        Long projectId = 1L;
        when(projectRepository.countAllUsersByProjectId(projectId)).thenReturn(null);

        // Act
        Integer count = projectService.getCountAllUsersByProjectId(projectId);

        // Assert
        assertEquals(0, count); // It should return 0 when count is null
        verify(projectRepository, times(1)).countAllUsersByProjectId(projectId);
    }

    @Test
    public void testGetCountAllUsersByProjectId_ZeroCount() {
        // Arrange
        Long projectId = 1L;
        when(projectRepository.countAllUsersByProjectId(projectId)).thenReturn(0);

        // Act
        Integer count = projectService.getCountAllUsersByProjectId(projectId);

        // Assert
        assertEquals(0, count); // It should return 0 when count is 0
        verify(projectRepository, times(1)).countAllUsersByProjectId(projectId);
    }

    @Test
    public void testGetCountAllUsersByProjectId_PositiveCount() {
        // Arrange
        Long projectId = 1L;
        int expectedCount = 5;
        when(projectRepository.countAllUsersByProjectId(projectId)).thenReturn(expectedCount);

        // Act
        Integer count = projectService.getCountAllUsersByProjectId(projectId);

        // Assert
        assertEquals(expectedCount, count); // It should return the positive count
        verify(projectRepository, times(1)).countAllUsersByProjectId(projectId);
    }


    @Test
    public void testGetCountAllPeopleAndProjectName_NoProjects() {
        // Arrange
        when(projectRepository.findAllProjects()).thenReturn(new ArrayList<>());

        // Act
        List<ProjectNamePeopleCountDTO> result = projectService.getCountAllPeopleAndProjectName();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty()); // It should return an empty list when there are no projects
        verify(projectRepository, times(1)).findAllProjects();
        verify(projectRepository, never()).countAllUsersByProjectId(anyLong());
    }

    @Test
    public void testGetCountAllPeopleAndProjectName_WithProjects() {
        // Arrange
        Project project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("Project1");

        Project project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project2");

        when(projectRepository.findAllProjects()).thenReturn(Arrays.asList(project1, project2));
        when(projectRepository.countAllUsersByProjectId(1L)).thenReturn(3); // 3 users in Project1
        when(projectRepository.countAllUsersByProjectId(2L)).thenReturn(2); // 2 users in Project2

        // Act
        List<ProjectNamePeopleCountDTO> result = projectService.getCountAllPeopleAndProjectName();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // It should return a list with two DTOs
        assertEquals(1L, result.get(0).getProjectId());
        assertEquals("Project1", result.get(0).getProjectName());
        assertEquals(3, result.get(0).getCountPeople());
        assertEquals(2L, result.get(1).getProjectId());
        assertEquals("Project2", result.get(1).getProjectName());
        assertEquals(2, result.get(1).getCountPeople());

        verify(projectRepository, times(1)).findAllProjects();
        verify(projectRepository, times(2)).countAllUsersByProjectId(anyLong());
    }

    @Test
    public void testGetCountAllUsersByProjectIdAndRole_NoUsers() {
        // Arrange
        Long projectId = 1L;
        EnumRole enumRole = EnumRole.USER;
        when(projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole)).thenReturn(0);

        // Act
        Integer result = projectService.getCountAllUsersByProjectIdAndRole(projectId, enumRole);

        // Assert
        assertNotNull(result);
        assertEquals(0, result); // It should return 0 when there are no users
        verify(projectRepository, times(1)).countAllUsersByProjectIdAndRole(projectId, enumRole);
    }

    @Test
    public void testGetCountAllUsersByProjectIdAndRole_WithUsers() {
        // Arrange
        Long projectId = 1L;
        EnumRole enumRole = EnumRole.USER;
        when(projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole)).thenReturn(3); // 3 users with the specified role

        // Act
        Integer result = projectService.getCountAllUsersByProjectIdAndRole(projectId, enumRole);

        // Assert
        assertNotNull(result);
        assertEquals(3, result); // It should return the count of users with the specified role
        verify(projectRepository, times(1)).countAllUsersByProjectIdAndRole(projectId, enumRole);
    }
    @Test
    public void testGetCountAllActiveProjects_NoActiveProjects() {
        // Arrange
        when(projectRepository.countAllActiveProjects()).thenReturn(0);

        // Act
        Integer result = projectService.getCountAllActiveProjects();

        // Assert
        assertNotNull(result);
        assertEquals(0, result); // It should return 0 when there are no active projects
        verify(projectRepository, times(1)).countAllActiveProjects();
    }

    @Test
    public void testGetCountAllActiveProjects_WithActiveProjects() {
        // Arrange
        when(projectRepository.countAllActiveProjects()).thenReturn(5); // 5 active projects

        // Act
        Integer result = projectService.getCountAllActiveProjects();

        // Assert
        assertNotNull(result);
        assertEquals(5, result); // It should return the count of active projects
        verify(projectRepository, times(1)).countAllActiveProjects();
    }
    @Test
    public void testGetCountAllInActiveProjects_NoInactiveProjects() {
        // Arrange
        when(projectRepository.countAllInActiveProjects()).thenReturn(0);

        // Act
        Integer result = projectService.getCountAllInActiveProjects();

        // Assert
        assertNotNull(result);
        assertEquals(0, result); // It should return 0 when there are no inactive projects
        verify(projectRepository, times(1)).countAllInActiveProjects();
    }

    @Test
    public void testGetCountAllInActiveProjects_WithInactiveProjects() {
        // Arrange
        when(projectRepository.countAllInActiveProjects()).thenReturn(3); // 3 inactive projects

        // Act
        Integer result = projectService.getCountAllInActiveProjects();

        // Assert
        assertNotNull(result);
        assertEquals(3, result); // It should return the count of inactive projects
        verify(projectRepository, times(1)).countAllInActiveProjects();
    }

    @Test
    public void testGetUsersByProjectIdAndRole_NoUsersFound() {
        // Arrange
        Long projectId = 1L;
        String role = "USER";

        when(projectRepository.findUsersByProjectIdAndRole(projectId, EnumRole.USER)).thenReturn(List.of());

        // Act
        List<UserDTO> result = projectService.getUsersByProjectIdAndRole(projectId, role);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty()); // It should return an empty list when no users are found
        verify(projectRepository, times(1)).findUsersByProjectIdAndRole(projectId, EnumRole.USER);
    }

    @Test
    void testGetUsersByProjectIdAndRole_WithUsers() {
        // Arrange
        Long projectId = 1L;
        String role = "USER";

        User user1 = new User(1L, "User1", "user1@example.com", EnumRole.USER);
        User user2 = new User(2L, "User2", "user2@example.com", EnumRole.USER);

        when(projectRepository.findUsersByProjectIdAndRole(projectId, EnumRole.USER)).thenReturn(List.of(user1, user2));

        // Act
        List<UserDTO> result = projectService.getUsersByProjectIdAndRole(projectId, role);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // It should return a list with 2 users
        assertEquals(user1.getName(), result.get(0).getName());
        assertEquals(user2.getName(), result.get(1).getName());
        verify(projectRepository, times(1)).findUsersByProjectIdAndRole(projectId, EnumRole.USER);
    }

    @Test
    public void testAddRepositoryToProject_Success() {
        // Arrange
        Long projectId = 1L;
        Long repoId = 2L;

        Project project = new Project();
        project.setDeleted(false);

        GitRepository gitRepository = new GitRepository();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(gitRepositoryRepository.findById(repoId)).thenReturn(Optional.of(gitRepository));
        when(gitRepositoryRepository.save(gitRepository)).thenReturn(gitRepository);

        // Act
        ResponseEntity<Object> response = projectService.addRepositoryToProject(projectId, repoId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Stored successfully", response.getBody());
        assertEquals(project, gitRepository.getProject());
    }

    @Test
    public void testAddRepositoryToDeletedProject() {
        // Arrange
        Long projectId = 1L;
        Long repoId = 2L;

        Project project = new Project();
        project.setDeleted(true);

        GitRepository gitRepository = new GitRepository();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(gitRepositoryRepository.findById(repoId)).thenReturn(Optional.of(gitRepository));

        // Act
        ResponseEntity<Object> response = projectService.addRepositoryToProject(projectId, repoId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Stored successfully", response.getBody());
        assertNull(gitRepository.getProject()); // The project should be null because it's deleted
    }

    @Test
    public void testAddRepositoryToNonExistentProject() {
        // Arrange
        Long projectId = 1L;
        Long repoId = 2L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        when(gitRepositoryRepository.findById(repoId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = projectService.addRepositoryToProject(projectId, repoId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testAddRepositoryToNonExistentRepository() {
        // Arrange
        Long projectId = 1L;
        Long repoId = 2L;

        Project project = new Project();
        project.setDeleted(false);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(gitRepositoryRepository.findById(repoId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = projectService.addRepositoryToProject(projectId, repoId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetProjectsWithoutFigmaURL() {
        // Arrange
        Project project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("Project1");
        project1.setDeleted(false);

        Project project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project2");
        project2.setDeleted(false);

        Project project3 = new Project();
        project3.setProjectId(3L);
        project3.setProjectName("Project3");
        project3.setDeleted(false);

        // Mock the repository to return the list of projects
        List<Project> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projects.add(project3);
        when(projectRepository.findAllProjects()).thenReturn(projects);

        // Act
        List<ProjectDTO> result = projectService.getProjectsWithoutFigmaURL();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size()); // Expecting all 3 projects
        // Check if the project names match the result
        assertEquals("Project1", result.get(0).getProjectName());
        assertEquals("Project2", result.get(1).getProjectName());
        assertEquals("Project3", result.get(2).getProjectName());
    }

    @Test
    public void testGetProjectsWithoutGoogleDriveLink() {
        // Arrange
        Project project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("Project1");
        project1.setDeleted(false);

        Project project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project2");
        project2.setDeleted(false);

        Project project3 = new Project();
        project3.setProjectId(3L);
        project3.setProjectName("Project3");
        project3.setDeleted(false);

        // Mock the repository to return the list of projects
        List<Project> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projects.add(project3);
        when(projectRepository.findAllProjects()).thenReturn(projects);

        // Act
        List<ProjectDTO> result = projectService.getProjectsWithoutGoogleDriveLink();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size()); // Expecting all 3 projects
        // Check if the project names match the result
        assertEquals("Project1", result.get(0).getProjectName());
        assertEquals("Project2", result.get(1).getProjectName());
        assertEquals("Project3", result.get(2).getProjectName());
    }


    @Test
    public void testGetProjectDetailsById() {
        // Arrange
        Long projectId = 1L;
        String projectName = "Test Project";
        String projectDescription = "Test Description";
        boolean status = false;
        String pmName = "Project Manager";
        String figmaURL = "https://figma.com/project";
        String driveLink = "https://drive.google.com/project";
        LocalDateTime lastUpdated = LocalDateTime.now();

        // Create a Figma and GoogleDrive object
        Figma figma = new Figma();
        figma.setFigmaURL(figmaURL);

        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setDriveLink(driveLink);

        // Create a project with the above details
        Project project = new Project();
        project.setProjectId(projectId);
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setDeleted(status);
        project.setLastUpdated(lastUpdated);

        // Create a Project Manager user
        User pmUser = new User();
        pmUser.setEnumRole(EnumRole.PROJECT_MANAGER);
        pmUser.setName(pmName);

        // Create a GitRepository
        GitRepository gitRepository = new GitRepository();
        gitRepository.setName("GitRepo1");
        gitRepository.setDescription("Git Repository Description");

        // Add the GitRepository to the project's list of repositories
        List<GitRepository> repositories = new ArrayList<>();
        repositories.add(gitRepository);
        project.setRepositories(repositories);

        // Set the project's Figma and GoogleDrive
        project.setFigma(figma);
        project.setGoogleDrive(googleDrive);

        // Create a list of users for the project
        List<User> users = new ArrayList<>();
        users.add(pmUser);
        project.setUsers(users);

        // Mock the repository to return the project
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        ProjectDTO result = projectService.getProjectDetailsById(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(projectName, result.getProjectName());
        assertEquals(projectDescription, result.getProjectDescription());
//        assertEquals(status, result.getStatus());
        assertEquals(pmName, result.getPmName());
        assertEquals(1, result.getRepositories().size());
        assertEquals("GitRepo1", result.getRepositories().get(0).getName());
        assertEquals("Git Repository Description", result.getRepositories().get(0).getDescription());
        assertEquals(figmaURL, result.getFigma().getFigmaURL());
        assertEquals(driveLink, result.getGoogleDrive().getDriveLink());
        assertEquals(lastUpdated, result.getLastUpdated());
    }

    @Test
    public void testMapProjectToProjectDTO() {
        // Arrange
        Long projectId = 1L;
        String projectName = "Test Project";

        // Create a Project object
        Project project = new Project();
        project.setProjectId(projectId);
        project.setProjectName(projectName);

        // Act
        ProjectDTO projectDTO = projectService.mapProjectToProjectDTO(project);

        // Assert
        assertNotNull(projectDTO);
        assertEquals(projectId, projectDTO.getProjectId());
        assertEquals(projectName, projectDTO.getProjectName());
    }

    @Test
    public void testMapProjectDTOToProject() {
        // Arrange
        Long projectId = 1L;
        String projectName = "Test Project";

        // Create a ProjectDTO object
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(projectId);
        projectDTO.setProjectName(projectName);

        // Act
        Project project = projectService.mapProjectDTOToProject(projectDTO);

        // Assert
        assertNotNull(project);
        assertEquals(projectId, project.getProjectId());
        assertEquals(projectName, project.getProjectName());
        // Add additional assertions for other mapped properties if needed
    }







}
