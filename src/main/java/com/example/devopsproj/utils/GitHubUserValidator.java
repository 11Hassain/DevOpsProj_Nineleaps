package com.example.devopsproj.utils;
/**
 * Interface for validating GitHub users. Implementations should provide a method to check
 * the validity of a GitHub user based on the provided username and access token.
 */
public interface GitHubUserValidator {
    boolean isGitHubUserValid(String username, String accessToken);
}
