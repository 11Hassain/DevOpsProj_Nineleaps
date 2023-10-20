package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.exceptions.RepositoryCreationException;
import com.example.devopsproj.exceptions.RepositoryDeletionException;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import java.util.Collections;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * The `GitRepositoryServiceImpl` class provides services for managing Git repositories, including
 * creating, deleting, and retrieving repositories. It also includes methods for listing repositories
 * based on a user's role and a specific project.
 *
 * @version 2.0
 */

@Service
@RequiredArgsConstructor
public class GitRepositoryServiceImpl implements GitRepositoryService {

    private final GitRepositoryRepository gitRepositoryRepository;
    private final ProjectServiceImpl projectServiceImpl;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(GitRepositoryServiceImpl.class);

    private static final String API_BASE_URL = "https://api.github.com";
    private static final String USER_REPOS_ENDPOINT = "/user/repos";
    private static final String REPOS_ENDPOINT = "/repos/sahilanna";
    private static final String GITHUB_ACCESS_TOKEN = System.getenv("GITHUB_ACCESS_TOKEN");

    private final RestTemplate restTemplate;


    @Override
    @Transactional
    public GitRepository createRepository(GitRepository gitRepository) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(GITHUB_ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GitRepository> requestEntity = new HttpEntity<>(gitRepository, headers);

        ResponseEntity<GitRepository> responseEntity = restTemplate.exchange(
                API_BASE_URL + USER_REPOS_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                GitRepository.class);

        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            GitRepository createdGitRepository = responseEntity.getBody();
            assert createdGitRepository != null;
            gitRepositoryRepository.save(createdGitRepository);
            return createdGitRepository;
        } else {
            throw new RepositoryCreationException("Error creating repository " + gitRepository.getName() + ". Response: " + responseEntity.getBody());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GitRepositoryDTO> getAllRepositories() {
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAll();

        return gitRepositories.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteRepository(Long repoId) {
        GitRepository repository = gitRepositoryRepository.findByRepoId(repoId)
                .orElseThrow(() -> new NotFoundException("Repository with repoId " + repoId + " not found."));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(GITHUB_ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    API_BASE_URL + REPOS_ENDPOINT + "/" + repository.getName(),
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class);

            if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new RepositoryDeletionException("Error deleting repository with repoId " + repoId + ". Response: " + responseEntity.getBody());
            }

            gitRepositoryRepository.delete(repository);
        } catch (Exception e) {
            throw new RepositoryDeletionException("Error deleting repository with repoId " + repoId);
        }
    }

    @Override
    public List<GitRepositoryDTO> getAllRepositoriesByProject(Long id) {
        Project project = projectServiceImpl.getProjectById(id).orElse(null);
        if(project != null){
            List<GitRepository> repositories = gitRepositoryRepository.findRepositoriesByProject(project);
            return repositories.stream()
                    .map(repository -> new GitRepositoryDTO(repository.getName(), repository.getDescription()))
                    .toList();
        }
        else return Collections.emptyList();
    }

    @Override
    public List<GitRepositoryDTO> getAllReposByRole(EnumRole enumRole) {
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAllByRole(enumRole);
        return gitRepositories.stream()
                .map(repository -> new GitRepositoryDTO(repository.getRepoId(), repository.getName(), repository.getDescription()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GitRepository getRepositoryById(Long id) {
        return gitRepositoryRepository.findById(id).orElse(null);
    }


    // ---- Other methods ------


    public GitRepositoryDTO convertToDto(GitRepository gitRepository) {
        // Perform the mapping logic from GitRepository to GitRepositoryDTO
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO();
        gitRepositoryDTO.setRepoId(gitRepository.getRepoId());
        gitRepositoryDTO.setName(gitRepository.getName());
        gitRepositoryDTO.setDescription(gitRepository.getDescription());

        return gitRepositoryDTO;
    }
}
