package com.example.DevOpsProj.otp.OTPService;

import com.example.DevOpsProj.otp.OTPDTO.SmsPojo;
import com.example.DevOpsProj.otp.OTPDTO.StoreOTP;
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

    private String phoneNumber;
    private final String ACCOUNT_SID="AC699c8c2d7ec00f10c39c35569a54ee8d";
    private final String AUTH_TOKEN ="ec332bf4cd438e7dff0a7c1108319d47";
    private final String FROM_NUMBER="+12315974243";

    public void send(SmsPojo sms) throws ParseException{
        Twilio.init(ACCOUNT_SID,AUTH_TOKEN);
        int min = 100000;
        int max = 999999;
        int number  = (int)(Math.random()*(max-min +1)+min);
        // String msg = "Your OTP - "+number+"please verify this otp";
        String msg = "Your OTP - "+number+" . Please verify this OTP. Do not share with anyone. Thank you!";
        Message message = Message.creator(new PhoneNumber(sms.getPhoneNumber()),new PhoneNumber(FROM_NUMBER),msg)
                .create();
        StoreOTP.setOtp(number);
        phoneNumber = sms.getPhoneNumber();
    }

    public String getPhoneNumber() {
        return phoneNumber;
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
