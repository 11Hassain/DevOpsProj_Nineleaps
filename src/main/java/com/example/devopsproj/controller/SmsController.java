package com.example.devopsproj.controller;


import com.example.devopsproj.otp.OTPDTO.SmsPojo;
import com.example.devopsproj.otp.OTPDTO.StoreOTP;
import com.example.devopsproj.otp.OTPDTO.TempOTP;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.service.implementations.SmsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RequestMapping("/api/v1/OTP")
@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService service;
    private final IUserService userservice;
    private final SimpMessagingTemplate webSocket;
    private static final String TOPIC_DESTINATION = "/lesson/sms";
    private static final String SECRET_KEY = System.getenv("SMS_SECRET_KEY");


    // Endpoint to send an SMS.
    @PostMapping("/send")
    public ResponseEntity<String> smsSubmit(@RequestBody SmsPojo sms) {
        try {
            // Call the service to send the SMS.
            service.send(sms);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // Notify via WebSocket that the SMS has been sent.
        webSocket.convertAndSend(TOPIC_DESTINATION, getTimeStamp() + ": SMS has been sent " + sms.getPhoneNumber());
        return new ResponseEntity<>("OTP sent", HttpStatus.OK);
    }


    // Endpoint to verify OTP during signup.
    @PostMapping("/verify")
    public Boolean verifyOTPsignup(@RequestBody TempOTP sms, HttpServletResponse response) throws Exception {

        if (sms.getOtp() == StoreOTP.getOtp()) {
            return true;
        } else {
            return false;
        }
    }

    // Helper method to get the current timestamp.
    private String getTimeStamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }
}
