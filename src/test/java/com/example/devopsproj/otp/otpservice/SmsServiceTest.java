package com.example.devopsproj.otp.otpservice;

import com.example.devopsproj.service.implementations.SmsService;
import com.example.devopsproj.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

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

}
