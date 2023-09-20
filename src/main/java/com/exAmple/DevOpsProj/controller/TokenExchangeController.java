package com.exAmple.DevOpsProj.controller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TokenExchangeController {

    @GetMapping("/token-exchange")
    public ResponseEntity<Object> tokenExchange(@RequestParam("code") String authorizationCode) {

        String clientId = System.getenv("GOOGLE_CLIENT_ID");
        String clientSecret = System.getenv("GOOGLE_SECRET_KEY");
        String redirectUri = "https://www.google.com/";

        // Create HttpClient
        HttpClient httpClient = HttpClients.createDefault();

        // Construct token endpoint URL
        String tokenEndpointUrl = "https://oauth2.googleapis.com/token";

        // Create a POST request to the token endpoint
        HttpPost httpPost = new HttpPost(tokenEndpointUrl);

        // Set request parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        params.add(new BasicNameValuePair("redirect_uri", redirectUri));
        params.add(new BasicNameValuePair("code", authorizationCode));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));

        try {
            // Set the request body with the parameters
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            // Execute the request and get the response
            HttpResponse response = httpClient.execute(httpPost);

            // Get the response entity and convert it to a string
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);

            return ResponseEntity.ok(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }
}
