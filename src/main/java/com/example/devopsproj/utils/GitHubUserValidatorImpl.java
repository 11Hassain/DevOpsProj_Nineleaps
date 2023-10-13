package com.example.devopsproj.utils;

import org.springframework.stereotype.Component;


@Component
public class GitHubUserValidatorImpl implements GitHubUserValidator {
    @Override
    public boolean isGitHubUserValid(String username, String accessToken) {
        return GitHubUserValidation.isGitHubUserValid(username, accessToken);
    }
}