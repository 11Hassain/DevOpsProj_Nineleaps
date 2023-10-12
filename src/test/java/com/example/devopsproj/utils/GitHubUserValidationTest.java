package com.example.devopsproj.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class GitHubUserValidationTest {

    private static final String VALID_USERNAME = "validusername";
    private static final String VALID_ACCESS_TOKEN = "validaccesstoken";

    private static final String INVALID_USERNAME = "invalidusername";
    private static final String INVALID_ACCESS_TOKEN = "invalidaccesstoken";



    @Test
    public void testInvalidGitHubUser() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid(INVALID_USERNAME, VALID_ACCESS_TOKEN));
    }

    @Test
    public void testInvalidAccessToken() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid(VALID_USERNAME, INVALID_ACCESS_TOKEN));
    }

    @Test
    public void testEmptyUsername() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid("", VALID_ACCESS_TOKEN));
    }

    @Test
    public void testNullUsername() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid(null, VALID_ACCESS_TOKEN));
    }

    @Test
    public void testEmptyAccessToken() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid(VALID_USERNAME, ""));
    }

    @Test
    public void testNullAccessToken() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid(VALID_USERNAME, null));
    }
}
