package com.example.DevOpsProj.service.TrilioService;

import com.example.DevOpsProj.commons.enumerations.OTPStatus;
import com.example.DevOpsProj.config.TwilioConfig;
import com.example.DevOpsProj.dto.requestDto.OTPRequestDTO;
import com.example.DevOpsProj.dto.responseDto.OTPResponseDTO;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TwilioOTPService {

    @Autowired
    private TwilioConfig twilioConfig;

    Map<String, String> otpMap = new HashMap<>();

    public Mono<OTPResponseDTO> sendOTP(OTPRequestDTO otpRequestDTO){

        OTPResponseDTO otpResponseDTO = null;
        try {
            PhoneNumber to = new PhoneNumber(otpRequestDTO.getPhone());
            PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
            String otp = generateOTP();
            String otpMessage = "Dear customer, your OTP is " + otp + " . Do not share it with anyone. Thank you!";

            Message message = Message
                    .creator(to, from, otpMessage)
                    .create();

            otpMap.put(otpRequestDTO.getEmail(), otp);

            otpResponseDTO = new OTPResponseDTO(OTPStatus.DELIVERED, otpMessage);
        }catch (Exception e){
            otpResponseDTO = new OTPResponseDTO(OTPStatus.FAILED, e.getMessage());
        }
        return Mono.just(otpResponseDTO);
    }

    public Mono<String> validateOTP(String enteredOTP, String email){
        if(enteredOTP.equals(otpMap.get(email))){
            return Mono.just("Valid OTP entered. Please proceed ...");
        }else {
            return Mono.error(new IllegalArgumentException("Invalid OTP. Please try again!"));
        }
    }

    //6-digit otp
    private String generateOTP(){
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }
}
