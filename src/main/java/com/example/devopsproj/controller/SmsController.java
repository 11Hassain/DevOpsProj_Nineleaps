package com.example.devopsproj.controller;


import com.example.devopsproj.dto.responsedto.SmsPojo;
import com.example.devopsproj.dto.responsedto.StoreOTP;
import com.example.devopsproj.dto.responsedto.TempOTP;

import com.example.devopsproj.service.implementations.SmsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Controller for handling SMS operations, including sending SMS messages and verifying OTPs.
 */
@RequestMapping("/api/v1/OTP")
@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    private final SimpMessagingTemplate webSocket;
    private static final String TOPIC_DESTINATION = "/lesson/sms";
    private static final String SECRET_KEY = System.getenv("SMS_SECRET_KEY");


    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);


    /**
     * Send an SMS.
     *
     * @param sms The SMS details to be sent, including the phone number and message.
     * @return ResponseEntity indicating the result of the SMS sending operation.
     */
    @PostMapping("/send")
    public ResponseEntity<String> smsSubmit(@RequestBody SmsPojo sms) {
        try {
            logger.info("try"); // Replace with the appropriate log level (info, debug, error, etc.)
            smsService.send(sms);
        } catch (Exception e) {
            logger.error("Something went wrong", e);
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        webSocket.convertAndSend(TOPIC_DESTINATION, smsService.getTimeStamp() + ": SMS has been sent " + sms.getPhoneNumber());
        return new ResponseEntity<>("OTP sent", HttpStatus.OK);
    }

    /**
     * Verify the OTP for sign-up using the phone number and OTP.
     *
     * @param sms The OTP details, including the OTP value to be verified.
     * @param response The HTTP response.
     * @return A string indicating whether the provided OTP is correct or not.
     */
    @PostMapping("/verifyOTP")
    public String verifyOTPSignUp(@RequestBody TempOTP sms,HttpServletResponse response){
        if(sms.getOtp()== StoreOTP.getOtp()) {
            return "correct otp";
        } else {
            return  "not a correct otp";
        }
    }

}
