package com.example.devopsproj.Controller;

import com.example.devopsproj.controller.SmsController;
import com.example.devopsproj.otp.OTPDTO.SmsPojo;
import com.example.devopsproj.otp.OTPDTO.StoreOTP;
import com.example.devopsproj.otp.OTPDTO.TempOTP;
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

import static org.mockito.Mockito.*;

public class SmsControllerTest {

    @InjectMocks
    private SmsController smsController;

    @Mock
    private SmsService smsService;

    @Mock
    private IUserService userService;

    @Mock
    private SimpMessagingTemplate webSocket;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSmsSubmit_Success() {
        // Create a sample SmsPojo
        SmsPojo sms = new SmsPojo();
        sms.setPhoneNumber("1234567890");

        // Mock the behavior of smsService.send
        doNothing().when(smsService).send(sms);

        // Call the smsSubmit method in the controller
        ResponseEntity<String> responseEntity = smsController.smsSubmit(sms);

        // Verify that smsService.send was called with the correct SmsPojo
        verify(smsService).send(sms);

        // Verify the response status
        assert(responseEntity.getStatusCode()).equals(HttpStatus.OK);
        assert(responseEntity.getBody()).equals("otp sent");
    }

    @Test
    public void testSmsSubmit_Failure() {
        // Create a sample SmsPojo
        SmsPojo sms = new SmsPojo();
        sms.setPhoneNumber("1234567890");

        // Mock the behavior of smsService.send to throw an exception
        doThrow(new RuntimeException("Error sending SMS")).when(smsService).send(sms);

        // Call the smsSubmit method in the controller
        ResponseEntity<String> responseEntity = smsController.smsSubmit(sms);

        // Verify that smsService.send was called with the correct SmsPojo
        verify(smsService).send(sms);

        // Verify the response status
        assert(responseEntity.getStatusCode()).equals(HttpStatus.INTERNAL_SERVER_ERROR);
        assert(responseEntity.getBody()).equals("something went wrong");
    }

    @Test
    public void testVerifyOTPSignUp_CorrectOTP() {
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
    public void testVerifyOTPSignUp_IncorrectOTP() {
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
