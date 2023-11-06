package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.service.interfaces.UserNamesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.*;

class UserNamesControllerTest {

    @InjectMocks
    private UserNamesController userNamesController;
    @Mock
    private UserNamesService userNamesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class SaveUsernameTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testSaveUsername_ValidToken_UserSavedSuccessfully() {
            UserNamesDTO userNamesDTO = new UserNamesDTO();

            when(userNamesService.saveUsername(userNamesDTO)).thenReturn(userNamesDTO);

            ResponseEntity<Object> response = userNamesController.saveUsername(userNamesDTO);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(userNamesDTO, response.getBody());
        }

        @Test
        @DisplayName("Testing case where Git username already exists")
        void testSaveUsername_ValidToken_UserAlreadyExists() {
            UserNamesDTO userNamesDTO = new UserNamesDTO();

            when(userNamesService.saveUsername(userNamesDTO)).thenThrow(DataIntegrityViolationException.class);

            ResponseEntity<Object> response = userNamesController.saveUsername(userNamesDTO);

            assertNotNull(response);
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Username already exists", response.getBody());
        }

        @Test
        @DisplayName("Testing user not found case")
        void testSaveUsername_ValidToken_UserNotFound() {
            UserNamesDTO userNamesDTO = new UserNamesDTO();

            when(userNamesService.saveUsername(userNamesDTO)).thenReturn(null);

            ResponseEntity<Object> response = userNamesController.saveUsername(userNamesDTO);

            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Github user not found", response.getBody());
        }
    }

    @Test
    @DisplayName("Testing success case with valid token")
    void testGetUserNamesByRole_ValidTokenAndRole() {
        String role = "USER";

        List<String> expectedUsernames = Arrays.asList("user1", "user2");
        when(userNamesService.getGitHubUserNamesByRole(EnumRole.USER)).thenReturn(expectedUsernames);

        ResponseEntity<Object> response = userNamesController.getUserNamesByRole(role);

        assertAll("All Assertions",
                () -> assertNotNull(response, "Response should not be null"),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                () -> {
                    List<String> responseUsernames = (List<String>) response.getBody();
                    assertNotNull(responseUsernames, "Response usernames should not be null");
                    assertEquals(expectedUsernames.size(), responseUsernames.size(), "Response should have the same number of usernames");
                    assertTrue(expectedUsernames.containsAll(responseUsernames), "All expected usernames should be present in the response");
                }
        );
    }
}
