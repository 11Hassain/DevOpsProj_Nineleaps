package com.example.devopsproj.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
/**
 * Utility class for validating GitHub users by making API requests using the provided username
 * and access token.
 */

public class GitHubUserValidation {
    private static final Logger logger = LoggerFactory.getLogger(GitHubUserValidation.class);

    private GitHubUserValidation() {
        // This constructor is intentionally left empty because the class
        // only contains static methods for mapping and doesn't need to be instantiated.
        throw new UnsupportedOperationException("This class should not be instantiated.");
    }

    public static boolean isGitHubUserValid(String username, String accessToken) {
        String apiUrl = "https://api.github.com/users/" + username;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            // Re-interrupt the thread before returning
            Thread.currentThread().interrupt();
            logger.error("An error occurred: ", e);
            return false; // Return a default value or handle the error gracefully.
        } catch (Exception e) {
            // Log the error message and return false or rethrow the exception as needed.
            logger.error("An error occurred: ", e);
            return false; // Return a default value or handle the error gracefully.
        }

        int statusCode = response.statusCode();
        return statusCode == 200;
    }
}