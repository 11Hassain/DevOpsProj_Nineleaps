package com.example.devopsproj.Controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.controller.UserNamesController;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.exceptions.DuplicateUsernameException;
import com.example.devopsproj.service.interfaces.UserNamesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mysql.cj.exceptions.MysqlErrorNumbers.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserNamesControllerTest {

    @InjectMocks
    private UserNamesController userNamesController;

    @Autowired
    private MockMvc mockMvc;


    @Mock
    private UserNamesService userNamesService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSaveUsername_Success() throws Exception {
        // Prepare test data
        UserNamesDTO userNamesDTO = new UserNamesDTO();
        userNamesDTO.setUsername("testUser");

        // Mock the behavior of userNamesService.saveUsername
        when(userNamesService.saveUsername(userNamesDTO)).thenReturn(userNamesDTO);

        // Call the controller method
        ResponseEntity<Object> response = userNamesController.saveUsername(userNamesDTO);

        // Verify the HTTP status code
        assert(response.getStatusCode() == HttpStatus.CREATED);

        // Verify the response body
        String responseBody = objectMapper.writeValueAsString(response.getBody());
        String expectedResponseBody = objectMapper.writeValueAsString(userNamesDTO);
        assert(responseBody.equals(expectedResponseBody));

        // Verify that userNamesService.saveUsername was called once with the correct parameter
        verify(userNamesService, times(1)).saveUsername(userNamesDTO);
    }

    @Test
    public void testGetUserNamesByRole_ValidRole() {
        // Arrange
        String role = "ADMIN";
        EnumRole enumRole = EnumRole.ADMIN;
        List<String> expectedUsernames = Arrays.asList("user1", "user2");

        when(userNamesService.getGitHubUserNamesByRole(enumRole)).thenReturn(expectedUsernames);

        // Act
        ResponseEntity<Object> response = userNamesController.getUserNamesByRole(role);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsernames, response.getBody());

        // Verify that the service method was called with the correct argument
        verify(userNamesService, times(1)).getGitHubUserNamesByRole(enumRole);
    }



}
