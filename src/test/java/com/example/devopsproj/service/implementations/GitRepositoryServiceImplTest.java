package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class GitRepositoryServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GitRepositoryRepository gitRepositoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private GitRepositoryServiceImpl gitRepositoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateRepository_Success() {
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
    public void testCreateRepository_Failure() {
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
    public void testGetAllRepositories_Success() {
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
    public void testGetAllRepositories_EmptyList() {
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
    public void testDeleteRepository_Success() {
        // Arrange
        Long repoId = 1L;
        String repoName = "TestRepo";

        GitRepository repository = new GitRepository();
        repository.setRepoId(repoId);
        repository.setName(repoName);

        when(gitRepositoryRepository.findByRepoId(repoId)).thenReturn(Optional.of(repository));
        when(restTemplate.exchange(
                eq("https://api.github.com/repos/Bindushree-0906/TestRepo"), // Adjust the URL as needed
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // Act
        gitRepositoryService.deleteRepository(repoId);

        // Assert
        verify(gitRepositoryRepository, times(1)).delete(repository);
    }

    @Test
    public void testGetAllReposByRole_Success() {
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
    public void testGetAllReposByRole_RepositoryMethodCalled() {
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
    public void testGetRepositoryById_Success() {
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
    public void testIsAccessTokenValid_ValidToken() {
        // Arrange
        String accessToken = "validAccessToken";
        String responseBody = "{\"message\": \"Hello, world!\"}"; // Simulated response body for a valid token
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        // Mock the behavior of the restTemplate.exchange method to return the responseEntity
        when(restTemplate.exchange(
                eq("https://api.github.com/user"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(responseEntity);

        // Act
        boolean result = gitRepositoryService.isAccessTokenValid(accessToken);

        // Assert
        assertTrue(result);
    }


}
