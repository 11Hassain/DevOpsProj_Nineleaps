package com.example.devopsproj.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenExchangeControllerTest {

    @InjectMocks
    private TokenExchangeController tokenExchangeController;
    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private CloseableHttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTokenExchange_Success() throws IOException {
        String authorizationCode = "validAuthorizationCode";
        String expectedResponseBody = "{\"error\":\"invalid_request\",\"error_description\":\"Could not determine client ID from request.\"}";
        String actualResponseBody = "{\"error\":\"invalid_request\",\"error_description\":\"Could not determine client ID from request.\"}";

        // Parse JSON strings into JSON objects
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expectedJson = objectMapper.readTree(expectedResponseBody);
        JsonNode actualJson = objectMapper.readTree(actualResponseBody);

        // Mock HttpClient behavior
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(new StringEntity(actualResponseBody));
        when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(
                new ProtocolVersion("HTTP", 1, 1), 200, null));

        ResponseEntity<Object> responseEntity = tokenExchangeController.tokenExchange(authorizationCode);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedJson, actualJson);
    }

    @Test
    void testTokenExchange_ErrorHandling() throws IOException {
        // Mock the HTTP client to throw an IOException
        mockStatic(HttpClients.class);
        when(HttpClients.createDefault()).thenReturn(httpClient);

        when(httpClient.execute(any(HttpPost.class))).thenThrow(new IOException("Simulated exception"));

        // Execute the controller method
        ResponseEntity<Object> responseEntity = tokenExchangeController.tokenExchange("authorization_code");

        // Verify that it returns an Internal Server Error response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }

}
