package com.example.devopsproj.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class GitHubUserValidationTest {

    private static final String VALID_USERNAME = "validusername";
    private static final String VALID_ACCESS_TOKEN = "validaccesstoken";

    private static final String INVALID_USERNAME = "invalidusername";
    private static final String INVALID_ACCESS_TOKEN = "invalidaccesstoken";


    @Test
     void testInvalidGitHubUser() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid(INVALID_USERNAME, VALID_ACCESS_TOKEN));
    }

    @Test
     void testInvalidAccessToken() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid(VALID_USERNAME, INVALID_ACCESS_TOKEN));
    }

    @Test
    void testEmptyUsername() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid("", VALID_ACCESS_TOKEN));
    }

    @Test
     void testNullUsername() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid(null, VALID_ACCESS_TOKEN));
    }

    @Test
    void testEmptyAccessToken() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid(VALID_USERNAME, ""));
    }

    @Test
     void testNullAccessToken() throws InterruptedException {
        assertFalse(GitHubUserValidation.isGitHubUserValid(VALID_USERNAME, null));
    }



    @Test
    void testIsGitHubUserValid_InvalidUser() {
        // Replace these with invalid username and token for testing an invalid user
        String invalidUsername = "your_invalid_username";
        String invalidAccessToken = "your_invalid_access_token";

        assertFalse(GitHubUserValidation.isGitHubUserValid(invalidUsername, invalidAccessToken));
    }



    @Test
    void testPrivateConstructor() throws Exception {
        // Use reflection to access the private constructor
        Constructor<GitHubUserValidation> constructor = GitHubUserValidation.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Attempt to create an instance of GitHubUserValidation
        try {
            constructor.newInstance();
        } catch (InvocationTargetException e) {
            // Check if the actual exception is an UnsupportedOperationException
            if (e.getCause() != null) {
                Assertions.assertEquals(UnsupportedOperationException.class, e.getCause().getClass());
            } else {
                Assertions.fail("Expected an UnsupportedOperationException but got no cause exception.");
            }
        }
    }



//    @Test
//    public void testValidGitHubUser() {
//        String username = "exampleuser";
//        String accessToken = "yourAccessToken";
//
//        boolean isValid = GitHubUserValidation.isGitHubUserValid(username, accessToken);
//
//        assertTrue(isValid);
//    }

    @Test
     void testInvalidGitHubUsers() {
        String username = "nonexistentuser";
        String accessToken = "yourAccessToken";

        boolean isValid = GitHubUserValidation.isGitHubUserValid(username, accessToken);

        assertFalse(isValid);
    }



    @Test
     void testInterruptedExceptionHandling() {
        // Create a scenario where the thread is interrupted
        Thread.currentThread().interrupt();
        String username = "exampleuser";
        String accessToken = "yourAccessToken";

        boolean isValid = GitHubUserValidation.isGitHubUserValid(username, accessToken);

        // Since the thread is already interrupted, the method should return false.
        assertFalse(isValid);
    }







}
