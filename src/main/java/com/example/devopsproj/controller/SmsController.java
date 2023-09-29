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

import static java.lang.System.out;

@RequestMapping("/api/v1/OTP")
@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;
    private final IUserService userservice;
    private final SimpMessagingTemplate webSocket;
    private static final String TOPIC_DESTINATION = "/lesson/sms";
    private static final String SECRET_KEY = System.getenv("SMS_SECRET_KEY");


    @PostMapping("/send")
    public ResponseEntity<String> smsSubmit(@RequestBody SmsPojo sms){
        try{
            out.println("try");
            smsService.send(sms);
        }catch (Exception e){
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        webSocket.convertAndSend(TOPIC_DESTINATION,smsService.getTimeStamp()+":SMS has been sent "+sms.getPhoneNumber());
        return new ResponseEntity<>("otp sent",HttpStatus.OK);
    }

    // Endpoint to verify the OTP for sign-up using phone number and OTP
    @PostMapping("/verifyOTP/signUp")
    public String verifyOTPSignUp(@RequestBody TempOTP sms,HttpServletResponse response){
        if(sms.getOtp()== StoreOTP.getOtp()) {
            return "correct otp";
        } else {
            return  "not a correct otp";
        }
    }
}
