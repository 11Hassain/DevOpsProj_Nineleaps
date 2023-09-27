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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GitRepositoryServiceImpl implements GitRepositoryService {

    @Autowired
    private GitRepositoryRepository gitRepositoryRepository;
    @Autowired
    private ProjectServiceImpl projectServiceImpl;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String API_BASE_URL = "https://api.github.com";
    private static final String USER_REPOS_ENDPOINT = "/user/repos";
    private static final String REPOS_ENDPOINT = "/repos/sahilanna";


    private static final String GITHUB_ACCESS_TOKEN = System.getenv("GITHUB_ACCESS_TOKEN");

    private final RestTemplate restTemplate;

    public GitRepositoryServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

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
                .orElseThrow(() -> new RuntimeException("Repository not found with repoId: " + repoId));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(GITHUB_ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    API_BASE_URL + REPOS_ENDPOINT + "/" + repository.getName(), // Use the correct repository name
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class);

            if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new RepositoryDeletionException("Error deleting repository with repoId " + repoId + ". Response: " + responseEntity.getBody());
            }

            gitRepositoryRepository.delete(repository);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Repository with repoId " + repoId + " not found.");
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


    private GitRepositoryDTO convertToDto(GitRepository gitRepository) {
        // Perform the mapping logic from GitRepository to GitRepositoryDTO
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO();
        gitRepositoryDTO.setRepoId(gitRepository.getRepoId());
        gitRepositoryDTO.setName(gitRepository.getName());
        gitRepositoryDTO.setDescription(gitRepository.getDescription());

        return gitRepositoryDTO;
    }
}
