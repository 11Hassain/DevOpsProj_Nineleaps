package com.example.devopsproj.controller;

import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.service.implementations.SmsService;
import com.example.devopsproj.dto.otpdto.SmsPojo;
import com.example.devopsproj.dto.otpdto.StoreOTP;
import com.example.devopsproj.dto.otpdto.TempOTP;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * The `SmsController` class is a Spring MVC REST controller that handles endpoints related to sending and verifying SMS messages for OTP-based authentication.
 *
 * @version 2.0
 */

@RequestMapping("/api/v1/OTP")
@RestController
@Validated
@RequiredArgsConstructor
public class SmsController {

    final SmsService service;
    final IUserService userService;
    private final SimpMessagingTemplate webSocket;
    private static final String TOPIC_DESTINATION = "/lesson/sms";
    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    /**
     * Submit SMS.
     *
     * @param sms The SMS details
     * @return ResponseEntity indicating the result of SMS submission.
     */
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
        logger.info("Received a request to submit SMS");

        try{
            service.send(sms);
            logger.info("SMS sent successfully");
        }catch (Exception e){
            logger.error("Internal Server Error Occurred: {}", e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        webSocket.convertAndSend(TOPIC_DESTINATION,getTimeStamp()+":SMS has been sent "+sms.getPhoneNumber());
        return new ResponseEntity<>("OTP sent",HttpStatus.OK);
    }

    /**
     * Verify OTP for signup.
     *
     * @param sms       The OTP details.
     * @param response  The HTTP response.
     * @return Boolean indicating whether OTP is verified successfully.
     */
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
        logger.info("Received a request to verify OTP for signup");
        return sms.getOtp() == StoreOTP.getOtp();
    }

    // ----- Helper Methods -----

    private String getTimeStamp(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

}
