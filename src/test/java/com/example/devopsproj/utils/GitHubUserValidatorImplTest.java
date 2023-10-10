package com.example.devopsproj.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GitHubUserValidatorImplTest {

    @InjectMocks
    private GitHubUserValidatorImpl gitHubUserValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void testIsGitHubUserValid_ValidUser() {
//        // Arrange
//        String username = "validUsername";
//        String accessToken = "validAccessToken";
//
//        // Assume that GitHubUserValidation.isGitHubUserValid(username, accessToken) returns true
//        boolean expectedResult = true;
//
//        // Act
//        boolean result = gitHubUserValidator.isGitHubUserValid(username, accessToken);
//
//        // Assert
//        assertEquals(expectedResult, result);
//    }

    @Test
    void testIsGitHubUserValid_InvalidUser() {
        String username = "invalidUsername";
        String accessToken = "invalidAccessToken";
        boolean expectedResult = false;

        boolean result = gitHubUserValidator.isGitHubUserValid(username, accessToken);

        assertEquals(expectedResult, result);
    }
}
