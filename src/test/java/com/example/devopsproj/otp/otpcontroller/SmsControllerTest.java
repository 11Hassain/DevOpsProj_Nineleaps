package com.example.devopsproj.otp.otpcontroller;

import com.example.devopsproj.model.User;
import com.example.devopsproj.otp.otpdto.JwtResponse;
import com.example.devopsproj.otp.otpdto.SmsPojo;
import com.example.devopsproj.otp.otpdto.StoreOTP;
import com.example.devopsproj.otp.otpdto.TempOTP;
import com.example.devopsproj.otp.otpservice.IUserService;
import com.example.devopsproj.otp.otpservice.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
    void testVerifyOTP_CorrectOTP_UserFound() {
        TempOTP tempOTP = new TempOTP();
        tempOTP.setOtp(123456);

        StoreOTP.setOtp(tempOTP.getOtp());

        when(service.getPhoneNumber()).thenReturn("1234567890");

        User user = new User();
        user.setEmail("test@example.com");
        when(userService.getUserViaPhoneNumber(anyString())).thenReturn(user);

        ResponseEntity<Object> response = smsController.verifyOTP(tempOTP);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check if the response body is an instance of JwtResponse
        if (response.getBody() instanceof JwtResponse jwtResponse) {
            assertNull(jwtResponse.getRefreshToken());
            assertNull(jwtResponse.getErrorMessage());
        } else {
            // Handle the case when the response is a String message
            String responseBody = (String) response.getBody();
            assertEquals("User not found", responseBody);
        }
    }

    @Test
    void testSmsSub_SuccessfulSubmission() {
        SmsPojo resendSms = new SmsPojo();
        resendSms.setPhoneNumber("1234567890");

        doNothing().when(service).send(resendSms);

        ResponseEntity<String> response = smsController.smsSub(resendSms);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OTP sent", response.getBody());

        verify(webSocket).convertAndSend(TOPIC_DESTINATION, getTimeStamp() + ":SMS has been sent " + resendSms.getPhoneNumber());
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
    void testVerifyOTP_IncorrectOTP() {
        TempOTP tempOTP = new TempOTP();
        tempOTP.setOtp(123456);

        StoreOTP.setOtp(7890);

        when(service.getPhoneNumber()).thenReturn("1234567890");

        ResponseEntity<Object> response = smsController.verifyOTP(tempOTP);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Incorrect OTP", response.getBody());
    }

    @Test
    void testVerifyOTP_UserNotFound() {
        TempOTP tempOTP = new TempOTP();
        tempOTP.setOtp(123456);

        StoreOTP.setOtp(tempOTP.getOtp());

        when(service.getPhoneNumber()).thenReturn("1234567890");

        when(userService.getUserViaPhoneNumber(anyString())).thenReturn(null);

        ResponseEntity<Object> response = smsController.verifyOTP(tempOTP);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testSmsSub_FailedSubmission() {
        SmsPojo resendSms = new SmsPojo();
        resendSms.setPhoneNumber("1234567890");

        doThrow(new RuntimeException("Failed to send SMS")).when(service).send(resendSms);

        ResponseEntity<String> response = smsController.smsSub(resendSms);

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


    private String getTimeStamp(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

}
