package com.example.devopsproj.controller;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.service.implementations.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


 class AuthControllerTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetEmailFromToken_Success() {
        String emailToVerify = "example@gmail.com";
        UserDTO userDTO = new UserDTO();
        when(userService.loginVerification(emailToVerify)).thenReturn(userDTO);

        ResponseEntity<Object> responseEntity = authController.getEmailFromToken(emailToVerify);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }



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


