package com.example.devopsproj.service.interfaces;

//package com.example.devopsproj.otp.OTPService;

import com.example.devopsproj.otp.OTPDTO.SmsPojo;
import com.example.devopsproj.otp.OTPDTO.StoreOTP;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import java.text.ParseException;

@Component
@Getter
@Setter
public class SmsService {

    @Getter
    private String phoneNumber;
    private final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    private final String AUTH_TOKEN = System.getenv("TWILIO_AUTH");
    private final String FROM_NUMBER = System.getenv("TWILIO_TRIAL_NUMBER");

    public void send(SmsPojo sms) throws ParseException{
        Twilio.init(ACCOUNT_SID,AUTH_TOKEN);
        int min = 100000;
        int max = 999999;
        int number  = (int)(Math.random()*(max-min +1)+min);
        String msg = "Your OTP - "+number+" . Please verify this OTP. Do not share with anyone. Thank you!";
        Message message = Message.creator(new PhoneNumber(sms.getPhoneNumber()),new PhoneNumber(FROM_NUMBER),msg)
                .create();
        StoreOTP.setOtp(number);
        phoneNumber = sms.getPhoneNumber();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void resend(SmsPojo resendsms) throws ParseException{
        Twilio.init(ACCOUNT_SID,AUTH_TOKEN);
        int min = 100000;
        int max = 999999;
        int number  = (int)(Math.random()*(max-min +1)+min);
        String msg = "Your OTP is - "+number+" . Please enter the OTP. Do not share it with anyone. Thank you!";
        Message newmessage = Message.creator(new PhoneNumber(resendsms.getPhoneNumber()),new PhoneNumber(FROM_NUMBER),msg)
                .create();
        StoreOTP.setOtp(number);
    }

    public void recieve (MultiValueMap<String,String> smscallback){
    }
}