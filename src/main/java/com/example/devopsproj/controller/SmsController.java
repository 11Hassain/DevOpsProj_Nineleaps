package com.example.devopsproj.controller;


import com.example.devopsproj.dto.responsedto.SmsPojo;
import com.example.devopsproj.dto.responsedto.StoreOTP;
import com.example.devopsproj.dto.responsedto.TempOTP;

import com.example.devopsproj.service.implementations.SmsService;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





@RequestMapping("/api/v1/OTP")
@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    private final SimpMessagingTemplate webSocket;
    private static final String TOPIC_DESTINATION = "/lesson/sms";
    private static final String SECRET_KEY = System.getenv("SMS_SECRET_KEY");


    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    // Send an SMS.
    @PostMapping("/send")
    @ApiOperation("Send an SMS")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> smsSubmit(@RequestBody SmsPojo sms) {
        try {
            logger.info("Sending SMS"); // Replace with the appropriate log level (info, debug, error, etc.)
            smsService.send(sms);
        } catch (Exception e) {
            logger.error("Something went wrong", e);
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        webSocket.convertAndSend(TOPIC_DESTINATION, smsService.getTimeStamp() + ": SMS has been sent " + sms.getPhoneNumber());
        return new ResponseEntity<>("OTP sent", HttpStatus.OK);
    }

    // Verify the OTP for sign-up using phone number and OTP.
    @PostMapping("/verifyOTP")
    @ApiOperation("Verify the OTP for sign-up")
    @ResponseStatus(HttpStatus.OK)
    public String verifyOTPSignUp(@RequestBody TempOTP sms, HttpServletResponse response) {
        if (sms.getOtp() == StoreOTP.getOtp()) {
            return "Correct OTP";
        } else {
            return "Not a correct OTP";
        }
    }

}
