package com.example.devopsproj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The TokenExchangeController class provides an endpoint for exchanging an authorization code for an access token.
 * This controller handles the token exchange process for external OAuth 2.0 providers, such as Google.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/api/v1")
@Validated
public class TokenExchangeController {

    private static final Logger logger = LoggerFactory.getLogger(TokenExchangeController.class);

    /**
     * Exchange authorization code for access token.
     *
     * @param authorizationCode The authorization code obtained during authentication.
     * @return ResponseEntity containing the access token or an error message.
     */
    @GetMapping("/token-exchange")
    @Operation(
            description = "Exchange authorization code for access token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token obtained successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> tokenExchange(@RequestParam("code") String authorizationCode) {
        logger.info("Received a request to exchange authorization code for access token");

        String clientId = System.getenv("GOOGLE_CLIENT_ID");
        String clientSecret = System.getenv("GOOGLE_SECRET_KEY");
        String redirectUri = "https://www.google.com/";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
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

            // Set the request body with the parameters
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            // Execute the request and get the response
            HttpResponse response = httpClient.execute(httpPost);

            // Get the response entity and convert it to a string
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);

            return ResponseEntity.ok(responseBody);

        } catch (IOException e) {
            logger.error("An error occurred: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

}
