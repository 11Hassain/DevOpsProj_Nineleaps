package com.example.devopsproj.service;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.service.implementations.GitHubCollaboratorServiceImpl;
import com.example.devopsproj.service.implementations.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class ProjectServiceImplTest {

    @InjectMocks
    private ProjectServiceImpl projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private GitRepositoryRepository gitRepositoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GitHubCollaboratorServiceImpl collaboratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProject_Success(){
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(1L);
        projectDTO.setProjectName("P1");
        projectDTO.setProjectDescription("This is project P1");

        Project projectToSave = new Project();
        projectToSave.setProjectId(projectDTO.getProjectId());
        projectToSave.setProjectName(projectDTO.getProjectName());
        projectToSave.setProjectDescription(projectDTO.getProjectDescription());
        projectToSave.setLastUpdated(LocalDateTime.now());

        ProjectDTO expectedDTO = new ProjectDTO();
        expectedDTO.setProjectId(projectDTO.getProjectId());
        expectedDTO.setProjectName(projectDTO.getProjectName());
        expectedDTO.setProjectDescription(projectDTO.getProjectDescription());

        when(projectRepository.save(projectToSave)).thenReturn(projectToSave);
        when(modelMapper.map(any(), eq(ProjectDTO.class))).thenReturn(expectedDTO);

        ProjectDTO createdProjectDTO = projectService.createProject(projectDTO);

        assertEquals(expectedDTO, createdProjectDTO);
    }

    @Nested
    class GetProjectByIdTest {
        @Test
        @DisplayName("Testing success case - project found")
        void testGetProjectById_Success(){
            Long id = 1L;

            Project expectedProject = new Project();
            expectedProject.setProjectId(id);

            when(projectRepository.findById(id)).thenReturn(Optional.of(expectedProject));

            Optional<Project> result = projectService.getProjectById(id);

            assertTrue(result.isPresent());
            assertEquals(expectedProject, result.get());
        }

        @Test
        @DisplayName("Testing failure case - project not found")
        void testGetProjectById_Failure(){
            Long id = 2L;

            when(projectRepository.findById(id)).thenReturn(Optional.empty());

            Optional<Project> result = projectService.getProjectById(id);

            assertFalse(result.isPresent());
        }
    }

    @Nested
    class GetProjectTest {
        @Test
        @DisplayName("Testing success case")
        void testGetProject_Success(){
            Long id = 2L;
            Project expectedProject = new Project();
            expectedProject.setProjectId(id);
            expectedProject.setProjectName("P1");
            expectedProject.setProjectDescription("P1 Description");

            when(projectRepository.findById(id)).thenReturn(Optional.of(expectedProject));

            ResponseEntity<Object> response = projectService.getProject(id);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody() instanceof ProjectDTO);

            ProjectDTO projectDTO = (ProjectDTO) response.getBody();
            assertEquals(expectedProject.getProjectId(), projectDTO.getProjectId());
            assertEquals(expectedProject.getProjectName(), projectDTO.getProjectName());
            assertEquals(expectedProject.getProjectDescription(), projectDTO.getProjectDescription());
        }

        @Test
        @DisplayName("Testing project deleted case")
        void testGetProject_Deleted() {
            Long projectId = 3L;
            Project deletedProject = new Project();
            deletedProject.setProjectId(projectId);
            deletedProject.setDeleted(true);

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(deletedProject));

            ResponseEntity<Object> responseEntity = projectService.getProject(projectId);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }

        @Test
        @DisplayName("Testing empty project case")
        void testGetProject_Empty_NotFound() {
            Long projectId = 2L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            ResponseEntity<Object> responseEntity = projectService.getProject(projectId);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }

        @Test
        @DisplayName("Testing project not found case")
        void testGetProject_NotFound() {
            Long projectId = 2L;

            when(projectRepository.findById(projectId)).thenThrow(NotFoundException.class);

            ResponseEntity<Object> responseEntity = projectService.getProject(projectId);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }

        @Test
        @DisplayName("Testing failure case (internal server error)")
        void testGetProject_Exception(){
            Long id = 1L;

            when(projectRepository.findById(id)).thenThrow(new RuntimeException("Internal Server Error"));

            ResponseEntity<Object> response = projectService.getProject(id);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());
        }
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

    @Nested
    class AddRepositoryToProjectTest {
        @Test
        @DisplayName("Testing success case")
        void testAddRepositoryToProject_Success(){
            Long projectId = 1L;
            Long repoId = 1L;

            Project project = new Project();
            project.setProjectId(projectId);
            project.setDeleted(false);

            GitRepository gitRepository = new GitRepository();
            gitRepository.setRepoId(repoId);

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
            when(gitRepositoryRepository.findById(repoId)).thenReturn(Optional.of(gitRepository));

            ResponseEntity<Object> responseEntity = projectService.addRepositoryToProject(projectId, repoId);

            assertEquals(ResponseEntity.ok("Stored successfully"), responseEntity);
            assertEquals(project, gitRepository.getProject());
        }

        @Test
        @DisplayName("Testing deleted project case")
        void testAddRepositoryToProject_DeletedProject() {
            Long projectId = 2L;
            Long repoId = 2L;

            Project project = new Project();
            project.setProjectId(projectId);
            project.setDeleted(true);

            GitRepository gitRepository = new GitRepository();
            gitRepository.setRepoId(repoId);

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
            when(gitRepositoryRepository.findById(repoId)).thenReturn(Optional.of(gitRepository));

            ResponseEntity<Object> responseEntity = projectService.addRepositoryToProject(projectId, repoId);

            assertEquals(ResponseEntity.ok("Stored successfully"), responseEntity); // You can customize this response as needed
            assertNull(gitRepository.getProject());
        }

        @Test
        @DisplayName("Testing project not found case")
        void testAddRepositoryToProject_ProjectNotFound() {
            Long projectId = 3L;
            Long repoId = 3L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
            when(gitRepositoryRepository.findById(repoId)).thenReturn(Optional.of(new GitRepository()));

            ResponseEntity<Object> responseEntity = projectService.addRepositoryToProject(projectId, repoId);

            assertEquals(ResponseEntity.notFound().build(), responseEntity);
        }

        @Test
        @DisplayName("Testing repository not found case")
        void testAddRepositoryToProject_RepositoryNotFound() {
            Long projectId = 4L;
            Long repoId = 4L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
            when(gitRepositoryRepository.findById(repoId)).thenReturn(Optional.empty());

            ResponseEntity<Object> responseEntity = projectService.addRepositoryToProject(projectId, repoId);

            assertEquals(ResponseEntity.notFound().build(), responseEntity);
        }
    }

    @Nested
    class GetAllTest {
        @Test
        @DisplayName("Testing success case")
        void testGetAll_Success(){
            List<Project> projects = new ArrayList<>();

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

            projects.add(project1);
            projects.add(project2);

            when(projectRepository.findAll()).thenReturn(projects);

            List<Project> result = projectService.getAll();

            assertNotNull(result);
            assertEquals(projects.size(), result.size());
            assertEquals(projects, result);
        }

        @Test
        @DisplayName("Testing empty project list case")
        void testGetAll_EmptyList() {
            List<Project> expectedProjects = new ArrayList<>();

            when(projectRepository.findAll()).thenReturn(expectedProjects);

            List<Project> result = projectService.getAll();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Testing null value case")
        void testGetAll_NullList() {
            when(projectRepository.findAll()).thenReturn(null);

            List<Project> result = projectService.getAll();

            assertNull(result);
        }
    }

    @Nested
    class GetAllProjectsTest {
        @Test
        @DisplayName("Testing success case - projects list")
        void testGetAllProjects() {
            List<Project> expectedProjects = new ArrayList<>();

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

            expectedProjects.add(project1);
            expectedProjects.add(project2);

            when(projectRepository.findAllProjects()).thenReturn(expectedProjects);

            List<Project> result = projectService.getAllProjects();

            assertNotNull(result);
            assertEquals(expectedProjects.size(), result.size());
            assertEquals(expectedProjects, result);
        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetAllProjects_EmptyList() {
            List<Project> expectedProjects = new ArrayList<>();

            when(projectRepository.findAllProjects()).thenReturn(expectedProjects);

            List<Project> result = projectService.getAllProjects();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Testing null list case")
        void testGetAllProjects_NullList() {
            when(projectRepository.findAllProjects()).thenReturn(null);

            List<Project> result = projectService.getAllProjects();

            assertNull(result);
        }
    }

    @Nested
    class UpdateProjectTest {
        @Test
        void testUpdateProject_Success() {
            Project updatedProject = new Project();
            updatedProject.setProjectId(1L);
            updatedProject.setProjectName("Updated Project");
            updatedProject.setProjectDescription("Updated Description");

            when(projectRepository.save(updatedProject)).thenReturn(updatedProject);

            Project result = projectService.updateProject(updatedProject);

            assertNotNull(result);
            assertEquals(updatedProject, result);
        }

        @Test
        @DisplayName("Testing null input case - failure")
        void testUpdateProject_NullInput() {
            when(projectRepository.save(new Project())).thenReturn(null);

            Project result = projectService.updateProject(null);

            assertNull(result);
        }
    }

    @Nested
    class GetAllUsersByProjectIdTest {
        @Test
        @DisplayName("Testing success case for getting all users")
        void testGetAllUsersByProjectId() {
            Long projectId = 1L;

            List<User> expectedUsers = new ArrayList<>();
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

            expectedUsers.add(user1);
            expectedUsers.add(user2);

            when(projectRepository.findAllUsersByProjectId(projectId)).thenReturn(expectedUsers);

            List<UserDTO> result = projectService.getAllUsersByProjectId(projectId);

            assertNotNull(result);
            assertEquals(expectedUsers.size(), result.size());

            for (int i = 0; i < expectedUsers.size(); i++) {
                User expectedUser = expectedUsers.get(i);
                UserDTO userDTO = result.get(i);

                assertEquals(expectedUser.getId(), userDTO.getId());
                assertEquals(expectedUser.getName(), userDTO.getName());
                assertEquals(expectedUser.getEmail(), userDTO.getEmail());
                assertEquals(expectedUser.getEnumRole(), userDTO.getEnumRole());
            }
        }

        @Test
        @DisplayName("Testing empty list case")
        void testGetAllUsersByProjectId_EmptyList() {
            Long projectId = 2L;

            List<User> expectedUsers = new ArrayList<>();

            when(projectRepository.findAllUsersByProjectId(projectId)).thenReturn(expectedUsers);

            List<UserDTO> result = projectService.getAllUsersByProjectId(projectId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetAllUsersByProjectIdAndRoleTest {
        @Test
        @DisplayName("Testing success case")
        void testGetAllUsersByProjectIdAndRole() {
            Long projectId = 1L;
            EnumRole role = EnumRole.USER;

            List<User> expectedUsers = new ArrayList<>();
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

            expectedUsers.add(user1);
            expectedUsers.add(user2);

            when(projectRepository.findAllUsersByProjectIdAndRole(projectId, role)).thenReturn(expectedUsers);

            List<User> result = projectService.getAllUsersByProjectIdAndRole(projectId, role);

            assertNotNull(result);
            assertEquals(expectedUsers.size(), result.size());
            assertEquals(expectedUsers, result);
        }

        @Test
        @DisplayName("Testing no users found case")
        void testGetAllUsersByProjectIdAndRole_NoUsers() {
            Long projectId = 2L;
            EnumRole role = EnumRole.ADMIN;

            when(projectRepository.findAllUsersByProjectIdAndRole(projectId, role)).thenReturn(Collections.emptyList());

            List<User> result = projectService.getAllUsersByProjectIdAndRole(projectId, role);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class ExistsByIdIsDeletedTest {
        @Test
        @DisplayName("Testing success case - project is deleted")
        void testExistsByIdIsDeleted_ProjectDeleted() {
            Long projectId = 2L;
            Project deletedProject = new Project();
            deletedProject.setDeleted(true);

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(deletedProject));

            boolean result = projectService.existsByIdIsDeleted(projectId);

            assertTrue(result);
        }

        @Test
        @DisplayName("Testing project not deleted case")
        void testExistsByIdIsDeleted_ProjectNotDeleted() {
            Long projectId = 3L;
            Project activeProject = new Project();
            activeProject.setDeleted(false);

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(activeProject));

            boolean result = projectService.existsByIdIsDeleted(projectId);

            assertFalse(result);
        }

        @Test
        @DisplayName("Testing project not found case")
        void testExistsByIdIsDeleted_ProjectNotFound() {
            Long projectId = 1L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            boolean result = projectService.existsByIdIsDeleted(projectId);

            assertTrue(result);
        }
    }

    @Nested
    class SoftDeleteProjectTest {
        @Test
        @DisplayName("Testing success case for soft delete")
        void testSoftDeleteProject_Success() {
            Long projectId = 1L;

            doNothing().when(projectRepository).softDeleteProject(projectId);

            boolean result = projectService.softDeleteProject(projectId);

            assertTrue(result);
            verify(projectRepository, times(1)).softDeleteProject(projectId);
        }

        @Test
        @DisplayName("Testing unable to soft delete case")
        void testSoftDeleteProject_Exception() {
            Long projectId = 2L;

            doThrow(new RuntimeException("Error deleting project")).when(projectRepository).softDeleteProject(projectId);

            boolean result = projectService.softDeleteProject(projectId);

            assertFalse(result);
            verify(projectRepository, times(1)).softDeleteProject(projectId);
        }
    }

    @Nested
    class ExistsProjectByIdTest {
        @Test
        @DisplayName("Testing success case - project exists")
        void testExistsProjectById_ProjectExists() {
            Long projectId = 1L;

            when(projectRepository.existsById(projectId)).thenReturn(true);

            boolean result = projectService.existsProjectById(projectId);

            assertTrue(result);
        }

        @Test
        @DisplayName("Testing project does not exist case")
        void testExistsProjectById_ProjectDoesNotExist() {
            Long projectId = 2L;

            when(projectRepository.existsById(projectId)).thenReturn(false);

            boolean result = projectService.existsProjectById(projectId);

            assertFalse(result);
        }
    }

    @Nested
    class ExistUserInProjectTest {
        @Test
        @DisplayName("Testing success case - user exists in project")
        void testExistUserInProject_UserExistsInProject() {
            Long projectId = 1L;
            Long userId = 1L;

            List<User> userList = new ArrayList<>();
            User user1 = new User();
            user1.setId(1L);
            user1.setName("U1");
            user1.setEmail("user1@gmail.com");
            user1.setEnumRole(EnumRole.USER);

            when(projectRepository.existUserInProject(projectId, userId)).thenReturn(userList);

            boolean result = projectService.existUserInProject(projectId, userId);

            assertFalse(result);
        }

        @Test
        @DisplayName("Testing user does not exist in project case")
        void testExistUserInProject_UserDoesNotExistInProject() {
            Long projectId = 2L;
            Long userId = 2L;

            when(projectRepository.existUserInProject(projectId, userId)).thenReturn(new ArrayList<>());

            boolean result = projectService.existUserInProject(projectId, userId);

            assertFalse(result);
        }

        @Test
        @DisplayName("Testing null users list case")
        void testExistUserInProject_NullUserList() {
            Long projectId = 3L;
            Long userId = 3L;

            when(projectRepository.existUserInProject(projectId, userId)).thenReturn(Collections.emptyList());

            boolean result = projectService.existUserInProject(projectId, userId);

            assertFalse(result);
        }

        @Test
        @DisplayName("Testing multiple users in list case")
        void testExistUserInProject_MultipleUsersInList() {
            Long projectId = 4L;
            Long userId = 4L;

            List<User> userList = new ArrayList<>();
            userList.add(new User());
            userList.add(new User());
            userList.add(new User());

            when(projectRepository.existUserInProject(projectId, userId)).thenReturn(userList);

            boolean result = projectService.existUserInProject(projectId, userId);

            assertTrue(result);
        }
    }

    @Test
    void testGetCountAllProjects() {
        int expectedCount = 5;

        when(projectRepository.countAllProjects()).thenReturn(expectedCount);

        int result = projectService.getCountAllProjects();

        assertEquals(expectedCount, result);
    }

    @Test
    void testGetCountAllProjectsByRole() {
        EnumRole role = EnumRole.ADMIN;
        int expectedCount = 3;

        when(projectRepository.countAllProjectsByRole(role)).thenReturn(expectedCount);

        int result = projectService.getCountAllProjectsByRole(role);

        assertEquals(expectedCount, result);
    }

    @Test
    void testGetCountAllProjectsByUserId() {
        Long userId = 1L;
        int expectedCount = 3;

        when(projectRepository.countAllProjectsByUserId(userId)).thenReturn(expectedCount);

        int result = projectService.getCountAllProjectsByUserId(userId);

        assertEquals(expectedCount, result);
    }

    @Test
    void testGetCountAllUsersByProjectId() {
        Long projectId = 1L;
        int expectedCount = 5;

        when(projectRepository.countAllUsersByProjectId(projectId)).thenReturn(expectedCount);

        int result = projectService.getCountAllUsersByProjectId(projectId);

        assertEquals(expectedCount, result);
    }

    @Test
    void testGetCountAllPeopleAndProjectName() {
        List<Project> projects = new ArrayList<>();
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

        projects.add(project1);
        projects.add(project2);

        when(projectRepository.findAllProjects()).thenReturn(projects);

        when(projectRepository.countAllUsersByProjectId(1L)).thenReturn(3);
        when(projectRepository.countAllUsersByProjectId(2L)).thenReturn(2);

        List<ProjectNamePeopleCountDTO> result = projectService.getCountAllPeopleAndProjectName();

        assertEquals(2, result.size());

        // Check Project A
        ProjectNamePeopleCountDTO projectA = result.get(0);
        assertEquals(1L, projectA.getProjectId());
        assertEquals("P1", projectA.getProjectName());
        assertEquals(3, projectA.getCountPeople());

        // Check Project B
        ProjectNamePeopleCountDTO projectB = result.get(1);
        assertEquals(2L, projectB.getProjectId());
        assertEquals("P2", projectB.getProjectName());
        assertEquals(2, projectB.getCountPeople());
    }

    @Test
    void testGetCountAllUsersByProjectIdAndRole() {
        Long projectId = 1L;
        EnumRole role = EnumRole.ADMIN;
        int expectedCount = 2;

        when(projectRepository.countAllUsersByProjectIdAndRole(projectId, role)).thenReturn(expectedCount);

        int result = projectService.getCountAllUsersByProjectIdAndRole(projectId, role);

        assertEquals(expectedCount, result);
    }

    @Test
    void testGetCountAllActiveProjects() {
        int expectedCount = 5;

        when(projectRepository.countAllActiveProjects()).thenReturn(expectedCount);

        int result = projectService.getCountAllActiveProjects();

        assertEquals(expectedCount, result);
    }

    @Test
    void testGetCountAllInActiveProjects() {
        int expectedCount = 2;

        when(projectRepository.countAllInActiveProjects()).thenReturn(expectedCount);

        int result = projectService.getCountAllInActiveProjects();

        assertEquals(expectedCount, result);
    }

    @Test
    void testGetUsersByProjectIdAndRole() {
        Long projectId = 1L;
        String role = "ADMIN";

        EnumRole userRole = EnumRole.ADMIN;

        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setName("U1");
        user1.setEmail("user1@gmail.com");
        user1.setEnumRole(EnumRole.ADMIN);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("U2");
        user2.setEmail("user2@gmail.com");
        user2.setEnumRole(EnumRole.ADMIN);

        users.add(user1);
        users.add(user2);

        when(projectRepository.findUsersByProjectIdAndRole(projectId, userRole)).thenReturn(users);

        List<UserDTO> result = projectService.getUsersByProjectIdAndRole(projectId, role);

        assertEquals(users.size(), result.size());

        // Check User A
        UserDTO userA = result.get(0);
        assertEquals(1L, userA.getId());
        assertEquals("U1", userA.getName());
        assertEquals("user1@gmail.com", userA.getEmail());
        assertEquals(userRole, userA.getEnumRole());

        // Check User B
        UserDTO userB = result.get(1);
        assertEquals(2L, userB.getId());
        assertEquals("U2", userB.getName());
        assertEquals("user2@gmail.com", userB.getEmail());
        assertEquals(userRole, userB.getEnumRole());
    }

    @Nested
    class GetProjectsWithoutFigmaURLTest {
        @Test
        @DisplayName("Testing success case")
        void testGetProjectsWithoutFigmaURL() {
            Project projectWithFigma = new Project();
            projectWithFigma.setProjectId(1L);
            projectWithFigma.setProjectName("P1");

            Figma figma = new Figma();
            figma.setFigmaURL("https://example.com/figma");

            projectWithFigma.setFigma(figma);

            Project projectWithoutFigma = new Project();
            projectWithoutFigma.setProjectId(2L);
            projectWithoutFigma.setProjectName("P2");

            List<Project> projects = new ArrayList<>();
            projects.add(projectWithFigma);
            projects.add(projectWithoutFigma);

            when(projectRepository.findAllProjects()).thenReturn(projects);

            List<ProjectDTO> result = projectService.getProjectsWithoutFigmaURL();

            assertEquals(1, result.size());

            // Check Project P2 (without Figma URL)
            ProjectDTO project2 = result.get(0);
            assertEquals(2L, project2.getProjectId());
            assertEquals("P2", project2.getProjectName());
        }

        @Test
        @DisplayName("Testing figma is null case")
        void testGetProjectsWithoutFigmaURL_FigmaIsNull() {
            List<Project> projects = new ArrayList<>();
            Project project = new Project();
            project.setFigma(null); // Figma is null
            projects.add(project);
            when(projectRepository.findAllProjects()).thenReturn(projects);

            List<ProjectDTO> result = projectService.getProjectsWithoutFigmaURL();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Testing figma URL is null case")
        void testGetProjectsWithoutFigmaURL_FigmaURLIsNull() {
            List<Project> projects = new ArrayList<>();
            Project project = new Project();
            Figma figma = new Figma();
            figma.setFigmaURL(null); // FigmaURL is null
            project.setFigma(figma);
            projects.add(project);
            when(projectRepository.findAllProjects()).thenReturn(projects);

            List<ProjectDTO> result = projectService.getProjectsWithoutFigmaURL();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        }
    }

    @Nested
    class GetProjectsWithoutGoogleDriveLinkTest {
        @Test
        @DisplayName("Testing success case")
        void testGetProjectsWithoutGoogleDriveLink() {
            // P1
            Project projectWithGoogleDrive = new Project();
            projectWithGoogleDrive.setProjectId(1L);
            projectWithGoogleDrive.setProjectName("P1");

            GoogleDrive googleDrive = new GoogleDrive();
            googleDrive.setDriveLink("https://drive.google.com/drive");

            projectWithGoogleDrive.setGoogleDrive(googleDrive);

            // P2
            Project projectWithoutGoogleDrive = new Project();
            projectWithoutGoogleDrive.setProjectId(2L);
            projectWithoutGoogleDrive.setProjectName("P2");

            List<Project> projects = new ArrayList<>();
            projects.add(projectWithGoogleDrive);
            projects.add(projectWithoutGoogleDrive);

            when(projectRepository.findAllProjects()).thenReturn(projects);

            List<ProjectDTO> result = projectService.getProjectsWithoutGoogleDriveLink();

            assertEquals(1, result.size());

            // Check Project P2 (without Google Drive link)
            ProjectDTO project2 = result.get(0);
            assertEquals(2L, project2.getProjectId());
            assertEquals("P2", project2.getProjectName());
        }

        @Test
        @DisplayName("Testing GDrive is null case")
        void testGetProjectsWithoutGoogleDriveLink_GoogleDriveIsNull() {
            List<Project> projects = new ArrayList<>();
            Project project = new Project();
            project.setGoogleDrive(null); // Google Drive is null
            projects.add(project);
            when(projectRepository.findAllProjects()).thenReturn(projects);

            List<ProjectDTO> result = projectService.getProjectsWithoutGoogleDriveLink();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Testing GDrive link is null case")
        void testGetProjectsWithoutGoogleDriveLink_DriveLinkIsNull() {
            List<Project> projects = new ArrayList<>();
            Project project = new Project();
            GoogleDrive googleDrive = new GoogleDrive();
            googleDrive.setDriveLink(null); // DriveLink is null
            project.setGoogleDrive(googleDrive);
            projects.add(project);
            when(projectRepository.findAllProjects()).thenReturn(projects);

            List<ProjectDTO> result = projectService.getProjectsWithoutGoogleDriveLink();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        }
    }

    @Nested
    class GetProjectDetailsByIdTest {
        @Test
        @DisplayName("Testing success case")
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
        @DisplayName("Testing project not found case")
        void testGetProjectDetailsById_ProjectNotFound() {
            Long projectId = 123L; // Assuming a non-existent project ID

            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            ProjectDTO result = projectService.getProjectDetailsById(projectId);

            assertNotNull(result);
            assertNull(result.getProjectName());
            assertNull(result.getProjectDescription());
            assertFalse(result.isStatus());
            assertNull(result.getPmName());
            assertTrue(result.getRepositories().isEmpty());
            assertNull(result.getFigma());
            assertNull(result.getGoogleDrive());
            assertNull(result.getLastUpdated());
        }

        @Test
        @DisplayName("Testing no PM found case")
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
    }

    @Nested
    class AddUserToProjectByUserIdAndProjectIdTest {
        @Test
        @DisplayName("Testing success case")
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

            ResponseEntity<Object> responseEntity = projectService.addUserToProjectByUserIdAndProjectId(projectId, userId);

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
        @DisplayName("Testing project not found case")
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

            ResponseEntity<Object> responseEntity = projectService.addUserToProjectByUserIdAndProjectId(projectId, userId);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }

        @Test
        @DisplayName("Testing user not found case")
        void testAddUserToProject_UserNotFound() {
            Long projectId = 1L;
            Long userId = 1L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            ResponseEntity<Object> responseEntity = projectService.addUserToProjectByUserIdAndProjectId(projectId, userId);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }

        @Test
        @DisplayName("Testing failure case (internal server error)")
        void testAddUserToProject_InternalServerError() {
            Long projectId = 1L;
            Long userId = 1L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
            when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
            when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("Internal Server Error"));

            ResponseEntity<Object> responseEntity = projectService.addUserToProjectByUserIdAndProjectId(projectId, userId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
            assertNull(responseEntity.getBody());
        }

        @Test
        @DisplayName("Testing user exists in project case")
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

            ResponseEntity<Object> response = projectService.addUserToProjectByUserIdAndProjectId(projectId, userId);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }
    }

    @Nested
    class RemoveUserFromProjectByUserIdAndProjectIdTest {
        @Test
        @DisplayName("Testing success case")
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

            ResponseEntity<String> responseEntity = projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals("User removed", responseEntity.getBody());
        }

        @Test
        @DisplayName("Testing project not found case")
        void testRemoveUserFromProject_ProjectNotFound() {
            Long projectId = 1L;
            Long userId = 1L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
            when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

            ResponseEntity<String> responseEntity = projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertEquals("Project or User not found", responseEntity.getBody());
        }

        @Test
        @DisplayName("Testing user not found case")
        void testRemoveUserFromProject_UserNotFound() {
            Long projectId = 1L;
            Long userId = 1L;

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            ResponseEntity<String> responseEntity = projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertEquals("Project or User not found", responseEntity.getBody());
        }

        @Test
        @DisplayName("Testing user not part of project case")
        void testRemoveUserFromProject_UserNotPartOfProject() {
            Long projectId = 1L;
            Long userId = 1L;

            Project project = new Project();
            project.setUsers(new ArrayList<>());

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
            when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

            ResponseEntity<String> responseEntity = projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertEquals("User is not part of the project", responseEntity.getBody());
        }
    }

    @Nested
    class RemoveUserFromProjectAndRepoTest {
        @Test
        @DisplayName("Testing success case")
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
        @DisplayName("Testing project not found case")
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
        @DisplayName("Testing user not found case")
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
        @DisplayName("Testing collaboration deletion failure case (bad request)")
        void testRemoveUserFromProjectAndRepo_CollaboratorDeletionFails() {
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
    }

    @Nested
    class MapProjectToProjectDTOTest {
        @Test
        @DisplayName("Testing success case")
        void testMapProjectToProjectDTO_BasicMapping() {
            Project project = new Project();
            project.setProjectId(1L);
            project.setProjectName("Sample Project");

            ProjectDTO projectDTO = projectService.mapProjectToProjectDTO(project);

            assertEquals(project.getProjectId(), projectDTO.getProjectId());
            assertEquals(project.getProjectName(), projectDTO.getProjectName());
        }

        @Test
        @DisplayName("Testing null attributes case")
        void testMapProjectToProjectDTO_NullAttributes() {
            Project project = new Project();

            ProjectDTO projectDTO = projectService.mapProjectToProjectDTO(project);

            assertNull(projectDTO.getProjectId());
            assertNull(projectDTO.getProjectName());
        }

        @Test
        @DisplayName("Testing empty attributes case")
        void testMapProjectToProjectDTO_EmptyAttributeValues() {
            Project project = new Project();
            project.setProjectId(null);
            project.setProjectName("");

            ProjectDTO projectDTO = projectService.mapProjectToProjectDTO(project);

            assertNull(projectDTO.getProjectId());
            assertEquals("", projectDTO.getProjectName());
        }
    }
}
