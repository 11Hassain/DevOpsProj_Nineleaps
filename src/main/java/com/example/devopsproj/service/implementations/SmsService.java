package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.otpdto.SmsPojo;
import com.example.devopsproj.dto.otpdto.StoreOTP;
import com.example.devopsproj.service.interfaces.IUserService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * The `SmsService` class provides services for sending SMS messages and generating JWT tokens for user authentication.
 *
 * @version 2.0
 */

@Getter
@Component
@Setter
@RequiredArgsConstructor
public class SmsService {

    private String phoneNumber;
    final IUserService userService;

    private static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH");
    private static final String FROM_NUMBER = System.getenv("TWILIO_TRIAL_NUMBER");
    private static final String SECRET_KEY = System.getenv("SMS_SECRET_KEY");

    public void send(SmsPojo sms){
        Twilio.init(ACCOUNT_SID,AUTH_TOKEN);

        // Create a SecureRandom instance for generating OTPs
        SecureRandom secureRandom = new SecureRandom();

        // Define the range for your OTP
        int min = 100000;
        int max = 999999;

        // Generate a random number within the specified range
        int number = secureRandom.nextInt(max - min + 1) + min;

        String msg = "Your OTP - "+number+" . Please verify this OTP. Do not share with anyone. Thank you!";
        Message.creator(new PhoneNumber(sms.getPhoneNumber()),new PhoneNumber(FROM_NUMBER),msg)
                .create();
        StoreOTP.setOtp(number);
        phoneNumber = sms.getPhoneNumber();
    }
}
