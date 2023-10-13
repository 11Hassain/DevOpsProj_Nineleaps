package com.example.devopsproj.utils;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import org.mockito.Mockito;
import org.mockito.MockedStatic;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;



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



    @Test
    void testIsGitHubUserValid_InvalidUser() {
        // Replace these with invalid username and token for testing an invalid user
        String invalidUsername = "your_invalid_username";
        String invalidAccessToken = "your_invalid_access_token";

        assertFalse(GitHubUserValidation.isGitHubUserValid(invalidUsername, invalidAccessToken));
    }






}
