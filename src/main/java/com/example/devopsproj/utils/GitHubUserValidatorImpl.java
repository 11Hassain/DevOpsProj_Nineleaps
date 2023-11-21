package com.example.devopsproj.utils;

import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link GitHubUserValidator} interface using the {@link GitHubUserValidation} utility class.
 */
@Component
public class GitHubUserValidatorImpl implements GitHubUserValidator {
    /**
     * Validates a GitHub user by delegating to the static method in {@link GitHubUserValidation}.
     *
     * @param username    GitHub username to be validated.
     * @param accessToken Access token for GitHub API authentication.
     * @return True if the GitHub user is valid, false otherwise.
     */
    @Override
    public boolean isGitHubUserValid(String username, String accessToken) {
        return GitHubUserValidation.isGitHubUserValid(username, accessToken);
    }
}