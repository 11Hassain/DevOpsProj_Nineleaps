package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
import com.example.devopsproj.service.implementations.UserNamesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserNamesControllerTest {

    @InjectMocks
    private UserNamesController userNamesController;
    @Mock
    private UserNamesServiceImpl userNamesService;
    @Mock
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final String INVALID_TOKEN = "Invalid Token";

    // ----- SUCCESS (For VALID TOKEN) -----

    @Test
    void testSaveUsername_ValidToken_UserSavedSuccessfully() {
        UserNamesDTO userNamesDTO = new UserNamesDTO();
        String accessToken = "valid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(userNamesService.saveUsername(userNamesDTO)).thenReturn(userNamesDTO);

        ResponseEntity<Object> response = userNamesController.saveUsername(userNamesDTO, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userNamesDTO, response.getBody());
    }

    @Test
    void testSaveUsername_ValidToken_UserAlreadyExists() {
        UserNamesDTO userNamesDTO = new UserNamesDTO();
        String accessToken = "valid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(userNamesService.saveUsername(userNamesDTO)).thenThrow(DataIntegrityViolationException.class);

        ResponseEntity<Object> response = userNamesController.saveUsername(userNamesDTO, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    void testSaveUsername_ValidToken_UserNotFound() {
        UserNamesDTO userNamesDTO = new UserNamesDTO();
        String accessToken = "valid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(userNamesService.saveUsername(userNamesDTO)).thenReturn(null);

        ResponseEntity<Object> response = userNamesController.saveUsername(userNamesDTO, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Github user not found", response.getBody());
    }

    @Test
    void testGetUserNamesByRole_ValidTokenAndRole() {
        String role = "USER";
        String accessToken = "valid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

        List<String> expectedUsernames = Arrays.asList("user1", "user2");
        when(userNamesService.getGitHubUserNamesByRole(EnumRole.USER)).thenReturn(expectedUsernames);

        ResponseEntity<Object> response = userNamesController.getUserNamesByRole(role, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<String> responseUsernames = (List<String>) response.getBody();
        assertNotNull(responseUsernames);
        assertEquals(expectedUsernames.size(), responseUsernames.size());
        assertTrue(expectedUsernames.containsAll(responseUsernames));
    }


    // ----- FAILURE (For INVALID TOKEN) -----

    @Test
    void testSaveUsername_InvalidToken() {
        UserNamesDTO userNamesDTO = new UserNamesDTO();

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = userNamesController.saveUsername(userNamesDTO, "invalid-access-token");

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(INVALID_TOKEN, response.getBody());
    }

    @Test
    void testGetUserNamesByRole_InvalidToken() {
        String role = "USER";
        String accessToken = "invalid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(false);

        ResponseEntity<Object> response = userNamesController.getUserNamesByRole(role, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


}
