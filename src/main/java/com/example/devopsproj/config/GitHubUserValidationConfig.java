package com.example.devopsproj.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.devopsproj.utils.GitHubUserValidation;

@Configuration
public class GitHubUserValidationConfig {

    @Bean
    public GitHubUserValidation gitHubUserValidation() {
        return new GitHubUserValidation();
    }
}
