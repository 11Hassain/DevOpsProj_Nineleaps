package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;

//import com.example.devopsproj.exceptions.NotFoundException;
//import com.example.devopsproj.exceptions.FigmaNotFoundException;
import com.example.devopsproj.exceptions.FigmaNotFoundException;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.Project;
//import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
//import com.example.devopsproj.repository.GitRepositoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitRepositoryServiceImpl implements GitRepositoryService {

    private static final String API_BASE_URL = "https://api.github.com";
    private static final String USER_REPOS_ENDPOINT = "/user/repos";
    private static final String REPOS_ENDPOINT = "/repos/Bindushree-0906";
    private static final String OWNER = "Bindushree-0906";

    private static final String GITHUB_ACCESS_TOKEN = System.getenv("GITHUB_ACCESS_TOKEN");

    private final RestTemplate restTemplate;
    private final GitRepositoryRepository gitRepositoryRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;


    // Create a new Git repository
    @Override
    @Transactional
    public GitRepository createRepository(GitRepository gitRepository) {
        // Create HTTP headers with the GitHub access token and content type
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(GITHUB_ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HTTP request entity with the GitRepository and headers
        HttpEntity<GitRepository> requestEntity = new HttpEntity<>(gitRepository, headers);

        // Send an HTTP POST request to create the repository on GitHub
        ResponseEntity<GitRepository> responseEntity = restTemplate.exchange(
                API_BASE_URL + USER_REPOS_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                GitRepository.class);

        // Check if the response status code indicates success (HTTP 201 - Created)
        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            GitRepository createdGitRepository = responseEntity.getBody();
            // Save the created GitRepository to the local repository
            gitRepositoryRepository.save(createdGitRepository);
            return createdGitRepository;
        } else {
            // Throw a RuntimeException if the repository creation fails
            throw new RuntimeException("Error creating repository " + gitRepository.getName() + ". Response: " + responseEntity.getBody());
        }
    }

    // Get a list of all Git repositories
    @Override
    @Transactional(readOnly = true)
    public List<GitRepositoryDTO> getAllRepositories() {
        // Retrieve all Git repositories from the local repository
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAll();

        // Convert the GitRepository entities to GitRepositoryDTOs and return the result directly
        return gitRepositories.stream()
                .map(this::convertToDto)
                .toList(); // Use Stream.toList() for simplicity
    }


    // Delete a Git repository by its ID
    @Override
    @Transactional
    public void deleteRepository(Long repoId) {
        // Retrieve the Git repository by its ID
        GitRepository repository = gitRepositoryRepository.findByRepoId(repoId)
                .orElseThrow(() -> new RuntimeException("Repository not found with repoId: " + repoId));

        // Get the repository name from the entity
        String repoName = repository.getName();

        // Create HTTP headers with the GitHub access token and content type
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(GITHUB_ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HTTP request entity with the headers
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            // Send an HTTP DELETE request to remove the repository on GitHub
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    API_BASE_URL + REPOS_ENDPOINT + "/" + repository.getName(), // Use the correct repository name
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class);

            // Check if the response status code indicates success (HTTP 204 - No Content)
            if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new RuntimeException("Error deleting repository with repoId " + repoId + ". Response: " + responseEntity.getBody());
            }

            // Delete the repository from the local repository
            gitRepositoryRepository.delete(repository);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Repository with repoId " + repoId + " not found.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting repository with repoId " + repoId, e);
        }
    }

    @Override
    public List<GitRepositoryDTO> getAllRepositoriesByProject(Long id) {
        return null;
    }

    // Get a list of all Git repositories associated with a project
//    @Override
//    @Transactional(readOnly = true)
//    public List<GitRepositoryDTO> getAllRepositoriesByProject(Long id) {
//        // Retrieve the project by its ID
//        Optional<Project> projectOptional = projectService.getProjectById(id);
//        if (projectOptional.isPresent()) {
//            Project project = projectOptional.get();
//
//            // Find the Git repositories associated with the project
//            List<GitRepository> repositories = gitRepositoryRepository.findRepositoriesByProject(project);
//
//            // Convert the GitRepository entities to GitRepositoryDTOs
//            List<GitRepositoryDTO> gitRepositoryDTOS = repositories.stream()
//                    .map(repository -> new GitRepositoryDTO(repository.getName(), repository.getDescription()))
//                    .collect(Collectors.toList());
//
//            return gitRepositoryDTOS;
//        } else {
//            // Handle the case where the project with the given ID doesn't exist
//            return Collections.emptyList(); // Return an empty list or handle it as needed
//        }
//    }



    // Get a list of Git repositories by role
    @Override
    @Transactional(readOnly = true)
    public List<GitRepositoryDTO> getAllReposByRole(EnumRole enumRole) {
        // Retrieve Git repositories by role from the local repository
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAllByRole(enumRole);

        // Convert the GitRepository entities to GitRepositoryDTOs and return the result directly
        return gitRepositories.stream()
                .map(repository -> new GitRepositoryDTO(repository.getRepoId(), repository.getName(), repository.getDescription()))
                .toList(); // Use Stream.toList() for simplicity
    }



    // Get a Git repository by its ID
    @Override
    @Transactional(readOnly = true)
    public GitRepository getRepositoryById(Long id) {
        try {
            // Retrieve the Git repository by its ID from the local repository
            return gitRepositoryRepository.findById(id).orElse(null);
        } catch (Exception e) {
            // Handle exceptions here if needed
            throw new FigmaNotFoundException("An error occurred while retrieving the Git repository by ID");
        }
    }

    // Check if a GitHub access token is valid
    @Override
    public boolean isAccessTokenValid(String accessToken) {
        // Create HTTP headers with the provided access token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // Create an HTTP request entity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Send an HTTP GET request to a GitHub API endpoint to validate the token
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                requestEntity,
                String.class);

        // Check if the response status code indicates success (HTTP 200 - OK)
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Optionally, you can parse the response body for more information
            String responseBody = responseEntity.getBody();
        }

        // Return true if the token is valid, otherwise return false
        return responseEntity.getStatusCode() == HttpStatus.OK;
    }

    // Helper method to convert a GitRepository entity to a GitRepositoryDTO
    private GitRepositoryDTO convertToDto(GitRepository gitRepository) {
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO();
        gitRepositoryDTO.setRepoId(gitRepository.getRepoId());
        gitRepositoryDTO.setName(gitRepository.getName());
        gitRepositoryDTO.setDescription(gitRepository.getDescription());
        return gitRepositoryDTO;
    }
}