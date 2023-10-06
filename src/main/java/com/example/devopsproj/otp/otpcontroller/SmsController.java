package com.example.devopsproj.otp.otpcontroller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.User;
import com.example.devopsproj.otp.otpservice.IUserService;
import com.example.devopsproj.otp.otpservice.SmsService;
import com.example.devopsproj.otp.otpdto.JwtResponse;
import com.example.devopsproj.otp.otpdto.SmsPojo;
import com.example.devopsproj.otp.otpdto.StoreOTP;
import com.example.devopsproj.otp.otpdto.TempOTP;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/v1/OTP")
@RestController
@Validated
@RequiredArgsConstructor
public class SmsController {

    final SmsService service;
    final IUserService userService;
    private final SimpMessagingTemplate webSocket;

    private static final String TOPIC_DESTINATION = "/lesson/sms";


    @PostMapping("/send")
    @Operation(
            description = "Submit SMS",
            responses = {
                    @ApiResponse(responseCode = "200", description = "SMS sent successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error - Something went wrong")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> smsSubmit(@Valid @RequestBody SmsPojo sms){
        try{
            service.send(sms);
        }catch (Exception e){
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        webSocket.convertAndSend(TOPIC_DESTINATION,getTimeStamp()+":SMS has been sent "+sms.getPhoneNumber());
        return new ResponseEntity<>("OTP sent",HttpStatus.OK);
    }

    @PostMapping("/resend")
    @Operation(
            description = "Resend SMS",
            responses = {
                    @ApiResponse(responseCode = "200", description = "SMS resent successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error - Something went wrong")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> smsSub(@Valid @RequestBody SmsPojo resendSms){
        try{
            service.send(resendSms);
        }catch (Exception e){
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        webSocket.convertAndSend(TOPIC_DESTINATION,getTimeStamp()+":SMS has been sent "+resendSms.getPhoneNumber());
        return new ResponseEntity<>("OTP sent",HttpStatus.OK);
    }

    @PostMapping("/verify/token")
    @Operation(
            description = "Verify OTP with token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Incorrect OTP"),
                    @ApiResponse(responseCode = "200", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Object> verifyOTP(@Valid @RequestBody TempOTP tempOTP) {

        if (tempOTP.getOtp() == StoreOTP.getOtp()) {
            String phoneNumber = service.getPhoneNumber();
            // Instantiate the Sms class and set the phoneNumber
            SmsPojo sms = new SmsPojo();
            sms.setPhoneNumber(phoneNumber);
            User user = userService.getUserViaPhoneNumber(sms.getPhoneNumber());

            if (user != null) {
                String email = user.getEmail();
                String accessToken= service.generateToken(email, phoneNumber);

                JwtResponse jwtResponse = new JwtResponse();
                jwtResponse.setAccessToken(accessToken);
                return ResponseEntity.ok(jwtResponse);
            } else {
                return ResponseEntity.ok("User not found") ;
            }
        }
        else {
            return ResponseEntity.ok("Incorrect OTP");
        }
    }

    @PostMapping("/verify")
    @Operation(
            description = "Verify OTP for signup",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Incorrect OTP")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public Boolean verifyOTPSignUp(@Valid @RequestBody TempOTP sms, HttpServletResponse response){

        return sms.getOtp() == StoreOTP.getOtp();
    }

    // ----- Helper Methods -----

    private String getTimeStamp(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

}
