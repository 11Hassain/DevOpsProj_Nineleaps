package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.SmsPojo;
import com.example.devopsproj.dto.responsedto.StoreOTP;
import com.example.devopsproj.dto.responsedto.TempOTP;
import com.example.devopsproj.service.implementations.SmsService;
import com.example.devopsproj.service.interfaces.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

 class SmsControllerTest {

    @InjectMocks
    private SmsController smsController;

    @Mock
    private SmsService smsService;

    @Mock
    private IUserService userService;

    @Mock
    private SimpMessagingTemplate webSocket;

    private ObjectMapper objectMapper;

    private static final String TOPIC_DESTINATION = "/lesson/sms";


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
    }
    @Test
    void testSmsSubmitSuccess() {
        // Arrange
        SmsPojo smsPojo = new SmsPojo();
        when(smsService.getTimeStamp()).thenReturn("2023-10-05 12:34:56");
        doNothing().when(smsService).send(smsPojo);

        // Act
        ResponseEntity<String> response = smsController.smsSubmit(smsPojo);

        // Assert
        verify(webSocket, times(1)).convertAndSend(eq(TOPIC_DESTINATION), anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OTP sent", response.getBody());
    }

    @Test
    void testSmsSubmitFailure() {
        // Arrange
        SmsPojo smsPojo = new SmsPojo();
        doThrow(new RuntimeException("Sending failed")).when(smsService).send(smsPojo);

        // Act
        ResponseEntity<String> response = smsController.smsSubmit(smsPojo);

        // Assert
        verify(webSocket, never()).convertAndSend(eq(TOPIC_DESTINATION), anyString());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong", response.getBody());
    }
    @Test
     void testVerifyOTPSignUp_CorrectOTP() {
        // Create a sample TempOTP
        TempOTP tempOTP = new TempOTP();
        tempOTP.setOtp(1234);

        // Set the OTP in StoreOTP
        StoreOTP.setOtp(1234);

        // Call the verifyOTPSignUp method in the controller
        String result = smsController.verifyOTPSignUp(tempOTP, null);

        // Verify that the result is "correct otp"
        assert(result).equals("correct otp");
    }

    @Test
     void testVerifyOTPSignUp_IncorrectOTP() {
        // Create a sample TempOTP
        TempOTP tempOTP = new TempOTP();
        tempOTP.setOtp(1234);

        // Set a different OTP in StoreOTP
        StoreOTP.setOtp(5678);

        // Call the verifyOTPSignUp method in the controller
        String result = smsController.verifyOTPSignUp(tempOTP, null);

        // Verify that the result is "not a correct otp"
        assert(result).equals("not a correct otp");
    }
}
