package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.controller.GitRepositoryController;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

//import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import org.mockito.verification.VerificationMode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;





 class GitRepositoryControllerTest {

    private GitRepositoryService gitRepositoryService;
    private GitRepositoryController gitRepositoryController;

    @BeforeEach
    public void setUp() {
        gitRepositoryService = mock(GitRepositoryService.class);
        gitRepositoryController = new GitRepositoryController(gitRepositoryService);
    }

    @Test
     void testCreateRepository_Success() {
        // Prepare test data
        GitRepository gitRepository = new GitRepository();
        gitRepository.setName("test-repo");

        // Mock the behavior of gitRepositoryService.createRepository
        when(gitRepositoryService.createRepository(gitRepository)).thenReturn(gitRepository);

        // Call the controller method
        ResponseEntity<Object> response = gitRepositoryController.createRepository(gitRepository);

        // Assert the response HTTP status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Assert the response body
        assertEquals(gitRepository, response.getBody());

        // Verify that gitRepositoryService.createRepository method was called with the correct argument
        verify(gitRepositoryService).createRepository(gitRepository);
    }

    @Test
     void testGetAllRepositories_Success() {
        // Prepare mock data
        List<GitRepositoryDTO> mockRepositories = new ArrayList<>();
        mockRepositories.add(new GitRepositoryDTO("1", "repo1")); // Use String for repoId
        mockRepositories.add(new GitRepositoryDTO("2", "repo2")); // Use String for repoId

        // Mock the service method to return the mock data
        when(gitRepositoryService.getAllRepositories()).thenReturn(mockRepositories);

        // Call the controller method
        ResponseEntity<Object> response = gitRepositoryController.getAllRepositories();

        // Verify the HTTP status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the response body
        List<GitRepositoryDTO> responseBody = (List<GitRepositoryDTO>) response.getBody();
        assertEquals(mockRepositories.size(), responseBody.size());

        // You can add more specific assertions for the response body if needed
        for (int i = 0; i < mockRepositories.size(); i++) {
            GitRepositoryDTO mockRepo = mockRepositories.get(i);
            GitRepositoryDTO responseRepo = responseBody.get(i);
            assertEquals(mockRepo.getRepoId(), responseRepo.getRepoId());
            assertEquals(mockRepo.getName(), responseRepo.getName());
            // Add more assertions for other properties if necessary
        }
    }
    @Test
    void testGetAllReposByProject_Success() {
        // Prepare mock data
        Long projectId = 1L; // Project ID to be used in the controller method

        List<GitRepositoryDTO> mockRepositories = new ArrayList<>();
        mockRepositories.add(new GitRepositoryDTO("1", "repo1"));
        mockRepositories.add(new GitRepositoryDTO("2", "repo2"));

        // Mock the service method to return the mock data
        when(gitRepositoryService.getAllRepositoriesByProject(projectId)).thenReturn(mockRepositories);

        // Call the controller method
        ResponseEntity<Object> response = gitRepositoryController.getAllReposByProject(projectId);

        // Verify the HTTP status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the response body
        List<GitRepositoryDTO> responseBody = (List<GitRepositoryDTO>) response.getBody();
        assertEquals(mockRepositories.size(), responseBody.size());

        // You can add more specific assertions for the response body if needed
        for (int i = 0; i < mockRepositories.size(); i++) {
            GitRepositoryDTO mockRepo = mockRepositories.get(i);
            GitRepositoryDTO responseRepo = responseBody.get(i);
            assertEquals(mockRepo.getRepoId(), responseRepo.getRepoId());
            assertEquals(mockRepo.getName(), responseRepo.getName());
            // Add more assertions for other properties if necessary
        }
    }

    @Test
   void testGetAllReposByRole_Success() {
        // Prepare mock data
        String role = "ADMIN"; // Role to be used in the controller method

        List<GitRepositoryDTO> mockRepositories = new ArrayList<>();
        mockRepositories.add(new GitRepositoryDTO("1", "repo1"));
        mockRepositories.add(new GitRepositoryDTO("2", "repo2"));

        // Mock the service method to return the mock data
        when(gitRepositoryService.getAllReposByRole(EnumRole.ADMIN)).thenReturn(mockRepositories);

        // Call the controller method
        ResponseEntity<Object> response = gitRepositoryController.getAllReposByRole(role);

        // Verify the HTTP status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the response body
        List<GitRepositoryDTO> responseBody = (List<GitRepositoryDTO>) response.getBody();
        assertEquals(mockRepositories.size(), responseBody.size());

        // You can add more specific assertions for the response body if needed
        for (int i = 0; i < mockRepositories.size(); i++) {
            GitRepositoryDTO mockRepo = mockRepositories.get(i);
            GitRepositoryDTO responseRepo = responseBody.get(i);
            assertEquals(mockRepo.getRepoId(), responseRepo.getRepoId());
            assertEquals(mockRepo.getName(), responseRepo.getName());
            // Add more assertions for other properties if necessary
        }
    }
    @Test
     void testGetRepositoryById_Success() {
        // Prepare mock data
        Long repoId = 1L; // Repository ID to be used in the controller method
        String repoName = "repo1";
        String repoDescription = "Description of repo1";

        GitRepository mockRepository = new GitRepository();
        mockRepository.setRepoId(repoId);
        mockRepository.setName(repoName);
        mockRepository.setDescription(repoDescription);

        // Mock the service method to return the mock data
        when(gitRepositoryService.getRepositoryById(repoId)).thenReturn(mockRepository);

        // Call the controller method
        ResponseEntity<Object> response = gitRepositoryController.getRepositoryById(repoId);

        // Verify the HTTP status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the response body
        GitRepositoryDTO responseBody = (GitRepositoryDTO) response.getBody();
        assertEquals(repoName, responseBody.getName());
        assertEquals(repoDescription, responseBody.getDescription());
    }

     @Test
     void testDeleteRepository_Success() {
         // Prepare mock data
         Long repoId = 1L; // Repository ID to be used in the controller method

         // Mock the service method to do nothing (since it's a void method)
         doNothing().when(gitRepositoryService).deleteRepository(repoId);

         // Call the controller method
         ResponseEntity<Object> response = gitRepositoryController.deleteRepository(repoId);

         // Verify that gitRepositoryService.deleteRepository method was called once
         verify(gitRepositoryService).deleteRepository(repoId);

         // Verify the HTTP status code
         assertEquals(HttpStatus.OK, response.getStatusCode());

         // Verify the response message
         String responseBody = (String) response.getBody();
         assertEquals("Soft-deleted successfully", responseBody); // Update the expected message
     }



 }