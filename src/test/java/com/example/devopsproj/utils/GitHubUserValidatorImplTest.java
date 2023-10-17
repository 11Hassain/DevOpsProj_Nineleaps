package com.example.devopsproj.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;


public class GitHubUserValidatorImplTest {

    @InjectMocks
    private GitHubUserValidatorImpl gitHubUserValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testIsGitHubUserValid_InvalidUser() {
        String username = "invalidUsername";
        String accessToken = "invalidAccessToken";
        boolean expectedResult = false;

        boolean result = gitHubUserValidator.isGitHubUserValid(username, accessToken);

        assertEquals(expectedResult, result);
    }
}
