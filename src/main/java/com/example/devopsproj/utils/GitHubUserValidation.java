package com.example.devopsproj.utils;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Component // Mark it as a Spring component
public class GitHubUserValidation {
    public static boolean isGitHubUserValid(String username, String accessToken) throws InterruptedException {
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
        } catch (InterruptedException e) {
            // Re-throw InterruptedException
            Thread.currentThread().interrupt();
            throw e;
        } catch (Exception e) {
            return false;
        }
    }
}
