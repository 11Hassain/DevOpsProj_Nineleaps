package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----- SUCCESS -----

    @Test
    void testGetEmailFromToken_Success() {
        String emailToVerify = "example@gmail.com";
        UserDTO userDTO = new UserDTO();
        when(userService.loginVerification(emailToVerify)).thenReturn(userDTO);

        ResponseEntity<Object> responseEntity = authController.getEmailFromToken(emailToVerify);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    // ----- FAILURE -----

    @Test
    void testGetEmailFromToken_NotFound() {
        String emailToVerify = "nonexistent@gmail.com";
        when(userService.loginVerification(emailToVerify)).thenReturn(null);

        ResponseEntity<Object> responseEntity = authController.getEmailFromToken(emailToVerify);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(userService, times(1)).loginVerification(emailToVerify);
    }

}
