package com.example.devopsproj.service.implementations;
import com.example.devopsproj.dto.responsedto.SmsPojo;
import com.example.devopsproj.dto.responsedto.StoreOTP;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.security.SecureRandom;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Getter
@Setter
public class SmsService {

    @Getter
    private String phoneNumber;
    private final String accountsid = System.getenv("TWILIO_ACCOUNT_SID");
    private final String authtoken = System.getenv("TWILIO_AUTH");
    private final String fromnumber = System.getenv("TWILIO_TRIAL_NUMBER");


    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    public void send(SmsPojo sms) {
        logger.info("Sending SMS");
        Twilio.init(accountsid,authtoken);
        logger.info("Twilio initialized");

        SecureRandom secureRandom = new SecureRandom();
        int number = secureRandom.nextInt(900000) + 100000; // Generates a random 6-digit number

        String msg = "Your OTP - " + number + "   please verify this OTP   a6mge7C9IMY";
        Message.creator(new PhoneNumber(sms.getPhoneNumber()), new PhoneNumber(fromnumber), msg)
                .create();
        StoreOTP.setOtp(number);
        phoneNumber = sms.getPhoneNumber();
        logger.info("SMS sent to phone number: {}", phoneNumber);
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getTimeStamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }


}