package com.example.devopsproj.otp.OTPService;

import com.example.devopsproj.otp.OTPDTO.SmsPojo;
import com.example.devopsproj.otp.OTPDTO.StoreOTP;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.security.SecureRandom;

@Getter
@Component
@Setter
public class SmsService {

    private String phoneNumber;
    private static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH");
    private static final String FROM_NUMBER = System.getenv("TWILIO_TRIAL_NUMBER");

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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void resend(SmsPojo resendsms){
        Twilio.init(ACCOUNT_SID,AUTH_TOKEN);
        // Create a SecureRandom instance for generating OTPs
        SecureRandom secureRandom = new SecureRandom();

        // Define the range for your OTP
        int min = 100000;
        int max = 999999;

        // Generate a random number within the specified range
        int number = secureRandom.nextInt(max - min + 1) + min;
        String msg = "Your OTP is - "+number+" . Please enter the OTP. Do not share it with anyone. Thank you!";
        Message.creator(new PhoneNumber(resendsms.getPhoneNumber()),new PhoneNumber(FROM_NUMBER),msg)
                .create();
        StoreOTP.setOtp(number);
    }

    public void recieve (MultiValueMap<String,String> smscallback){
    }
}
