package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.User;
import com.example.devopsproj.otp.OTPDTO.JwtResponse;
import com.example.devopsproj.otp.OTPDTO.SmsPojo;
import com.example.devopsproj.otp.OTPDTO.StoreOTP;
import com.example.devopsproj.otp.OTPDTO.TempOTP;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.service.interfaces.SmsService;
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

    // Endpoint to resend an SMS.
    @PostMapping("/resend")
    public ResponseEntity<String> smsSub(@RequestBody SmsPojo resendsms) {
        try {
            // Call the service to resend the SMS.
            service.send(resendsms);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // Notify via WebSocket that the SMS has been sent again.
        webSocket.convertAndSend(TOPIC_DESTINATION, getTimeStamp() + ": SMS has been sent " + resendsms.getPhoneNumber());
        return new ResponseEntity<>("OTP sent", HttpStatus.OK);
    }

    // Endpoint to verify OTP and generate an access token.
    @PostMapping("/verify/token")
    public ResponseEntity<Object> verifyOTP(@RequestBody TempOTP tempOTP, HttpServletResponse response) throws Exception {

        if (tempOTP.getOtp() == StoreOTP.getOtp()) {
            SmsPojo sms = new SmsPojo(); // Instantiate the SmsPojo class
            String phoneNumber = sms.getPhoneNumber(); // Get the phone number from SmsPojo
            User user = userservice.getUserViaPhoneNumber(phoneNumber);

            if (user != null) {
                String email = user.getEmail();
                String accessToken = generateToken(email, phoneNumber, response);

                JwtResponse jwtResponse = new JwtResponse();
                jwtResponse.setAccessToken(accessToken);
                return ResponseEntity.ok(jwtResponse);
            } else {
                return ResponseEntity.ok("User not found");
            }
        } else {
            return ResponseEntity.ok("Incorrect OTP");
        }
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

    // Generate a JWT token with claims.
    public String generateToken(String email, String phoneNumber, HttpServletResponse response) throws IOException {
        User userDtls = userservice.getUserByMail(email.trim());
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
        Set<EnumRole> roles = Collections.singleton(userDtls.getEnumRole());

        // Extract role names as strings
        List<String> roleNames = new ArrayList<>();
        for (EnumRole role : roles) {
            roleNames.add(role.toString());
        }

        // Convert the List<String> to a comma-separated string
        String rolesString = String.join(",", roleNames);
        return JWT.create()
                .withSubject(email)
                .withClaim("phoneNumber", phoneNumber) // Add phone number claim
                .withClaim("roles", rolesString)
                .withClaim("userId", userDtls.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // Set token expiration time
                .sign(algorithm);
    }

    // Helper method to get the current timestamp.
    private String getTimeStamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }
}
