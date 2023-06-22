package com.example.DevOpsProj.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GitHubUserValidation {

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
            e.printStackTrace();
            return false;
        }
    }
}
