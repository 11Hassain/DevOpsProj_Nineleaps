package com.example.devopsproj.otp.otpcontroller;

import com.example.devopsproj.controller.SmsController;
import com.example.devopsproj.dto.otpdto.SmsPojo;
import com.example.devopsproj.dto.otpdto.StoreOTP;
import com.example.devopsproj.dto.otpdto.TempOTP;
import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.service.implementations.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SmsControllerTest {

    @InjectMocks
    private SmsController smsController;
    @Mock
    private SmsService service;
    @Mock
    private IUserService userService;
    @Mock
    private SimpMessagingTemplate webSocket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final String TOPIC_DESTINATION = "/lesson/sms";

    // ----- SUCCESS -----

    @Test
    void testSmsSubmit_Success() {
        SmsPojo sms = new SmsPojo();
        doNothing().when(service).send(sms);

        ResponseEntity<String> response = smsController.smsSubmit(sms);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OTP sent", response.getBody());
    }

    @Test
    void testVerifyOTPSignUp_CorrectOTP() {
        int otp = 123456;
        StoreOTP.setOtp(otp);

        TempOTP tempOTP = new TempOTP();
        tempOTP.setOtp(otp);

        Boolean result = smsController.verifyOTPSignUp(tempOTP, new MockHttpServletResponse());

        assertTrue(result);
    }



    // ----- FAILURE -----

    @Test
    void testSmsSubmit_Exception() {
        SmsPojo sms = new SmsPojo();
        doThrow(new RuntimeException("Something went wrong")).when(service).send(sms);

        ResponseEntity<String> response = smsController.smsSubmit(sms);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong", response.getBody());
    }

    @Test
    void testVerifyOTPSignUp_IncorrectOTP() {
        int storedOtp = 123456;
        StoreOTP.setOtp(storedOtp);

        TempOTP tempOTP = new TempOTP();
        tempOTP.setOtp(789012);

        Boolean result = smsController.verifyOTPSignUp(tempOTP, new MockHttpServletResponse());

        assertFalse(result);
    }

}
