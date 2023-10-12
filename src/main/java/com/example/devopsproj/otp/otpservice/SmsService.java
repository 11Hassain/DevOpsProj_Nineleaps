package com.example.devopsproj.otp.otpservice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.User;
import com.example.devopsproj.otp.otpdto.SmsPojo;
import com.example.devopsproj.otp.otpdto.StoreOTP;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.*;

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

    public String generateToken(String email, String phoneNumber) {
        User userDtls = userService.getUserByMail(email.trim());
        if (userDtls == null) {
            return null; // Handle the case where the user is not found
        }

        Set<EnumRole> roles = Collections.singleton(userDtls.getEnumRole());
        List<String> roleNames = roles.stream()
                .map(EnumRole::toString)
                .toList();

        String rolesString = String.join(",", roleNames);

        return createJwtToken(email, phoneNumber, rolesString, userDtls.getId());
    }

    String createJwtToken(String email, String phoneNumber, String rolesString, Long userId) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());

        return JWT.create()
                .withSubject(email)
                .withClaim("phoneNumber", phoneNumber)
                .withClaim("roles", rolesString)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .sign(algorithm);
    }
}
