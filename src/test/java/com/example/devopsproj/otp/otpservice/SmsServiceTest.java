package com.example.devopsproj.otp.otpservice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SmsServiceTest {

    @InjectMocks
    private SmsService smsService;
    @Mock
    private IUserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----- SUCCESS -----

    @Test
    void testSetPhoneNumber() {
        SmsService smsService = new SmsService(userService);

        smsService.setPhoneNumber("1234567890");

        assertEquals("1234567890", smsService.getPhoneNumber());
    }


    // ----- FAILURE -----

    @Test
    void testGenerateToken_UserNotFound(){
        String email = "sample@example.com";
        String phoneNumber = "1234567890";

        when(userService.getUserByMail(email)).thenReturn(null);

        String token = smsService.generateToken(email, phoneNumber);

        assertNull(token);
    }
}
