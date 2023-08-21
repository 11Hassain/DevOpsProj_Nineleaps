package com.example.DevOpsProj.service;
import com.example.DevOpsProj.config.JiraConfig;
import com.example.DevOpsProj.dto.responseDto.JiraProjectDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class JiraService {
    private final JiraConfig jiraConfig;
    private final RestTemplate restTemplate;

    public JiraService(JiraConfig jiraConfig, RestTemplate restTemplate) {
        this.jiraConfig = jiraConfig;
        this.restTemplate = restTemplate;
    }

    public void createProject(JiraProjectDTO projectDto) {
        String apiUrl = jiraConfig.getApiUrl();
        String username = jiraConfig.getUsername();
        String password = jiraConfig.getPassword();

        String url = apiUrl + "/project";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.setContentType(MediaType.APPLICATION_JSON); // Set the content type to JSON

        HttpEntity<JiraProjectDTO> requestEntity = new HttpEntity<>(projectDto, headers);

        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }


}
