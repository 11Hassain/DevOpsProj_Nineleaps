package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.exceptions.RepositoryDeletionException;
import com.example.devopsproj.model.GitRepository;

import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import com.example.devopsproj.service.interfaces.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

 class GitRepositoryServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GitRepositoryRepository gitRepositoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectServiceImpl projectServiceImpl;
    @Mock
    private ProjectService projectService;

    @InjectMocks
    private GitRepositoryServiceImpl gitRepositoryService;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
     void testCreateRepository_Success() {
        // Arrange
        GitRepository gitRepository = new GitRepository();
        gitRepository.setName("test-repo");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("your-access-token");
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<GitRepository> successResponse = new ResponseEntity<>(gitRepository, HttpStatus.CREATED);

        when(restTemplate.exchange(
                eq("https://api.github.com/user/repos"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(GitRepository.class)))
                .thenReturn(successResponse);

        // Act
        GitRepository createdRepository = gitRepositoryService.createRepository(gitRepository);

        // Assert
        assertNotNull(createdRepository);
        assertEquals("test-repo", createdRepository.getName());

        // Verify that the save method was called
        verify(gitRepositoryRepository, times(1)).save(any(GitRepository.class));
    }

    @Test
     void testCreateRepository_Failure() {
        // Arrange
        GitRepository gitRepository = new GitRepository();
        gitRepository.setName("test-repo");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("your-access-token");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Simulate a failure response from GitHub API
        ResponseEntity<GitRepository> failureResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(
                eq("https://api.github.com/user/repos"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(GitRepository.class)))
                .thenReturn(failureResponse);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> gitRepositoryService.createRepository(gitRepository));

        // Verify that the save method was not called
        verify(gitRepositoryRepository, never()).save(any(GitRepository.class));
    }

    @Test
     void testGetAllRepositories_Success() {
        // Arrange
        List<GitRepository> mockGitRepositories = new ArrayList<>();
        mockGitRepositories.add(new GitRepository("Repo1"));
        mockGitRepositories.add(new GitRepository("Repo2"));


        // Mock the gitRepositoryRepository.findAll() method to return the mockGitRepositories
        when(gitRepositoryRepository.findAll()).thenReturn(mockGitRepositories);

        // Act
        List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositories();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Repo1", result.get(0).getName());
        assertEquals("Repo2", result.get(1).getName());

        // Verify that gitRepositoryRepository.findAll() was called
        verify(gitRepositoryRepository, times(1)).findAll();
    }

    @Test
     void testGetAllRepositories_EmptyList() {
        // Arrange
        List<GitRepository> mockGitRepositories = new ArrayList<>();

        // Mock the gitRepositoryRepository.findAll() method to return an empty list
        when(gitRepositoryRepository.findAll()).thenReturn(mockGitRepositories);

        // Act
        List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositories();

        // Assert
        assertTrue(result.isEmpty());

        // Verify that gitRepositoryRepository.findAll() was called
        verify(gitRepositoryRepository, times(1)).findAll();
    }



    @Test
     void testGetAllReposByRole_Success() {
        // Arrange
        EnumRole enumRole = EnumRole.ADMIN; // Replace with the appropriate role

        // Create a list of Git repositories to be returned by the repository
        List<GitRepository> mockGitRepositories = new ArrayList<>();
        mockGitRepositories.add(new GitRepository(1L, "Repo1", "Description1"));
        mockGitRepositories.add(new GitRepository(2L, "Repo2", "Description2"));

        // Mock the gitRepositoryRepository to return the list of repositories by role
        when(gitRepositoryRepository.findAllByRole(enumRole)).thenReturn(mockGitRepositories);

        // Act
        List<GitRepositoryDTO> result = gitRepositoryService.getAllReposByRole(enumRole);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getRepoId());
        assertEquals("Repo1", result.get(0).getName());
        assertEquals("Description1", result.get(0).getDescription());

        assertEquals(2L, result.get(1).getRepoId());
        assertEquals("Repo2", result.get(1).getName());
        assertEquals("Description2", result.get(1).getDescription());
    }

    // Add more test cases as needed

    // Don't forget to verify that the repository method was called
    @Test
     void testGetAllReposByRole_RepositoryMethodCalled() {
        // Arrange
        EnumRole enumRole = EnumRole.USER; // Replace with the appropriate role

        // Create an empty list to simulate no repositories found by the repository
        List<GitRepository> emptyList = new ArrayList<>();

        // Mock the gitRepositoryRepository to return an empty list
        when(gitRepositoryRepository.findAllByRole(enumRole)).thenReturn(emptyList);

        // Act
        List<GitRepositoryDTO> result = gitRepositoryService.getAllReposByRole(enumRole);

        // Assert
        assertEquals(0, result.size());

        // Verify that the repository method was called once
        verify(gitRepositoryRepository, times(1)).findAllByRole(enumRole);
    }
    @Test
     void testGetRepositoryById_Success() {
        // Arrange
        Long repositoryId = 1L;
        GitRepository expectedRepository = new GitRepository();
        expectedRepository.setRepoId(repositoryId);
        expectedRepository.setName("Repo1");

        // Mock the repository to return the expected repository when findById is called
        when(gitRepositoryRepository.findById(repositoryId)).thenReturn(java.util.Optional.of(expectedRepository));

        // Act
        GitRepository result = gitRepositoryService.getRepositoryById(repositoryId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedRepository, result);
    }

    @Test
    void testDeleteRepository_Successful() {
        Long repoId = 1L;
        GitRepository repository = new GitRepository();
        repository.setRepoId(repoId);
        repository.setName("my-repo");

        when(gitRepositoryRepository.findByRepoId(repoId)).thenReturn(Optional.of(repository));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        assertDoesNotThrow(() -> gitRepositoryService.deleteRepository(repoId));

        verify(gitRepositoryRepository, times(1)).delete(repository);
    }

    @Test
    void testDeleteRepository_Successful_NoContent() {
        Long repoId = 1L;
        GitRepository repository = new GitRepository();
        repository.setRepoId(repoId);
        repository.setName("my-repo");

        when(gitRepositoryRepository.findByRepoId(repoId)).thenReturn(Optional.of(repository));
        doNothing().when(gitRepositoryRepository).delete(repository);

        ResponseEntity<Void> noContentResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        doReturn(noContentResponse).when(restTemplate).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class));

        gitRepositoryService.deleteRepository(repoId);

        verify(gitRepositoryRepository).delete(repository);
    }
    @Test
    void testDeleteRepository_Unsuccessful() {
        Long repoId = 1L;
        GitRepository repository = new GitRepository();
        repository.setRepoId(repoId);
        repository.setName("my-repo");

        when(gitRepositoryRepository.findByRepoId(repoId)).thenReturn(Optional.of(repository));

        HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");

        doThrow(httpClientErrorException)
                .when(restTemplate).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class));

        RepositoryDeletionException exception = assertThrows(RepositoryDeletionException.class, () -> gitRepositoryService.deleteRepository(repoId));

        assertTrue(exception.getMessage().contains("Error deleting repository with repoId " + repoId));
    }


    @Test
    void testDeleteRepository_NotFound() {
        Long repoId = 1L;

        when(gitRepositoryRepository.findByRepoId(repoId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> gitRepositoryService.deleteRepository(repoId));

        assertTrue(exception.getMessage().contains("Repository with repoId " + repoId + " not found."));
    }


    @Test
    void testGetAllReposByRole_NoReposFound() {
        EnumRole role = EnumRole.ADMIN;

        when(gitRepositoryRepository.findAllByRole(role)).thenReturn(Collections.emptyList());

        List<GitRepositoryDTO> result = gitRepositoryService.getAllReposByRole(role);

        assertEquals(0, result.size());
    }

    @Test
    void testGetRepositoryById_RepositoryDoesNotExist() {
        Long repositoryId = 2L;

        when(gitRepositoryRepository.findById(repositoryId)).thenReturn(Optional.empty());

        GitRepository result = gitRepositoryService.getRepositoryById(repositoryId);

        assertNull(result);
    }

    @Test
    void testCreateRepository_Unsuccessful() {
        GitRepository gitRepository = new GitRepository();
        gitRepository.setName("my-repo");

        // Mock an HTTP error (Bad Request in this case)
        HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(GitRepository.class)))
                .thenThrow(httpClientErrorException);

        assertThrows(HttpClientErrorException.class, () -> gitRepositoryService.createRepository(gitRepository));
    }



    @Test
     void testGetAllRepositoriesByProjectWithValidProject() {
        // Create a test project and a list of repositories.
        ProjectDTO testProject = new ProjectDTO(/* initialize with required data */);
        List<GitRepository> testRepositories = new ArrayList<>();
        // Add some repositories to the testRepositories list.

        // Define the behavior of your mocked dependencies.
        Mockito.when(projectServiceImpl.getProjectById(Mockito.anyLong())).thenReturn(testProject);
        Mockito.when(gitRepositoryRepository.findRepositoriesByProject(Mockito.any(Project.class))).thenReturn(testRepositories);


        // Test the method
        List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositoriesByProject(1L); // Assuming project id 1

        // Add assertions based on your test data and business logic.
        assertEquals(testRepositories.size(), result.size());
    }

    @Test
     void testGetAllRepositoriesByProjectWithNonExistentProject() {
        // Mock the ProjectService to return null when looking for a non-existent project.
        Mockito.when(projectServiceImpl.getProjectById(Mockito.anyLong())).thenReturn(null);

        // Test the method
        List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositoriesByProject(2L); // Assuming project id 2

        // Add assertions to verify the result (it should be an empty list).
        assertTrue(result.isEmpty());
    }
}






