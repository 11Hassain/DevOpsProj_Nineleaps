package com.example.devopsproj.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GitHubUserValidation {

    private static final Logger logger = LoggerFactory.getLogger(GitHubUserValidation.class);

    public static boolean isGitHubUserValid(String username, String accessToken) {
        String apiUrl = "https://api.github.com/users/" + username;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            return statusCode == 200;
        } catch (Exception e) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();
            logger.error("An error occurred: ", e);
            return false;
        }
    }
}
