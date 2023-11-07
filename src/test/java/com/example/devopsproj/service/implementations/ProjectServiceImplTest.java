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
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private GitHubCollaboratorServiceImpl collaboratorService;
    @Mock
    private GitRepositoryRepository gitRepositoryRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testCreateProject_Success() {
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
     void testCreateProject_SaveError() {
        // Arrange
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("Test Project");

        // Mock the repository save method to throw an exception
        Mockito.when(projectRepository.save(Mockito.any(Project.class))).thenThrow(new RuntimeException("Save failed"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> projectService.createProject(projectDTO));
    }

    @Test
     void testGetProjectById_Success() {
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
     void testGetProjectById_NotFound() {
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
     void testGetAll_Success() {
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
     void testGetAllProjectsWithUsers_NoProjectsFound() {
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
     void testGetAllProjects_Success() {
         // Arrange
         List<Project> mockProjects = new ArrayList<>();
         Project project1 = new Project(1L, "Project1", "Description1", LocalDateTime.now(), false);
         Project project2 = new Project(2L, "Project2", "Description2", LocalDateTime.now(), false);
         mockProjects.add(project1);
         mockProjects.add(project2);

         // Mock the repository to return the list of projects
         when(projectRepository.findAll()).thenReturn(mockProjects);

         // Act
         List<Project> projects = projectService.getAllProjects();

         // Assert
         assertNotNull(projects);
         assertEquals(2, projects.size()); // Assuming 2 projects are returned
         assertEquals(project1, projects.get(0)); // Ensure project1 is in the list
         assertEquals(project2, projects.get(1)); // Ensure project2 is in the list
     }


     @Test
    void testGetAllProjects_EmptyList() {
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
     void testUpdateProject_Success() {
        // Arrange
        Project projectToUpdate = new Project(1L, "UpdatedProject", "UpdatedDescription", LocalDateTime.now(), false);

        // Mock the repository to return the updated project when saved
        when(projectRepository.save(projectToUpdate)).thenReturn(projectToUpdate);

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

//    @Test
//     void testUpdateProject_NullInput() {
//        // Arrange
//        // Mock the repository to return null when trying to save a null project
//        when(projectRepository.save(null)).thenReturn(null);
//
//        // Act
//        Project updatedProject = projectService.updateProject(null);
//
//        // Assert
//        assertNull(updatedProject); // The method should return null for a null input
//    }

    @Test
     void testGetAllUsersByProjectId_Success() {
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
     void testGetAllUsersByProjectId_NoUsersFound() {
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
     void testGetAllUsersByProjectIdAndRole_Success() {
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
     void testGetAllUsersByProjectIdAndRole_NoUsersFound() {
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
   void testUpdateProject_Successs() {
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
    void testUpdateProject_ProjectNotFound() {
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
    void testDeleteProject_Success() {
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
    void testDeleteProject_ProjectNotFound() {
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
    void testAddUserToProject_UserAlreadyExists() {
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
    void testAddUserToProject_ProjectOrUserNotFound() {
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
     void testAddUserToProject_InternalServerErrorr() {
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
    void testSoftDeleteProject_Success() {
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
   void testSoftDeleteProject_Failure() {
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
     void testExistsProjectById_Exists() {
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
   void testExistsProjectById_NotExists() {
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
   void testGetCountAllProjects_NullCount() {
        // Arrange
        when(projectRepository.countAllProjects()).thenReturn(null);

        // Act
        Integer count = projectService.getCountAllProjects();

        // Assert
        assertEquals(0, count); // It should return 0 when count is null
        verify(projectRepository, times(1)).countAllProjects();
    }

    @Test
    void testGetCountAllProjects_ZeroCount() {
        // Arrange
        when(projectRepository.countAllProjects()).thenReturn(0);

        // Act
        Integer count = projectService.getCountAllProjects();

        // Assert
        assertEquals(0, count); // It should return 0 when count is 0
        verify(projectRepository, times(1)).countAllProjects();
    }

    @Test
     void testGetCountAllProjects_PositiveCount() {
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
     void testGetCountAllProjectsByRole_NullCount() {
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
   void testGetCountAllProjectsByRole_ZeroCount() {
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
    void testGetCountAllProjectsByRole_PositiveCount() {
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
     void testGetCountAllProjectsByUserId_NullCount() {
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
     void testGetCountAllProjectsByUserId_ZeroCount() {
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
    void testGetCountAllProjectsByUserId_PositiveCount() {
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
     void testGetCountAllUsersByProjectId_NullCount() {
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
    void testGetCountAllUsersByProjectId_ZeroCount() {
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
     void testGetCountAllUsersByProjectId_PositiveCount() {
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
   void testGetCountAllPeopleAndProjectName_NoProjects() {
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
    void testGetCountAllPeopleAndProjectName_WithProjects() {
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
    void testGetCountAllUsersByProjectIdAndRole_NoUsers() {
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
   void testGetCountAllUsersByProjectIdAndRole_WithUsers() {
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
     void testGetCountAllActiveProjects_NoActiveProjects() {
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
    void testGetCountAllActiveProjects_WithActiveProjects() {
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
     void testGetCountAllInActiveProjects_NoInactiveProjects() {
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
    void testGetCountAllInActiveProjects_WithInactiveProjects() {
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
     void testGetUsersByProjectIdAndRole_NoUsersFound() {
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
     void testAddRepositoryToProject_Success() {
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
     void testAddRepositoryToDeletedProject() {
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
   void testAddRepositoryToNonExistentProject() {
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
     void testAddRepositoryToNonExistentRepository() {
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
    void testGetProjectsWithoutFigmaURL() {
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
    void testGetProjectsWithoutGoogleDriveLink() {
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
    void testMapProjectToProjectDTO() {
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
    void testMapProjectDTOToProject() {
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


    @Test
    void testRemoveUserFromProjectAndRepo_Success() {
        Long projectId = 1L;
        Long userId = 1L;
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

        Project project = new Project();
        project.setUsers(new ArrayList<>());
        User user = new User();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(collaboratorService.deleteCollaborator(collaboratorDTO)).thenReturn(true);
        when(projectRepository.save(project)).thenReturn(project);

        ResponseEntity<String> responseEntity = projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User removed", responseEntity.getBody());
    }

    @Test
    void testRemoveUserFromProjectAndRepo_ProjectNotFound() {
        Long projectId = 1L;
        Long userId = 1L;
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        ResponseEntity<String> responseEntity = projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Project or User not found", responseEntity.getBody());
    }

    @Test
    void testRemoveUserFromProjectAndRepo_UserNotFound() {
        Long projectId = 1L;
        Long userId = 1L;
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Project or User not found", responseEntity.getBody());
    }

    @Test
    void testRemoveUserFromProjectAndRepo_badRequest() {
        Long projectId = 1L;
        Long userId = 1L;
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

        Project project = new Project();
        project.setUsers(new ArrayList<>());
        User user = new User();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(collaboratorService.deleteCollaborator(collaboratorDTO)).thenReturn(false);

        ResponseEntity<String> responseEntity = projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    @Test
    void testExistsProjectById_ProjectDoesNotExist() {
        Long projectId = 2L;

        when(projectRepository.existsById(projectId)).thenReturn(false);

        boolean result = projectService.existsProjectById(projectId);

        assertFalse(result);
    }

    @Test
    void testExistUserInProject_UserDoesNotExistInProject() {
        Long projectId = 2L;
        Long userId = 2L;

        when(projectRepository.existUserInProject(projectId, userId)).thenReturn(new ArrayList<>());

        boolean result = projectService.existUserInProject(projectId, userId);

        assertFalse(result);
    }

    @Test
    void testExistsByIdIsDeleted() {
        // Arrange
        Long projectId = 1L;

        // Act
        boolean result = projectService.existsByIdIsDeleted(projectId);

        // Assert
        assertFalse(result); // Expecting the method to return false
    }


    @Test
    void testGetCountAllUsersByProjectIdAndRole() {
        // Arrange
        Long projectId = 1L;
        EnumRole enumRole = EnumRole.USER;
        Integer expectedResult = 5;

        // Mock the projectRepository's behavior
        Mockito.when(projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole)).thenReturn(expectedResult);

        // Act
        Integer result = projectService.getCountAllUsersByProjectIdAndRole(projectId, enumRole);

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetCountAllUsersByProjectIdAndRoleWithNullResult() {
        // Arrange
        Long projectId = 2L;
        EnumRole enumRole = EnumRole.USER;
        Integer expectedResult = 0;

        // Mock the projectRepository's behavior to return null
        Mockito.when(projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole)).thenReturn(null);

        // Act
        Integer result = projectService.getCountAllUsersByProjectIdAndRole(projectId, enumRole);

        // Assert
        assertEquals(expectedResult, result);
    }


    @Test
    void testGetCountAllActiveProjects() {
        // Arrange
        Integer expectedResult = 10;

        // Mock the projectRepository's behavior
        Mockito.when(projectRepository.countAllActiveProjects()).thenReturn(expectedResult);

        // Act
        Integer result = projectService.getCountAllActiveProjects();

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetCountAllActiveProjectsWithNullResult() {
        // Arrange
        Integer expectedResult = 0;

        // Mock the projectRepository's behavior to return null
        Mockito.when(projectRepository.countAllActiveProjects()).thenReturn(null);

        // Act
        Integer result = projectService.getCountAllActiveProjects();

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetCountAllInActiveProjects() {
        // Arrange
        Integer expectedResult = 5;

        // Mock the projectRepository's behavior
        Mockito.when(projectRepository.countAllInActiveProjects()).thenReturn(expectedResult);

        // Act
        Integer result = projectService.getCountAllInActiveProjects();

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetCountAllInActiveProjectsWithNullResult() {
        // Arrange
        Integer expectedResult = 0;

        // Mock the projectRepository's behavior to return null
        Mockito.when(projectRepository.countAllInActiveProjects()).thenReturn(null);

        // Act
        Integer result = projectService.getCountAllInActiveProjects();

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void testAddRepositoryToProjectWithException() {
        // Arrange
        Long projectId = 1L;
        Long repoId = 2L;
        // Mock the necessary repository methods to throw an exception
        Mockito.when(projectRepository.findById(projectId)).thenThrow(new RuntimeException("Test Exception"));
        Mockito.when(gitRepositoryRepository.findById(repoId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> responseEntity = projectService.addRepositoryToProject(projectId, repoId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Server Error", responseEntity.getBody());
    }

    @Test
    void testGetProjectsWithoutFigmaURLs() {
        // Create a project with no Figma
        Project projectWithoutFigma = new Project();

        // Create a project with Figma but no FigmaURL
        Project projectWithFigmaNoURL = new Project();
        Figma figmaWithoutURL = new Figma();
        projectWithFigmaNoURL.setFigma(figmaWithoutURL);

        // Create a project with Figma and FigmaURL
        Project projectWithFigmaAndURL = new Project();
        Figma figmaWithURL = new Figma();
        figmaWithURL.setFigmaURL("https://figma.com/project");
        projectWithFigmaAndURL.setFigma(figmaWithURL);

        List<Project> projects = new ArrayList<>();
        projects.add(projectWithoutFigma);
        projects.add(projectWithFigmaNoURL);
        projects.add(projectWithFigmaAndURL);

        when(projectRepository.findAllProjects()).thenReturn(projects);

        List<ProjectDTO> projectDTOs = projectService.getProjectsWithoutFigmaURL();

        assertEquals(2, projectDTOs.size()); // Only two projects should have no Figma URL
    }

    @Test
    void testGetProjectsWithoutGoogleDriveLinks() {
        // Create a project with no Google Drive
        Project projectWithoutGoogleDrive = new Project();

        // Create a project with Google Drive but no Drive Link
        Project projectWithGoogleDriveNoLink = new Project();
        GoogleDrive googleDriveWithoutLink = new GoogleDrive();
        projectWithGoogleDriveNoLink.setGoogleDrive(googleDriveWithoutLink);

        // Create a project with Google Drive and Drive Link
        Project projectWithGoogleDriveAndLink = new Project();
        GoogleDrive googleDriveWithLink = new GoogleDrive();
        googleDriveWithLink.setDriveLink("https://drive.google.com/project");
        projectWithGoogleDriveAndLink.setGoogleDrive(googleDriveWithLink);

        List<Project> projects = new ArrayList<>();
        projects.add(projectWithoutGoogleDrive);
        projects.add(projectWithGoogleDriveNoLink);
        projects.add(projectWithGoogleDriveAndLink);

        when(projectRepository.findAllProjects()).thenReturn(projects);

        List<ProjectDTO> projectDTOs = projectService.getProjectsWithoutGoogleDriveLink();

        assertEquals(2, projectDTOs.size()); // Only two projects should have no Google Drive link
    }

    @Test
    void testGetProjectDetailsById_ProjectExists() {
        // Create a sample project
        Project sampleProject = new Project();
        sampleProject.setProjectName("Sample Project");
        sampleProject.setProjectDescription("Description");
        sampleProject.setDeleted(false);

        // Create a sample user with the role PROJECT_MANAGER
        User sampleUser = new User();
        sampleUser.setName("Project Manager");
        sampleUser.setEnumRole(EnumRole.PROJECT_MANAGER);

        List<User> users = new ArrayList<>();
        users.add(sampleUser);
        sampleProject.setUsers(users);

        // Create a sample GitRepository
        GitRepository sampleRepository = new GitRepository();
        sampleRepository.setName("Repo Name");
        sampleRepository.setDescription("Repo Description");

        List<GitRepository> repositories = new ArrayList<>();
        repositories.add(sampleRepository);
        sampleProject.setRepositories(repositories);

        // Create a sample Figma
        Figma figma = new Figma();
        figma.setFigmaURL("https://figma.com/project");
        sampleProject.setFigma(figma);

        // Create a sample GoogleDrive
        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setDriveLink("https://drive.google.com/project");
        sampleProject.setGoogleDrive(googleDrive);

        // Set lastUpdated
        sampleProject.setLastUpdated(LocalDateTime.now());

        // Mock projectRepository's findById method
        when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(sampleProject));

        // Test the service method
        ProjectDTO projectDTO = projectService.getProjectDetailsById(1L);

        // Verify the results
        assertEquals("Sample Project", projectDTO.getProjectName());
        assertEquals("Description", projectDTO.getProjectDescription());
//        assertEquals(false, projectDTO.getStatus());
        assertEquals("Project Manager", projectDTO.getPmName());

        assertEquals(1, projectDTO.getRepositories().size());
        assertEquals("Repo Name", projectDTO.getRepositories().get(0).getName());
        assertEquals("Repo Description", projectDTO.getRepositories().get(0).getDescription());

        assertEquals("https://figma.com/project", projectDTO.getFigma().getFigmaURL());
        assertEquals("https://drive.google.com/project", projectDTO.getGoogleDrive().getDriveLink());

        // Verify the lastUpdated field, assuming it's a reasonable approximation
        assertEquals(sampleProject.getLastUpdated().getYear(), projectDTO.getLastUpdated().getYear());
    }

    @Test
    void testGetProjectDetailsById_ProjectDoesNotExist() {
        // Mock projectRepository's findById method to return an empty Optional
        when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        // Test the service method
        ProjectDTO projectDTO = projectService.getProjectDetailsById(1L);

        // Verify that it returns an empty ProjectDTO
        ProjectDTO expectedProjectDTO = new ProjectDTO();

        // Use appropriate assertions to compare the expected and actual objects
        assertEquals(expectedProjectDTO.getProjectId(), projectDTO.getProjectId());
        assertEquals(expectedProjectDTO.getProjectName(), projectDTO.getProjectName());
        assertEquals(expectedProjectDTO.getProjectDescription(), projectDTO.getProjectDescription());
        assertEquals(expectedProjectDTO.getLastUpdated(), projectDTO.getLastUpdated());
//        assertEquals(expectedProjectDTO.getStatus(), projectDTO.getStatus());
        assertEquals(expectedProjectDTO.getPmName(), projectDTO.getPmName());
        assertEquals(expectedProjectDTO.getRepositories(), projectDTO.getRepositories());
        assertEquals(expectedProjectDTO.getFigma(), projectDTO.getFigma());
        assertEquals(expectedProjectDTO.getGoogleDrive(), projectDTO.getGoogleDrive());
        assertEquals(expectedProjectDTO.getHelpDocuments(), projectDTO.getHelpDocuments());
    }

    @Test
    void testGetAll_NoProjectsFound() {
        // Mock the projectRepository to return an empty list
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        // Test the service method and expect a NotFoundException
        assertThrows(NotFoundException.class, () -> projectService.getAll());
    }


    @Test
    void testGetAllUsersByProjectIdAndRole_UsersExist() {
        // Create a test user with non-null usernames
        User user = new User();
        UserNames usernames = new UserNames();
        usernames.setUsername("testuser");
        user.setUserNames(usernames);

        List<User> users = new ArrayList<>();
        users.add(user);

        // Mock the repository to return the list of users
        when(projectRepository.findAllUsersByProjectIdAndRole(anyLong(), any(EnumRole.class))).thenReturn(users);

        // Call the service method
        List<UserDTO> userDTOs = projectService.getAllUsersByProjectIdAndRole(1L, EnumRole.USER);

        // Assertions
        assertEquals(1, userDTOs.size());
        assertEquals("testuser", userDTOs.get(0).getGitHubUsername());

    }


    @Test
    void testGetAllUsersByProjectIdAndRole_NoUsers() {
        // Simulate the case where no users are found
        when(projectRepository.findAllUsersByProjectIdAndRole(anyLong(), any(EnumRole.class))).thenReturn(new ArrayList<>());

        // Use assertThrows to check if NotFoundException is thrown
        assertThrows(NotFoundException.class, () -> {
            projectService.getAllUsersByProjectIdAndRole(1L, EnumRole.USER);
        });
    }

    @Test
    void testGetAllUsersByProjectIdAndRole_NullUsernames() {
        // Create a test user with null usernames
        User user = new User();

        List<User> users = new ArrayList<>();
        users.add(user);

        when(projectRepository.findAllUsersByProjectIdAndRole(anyLong(), any(EnumRole.class))).thenReturn(users);

        List<UserDTO> userDTOs = projectService.getAllUsersByProjectIdAndRole(1L, EnumRole.USER);

        assertEquals(1, userDTOs.size());
        assertNull(userDTOs.get(0).getName());
    }


    @Test
    void testGetProjectDetailsById() {
        Long projectId = 1L;
        EnumRole projectManagerRole = EnumRole.PROJECT_MANAGER;

        User user = new User();
        user.setId(1L);
        user.setName("Project Manager");
        user.setEmail("pm@gmail.com");
        user.setEnumRole(projectManagerRole);

        GitRepository gitRepository = new GitRepository();
        gitRepository.setRepoId(1L);
        gitRepository.setName("Repo A");
        gitRepository.setDescription("Description A");

        Figma figma = new Figma();
        figma.setFigmaURL("https://example.com/figma");

        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setDriveLink("https://drive.google.com/drive");

        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Project A");
        project.setProjectDescription("Description A");
        project.setDeleted(false);
        project.setUsers(List.of(user));
        project.setRepositories(List.of(gitRepository));
        project.setFigma(figma);
        project.setGoogleDrive(googleDrive);
        project.setLastUpdated(LocalDateTime.now());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectDTO result = projectService.getProjectDetailsById(projectId);

        assertNotNull(result);
        assertEquals("Project A", result.getProjectName());
        assertEquals("Description A", result.getProjectDescription());
        assertFalse(result.isStatus());
        assertEquals("Project Manager", result.getPmName());
        assertEquals(1, result.getRepositories().size());
        assertEquals("Repo A", result.getRepositories().get(0).getName());
        assertEquals("Description A", result.getRepositories().get(0).getDescription());
        assertEquals("https://example.com/figma", result.getFigma().getFigmaURL());
        assertEquals("https://drive.google.com/drive", result.getGoogleDrive().getDriveLink());
        assertNotNull(result.getLastUpdated());
    }


    @Test
    void testGetProjectDetailsById_NoProjectManager() {
        Long projectId = 1L;

        // Create a project with users where none of them have the PROJECT_MANAGER role
        Project project = new Project();
        project.setProjectName("TestProject");
        project.setProjectDescription("Description");
        project.setDeleted(false);
        project.setLastUpdated(LocalDateTime.now());
        project.setRepositories(new ArrayList<>());

        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setEnumRole(EnumRole.USER);
        user1.setName("User1");
        users.add(user1);

        User user2 = new User();
        user2.setEnumRole(EnumRole.USER);
        user2.setName("User2");
        users.add(user2);

        project.setUsers(users);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectDTO result = projectService.getProjectDetailsById(projectId);

        assertNull(result.getPmName());
        assertTrue(result.getRepositories().isEmpty());
    }


    @Test
    void testAddUserToProject_Success() {
        Long projectId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("User");
        user.setEmail("user@example.com");
        user.setEnumRole(EnumRole.USER);

        Project project = new Project();
        project.setProjectId(projectId);
        project.setProjectName("Project A");
        project.setProjectDescription("Description A");
        project.setUsers(new ArrayList<>());
        project.getUsers().add(user);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.save(project)).thenReturn(project);

        ResponseEntity<Object> responseEntity = projectService.addUserToProject(projectId, userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ProjectUserDTO projectUserDTO = (ProjectUserDTO) responseEntity.getBody();
        assertNotNull(projectUserDTO);
        assertEquals(project.getProjectId(), projectUserDTO.getProjectId());
        assertEquals(project.getProjectName(), projectUserDTO.getProjectName());
        assertEquals(project.getProjectDescription(), projectUserDTO.getProjectDescription());
        assertEquals(2, projectUserDTO.getUsers().size());
        assertEquals(user.getId(), projectUserDTO.getUsers().get(0).getId());
        assertEquals(user.getName(), projectUserDTO.getUsers().get(0).getName());
        assertEquals(user.getEmail(), projectUserDTO.getUsers().get(0).getEmail());
        assertEquals(user.getEnumRole(), projectUserDTO.getUsers().get(0).getEnumRole());
    }

    @Test
    void testAddUserToProject_ProjectNotFound() {
        Long projectId = 1L;
        Long userId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@gmail.com");
        user.setEnumRole(EnumRole.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<Object> responseEntity = projectService.addUserToProject(projectId, userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testAddUserToProject_UserNotFound() {
        Long projectId = 1L;
        Long userId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = projectService.addUserToProject(projectId, userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testAddUserToProject_InternalServerError() {
        Long projectId = 1L;
        Long userId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("Internal Server Error"));

        ResponseEntity<Object> responseEntity = projectService.addUserToProject(projectId, userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testAddUserToProjectByUserIdAndProjectId_UserExistsInProject() {
        Long projectId = 1L;
        Long userId = 2L;

        Project project = new Project();
        project.setProjectId(projectId);
        project.setProjectName("Project1");

        User user = new User();
        user.setId(userId);

        List<User> userList = new ArrayList<>();
        userList.add(user);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.existUserInProject(projectId, userId)).thenReturn(userList);

        ResponseEntity<Object> response = projectService.addUserToProject(projectId, userId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }


    @Test
    void testRemoveUserFromProject_Success() {
        Long projectId = 1L;
        Long userId = 1L;

        List<User> userList = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        Project project = new Project();
        project.setUsers(userList);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        when(projectRepository.save(project)).thenReturn(project);

        ResponseEntity<String> responseEntity = projectService.removeUserFromProject(projectId, userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User removed", responseEntity.getBody());
    }

    @Test
    void testRemoveUserFromProject_ProjectNotFound() {
        Long projectId = 1L;
        Long userId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        ResponseEntity<String> responseEntity = projectService.removeUserFromProject(projectId, userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Project or User not found", responseEntity.getBody());
    }

    @Test
    void testRemoveUserFromProject_UserNotFound() {
        Long projectId = 1L;
        Long userId = 1L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = projectService.removeUserFromProject(projectId, userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Project or User not found", responseEntity.getBody());
    }

    @Test
    void testRemoveUserFromProject_UserNotPartOfProject() {
        Long projectId = 1L;
        Long userId = 1L;

        Project project = new Project();
        project.setUsers(new ArrayList<>());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        ResponseEntity<String> responseEntity = projectService.removeUserFromProject(projectId, userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("User is not part of the project", responseEntity.getBody());
    }

    @Test
    void testDeleteProject_ProjectExistsAndIsDeleted() {
        Long projectId = 1L;
        Project existingProject = new Project();
        existingProject.setDeleted(true);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));

        ResponseEntity<String> response = projectService.deleteProject(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Project doesn't exist", response.getBody());
    }

    @Test
    void testDeleteProject_ProjectExistsAndIsNotDeleted() {
        Long projectId = 2L;
        Project existingProject = new Project();
        existingProject.setDeleted(false);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));

        ResponseEntity<String> response = projectService.deleteProject(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deleted project successfully", response.getBody());
    }

    @Test
    void testDeleteProject_ProjectDoesNotExist() {
        Long projectId = 3L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.deleteProject(projectId));
    }


    @Test
    void testGetAllProjectsWithUsers() {
        List<Project> projects = new ArrayList<>();
        List<User> users = new ArrayList<>();

        Project project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("P1");
        project1.setProjectDescription("Description P1");
        project1.setLastUpdated(LocalDateTime.now());

        Project project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("P2");
        project2.setProjectDescription("Description P2");
        project2.setLastUpdated(LocalDateTime.now());

        User user1 = new User();
        user1.setId(1L);
        user1.setName("U1");
        user1.setEmail("user1@gmail.com");
        user1.setEnumRole(EnumRole.USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("U2");
        user2.setEmail("user2@gmail.com");
        user2.setEnumRole(EnumRole.USER);

        projects.add(project1);
        projects.add(project2);
        users.add(user1);
        users.add(user2);

        when(projectRepository.findAll()).thenReturn(projects);
        when(projectRepository.findAllUsersByProjectId(anyLong())).thenReturn(users);

        List<ProjectWithUsersDTO> result = projectService.getAllProjectsWithUsers();

        assertNotNull(result);
        assertEquals(2, result.size());

        ProjectWithUsersDTO projectWithUsers1 = result.get(0);
        assertEquals(project1.getProjectId(), projectWithUsers1.getProjectId());
        assertEquals(project1.getProjectName(), projectWithUsers1.getProjectName());
        assertEquals(project1.getProjectDescription(), projectWithUsers1.getProjectDescription());
        assertEquals(project1.getLastUpdated(), projectWithUsers1.getLastUpdated());
        assertEquals(2, projectWithUsers1.getUsers().size());

        ProjectWithUsersDTO projectWithUsers2 = result.get(1);
        assertEquals(project2.getProjectId(), projectWithUsers2.getProjectId());
        assertEquals(project2.getProjectName(), projectWithUsers2.getProjectName());
        assertEquals(project2.getProjectDescription(), projectWithUsers2.getProjectDescription());
        assertEquals(project2.getLastUpdated(), projectWithUsers2.getLastUpdated());
        assertEquals(2, projectWithUsers2.getUsers().size());
    }
}






    


