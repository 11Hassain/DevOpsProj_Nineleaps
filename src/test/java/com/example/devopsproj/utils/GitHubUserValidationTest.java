package com.example.devopsproj.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GitHubUserValidationTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUp() {
        WireMockConfiguration config = WireMockConfiguration.options().port(8089);
        wireMockServer = new WireMockServer(config);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testIsGitHubUserValid_InvalidUser() {
        String username = "invalidUsername";
        String accessToken = "invalidAccessToken";
        wireMockServer.stubFor(
                WireMock.get(urlEqualTo("/users/" + username))
                        .withHeader("Authorization", WireMock.equalTo("Bearer " + accessToken))
                        .willReturn(aResponse()
                                .withStatus(404)
                        )
        );

        boolean result = GitHubUserValidation.isGitHubUserValid(username, accessToken);

        assertFalse(result);
    }

    @Test
    void testIsGitHubUserValid_ExceptionHandling() {
        // Configure WireMock to stub a GitHub API endpoint that throws an exception
        wireMockServer.stubFor(get(urlEqualTo("/users/username"))
                .willReturn(aResponse()
                        .withStatus(500) // Simulate a 500 Internal Server Error
                ));

        // Invoke the method with invalid GitHub credentials, which will trigger the catch block
        boolean result = GitHubUserValidation.isGitHubUserValid("username", "invalid_token");

        assertFalse(result);
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
}
