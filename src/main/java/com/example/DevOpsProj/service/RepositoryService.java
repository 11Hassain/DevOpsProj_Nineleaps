package com.example.DevOpsProj.service;

import com.example.DevOpsProj.model.Repository;
//import com.example.DevOpsProj.model.RepositoryEntity;
import com.example.DevOpsProj.repository.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.StringTokenizer;

@Service
public class RepositoryService {

//    @Value("${github.accessToken}")
//    private String accessToken;
//
//    private static final String API_BASE_URL = "https://api.github.com";
//    private static final String USER_REPOS_ENDPOINT = "/user/repos";
//
//    private RestTemplate restTemplate;
//    private RepositoryRepository repositoryRepository;
//
//    public RepositoryService(RepositoryRepository repositoryRepository) {
//        this.restTemplate = new RestTemplate();
//        this.restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//        this.repositoryRepository = repositoryRepository;
//    }
//
//    public Repository createRepository(Repository repository) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Repository> requestEntity = new HttpEntity<>(repository, headers);
//
//        ResponseEntity<Repository> responseEntity = restTemplate.exchange(
//                API_BASE_URL + USER_REPOS_ENDPOINT,
//                HttpMethod.POST,
//                requestEntity,
//                Repository.class);
//
//        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
//            // Store the repository name in the database
//            RepositoryEntity repositoryEntity = new RepositoryEntity();
//            repositoryEntity.setName(repository.getName());
//            repositoryRepository.save(repositoryEntity);
//
//            return responseEntity.getBody();
//        } else {
//            throw new RuntimeException("Error creating repository " + repository.getName() + ". Response: " + responseEntity.getBody());
//        }
//    }


    @Autowired
    private RepositoryRepository repositoryRepository;

    @Value("${github.accessToken}")
    private String accessToken;

    private static final String API_BASE_URL = "https://api.github.com";
    private static final String USER_REPOS_ENDPOINT = "/user/repos";

    private RestTemplate restTemplate;

    public RepositoryService() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Transactional
    public Repository createRepository(Repository repository) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Repository> requestEntity = new HttpEntity<>(repository, headers);

        ResponseEntity<Repository> responseEntity = restTemplate.exchange(
                API_BASE_URL + USER_REPOS_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                Repository.class);

        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            Repository createdRepository = responseEntity.getBody();
            repositoryRepository.save(createdRepository);
            return createdRepository;
        } else {
            throw new RuntimeException("Error creating repository " + repository.getName() + ". Response: " + responseEntity.getBody());
        }
    }
    @Transactional(readOnly = true)
    public List<Repository> getAllRepositories() {
        return repositoryRepository.findAll();
    }

}
