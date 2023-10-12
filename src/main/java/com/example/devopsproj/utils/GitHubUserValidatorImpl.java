package com.example.devopsproj.utils;

import org.springframework.stereotype.Component;

/**
 * The `GitHubUserValidatorImpl` class is an implementation of the `GitHubUserValidator` interface.
 * It provides a way to validate a GitHub user using an access token by delegating to the `GitHubUserValidation` utility.
 *
 * @version 2.0
 */

@Component
public class GitHubUserValidatorImpl implements GitHubUserValidator {
    @Override
    public boolean isGitHubUserValid(String username, String accessToken) {
        return GitHubUserValidation.isGitHubUserValid(username, accessToken);
    }
}
