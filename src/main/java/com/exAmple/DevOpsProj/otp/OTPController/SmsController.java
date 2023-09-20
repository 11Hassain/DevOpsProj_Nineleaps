package com.exAmple.DevOpsProj.otp.OTPController;

import com.exAmple.DevOpsProj.commons.enumerations.EnumRole;
import com.exAmple.DevOpsProj.model.User;
import com.exAmple.DevOpsProj.otp.OTPService.IUserService;
import com.exAmple.DevOpsProj.otp.OTPService.SmsService;
import com.exAmple.DevOpsProj.otp.OTPDTO.JwtResponse;
import com.exAmple.DevOpsProj.otp.OTPDTO.SmsPojo;
import com.exAmple.DevOpsProj.otp.OTPDTO.StoreOTP;
import com.exAmple.DevOpsProj.otp.OTPDTO.TempOTP;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SmsController {

    @Autowired
    SmsService service;
    @Autowired
    IUserService userservice;
    @Autowired
    private SimpMessagingTemplate webSocket;
    private static final String TOPIC_DESTINATION = "/lesson/sms";
    private static final String SECRET_KEY = System.getenv("SMS_SECRET_KEY");


    @PostMapping("/send")
    public ResponseEntity<String> smsSubmit(@RequestBody SmsPojo sms){
        try{
            service.send(sms);
        }catch (Exception e){
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        webSocket.convertAndSend(TOPIC_DESTINATION,getTimeStamp()+":SMS has been sent "+sms.getPhoneNumber());
        return new ResponseEntity<>("OTP sent",HttpStatus.OK);
    }

    @PostMapping("/resend")
    public ResponseEntity<String> smsSub(@RequestBody SmsPojo resendsms){
        try{
            service.send(resendsms);
        }catch (Exception e){
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        webSocket.convertAndSend(TOPIC_DESTINATION,getTimeStamp()+":SMS has been sent "+resendsms.getPhoneNumber());
        return new ResponseEntity<>("OTP sent",HttpStatus.OK);
    }

    @PostMapping("/verify/token")
    public ResponseEntity<Object> verifyOTP(@RequestBody TempOTP tempOTP, HttpServletResponse response) throws Exception {

        if (tempOTP.getOtp() == StoreOTP.getOtp()) {
            String phoneNumber = service.getPhoneNumber();
            // Instantiate the Sms class and set the phoneNumber
            SmsPojo sms = new SmsPojo();
            sms.setPhoneNumber(phoneNumber);
            User user = userservice.getUserViaPhoneNumber(sms.getPhoneNumber());

            if (user != null) {
                String email = user.getEmail();
                String accessToken= generateToken(email, phoneNumber, response);

                JwtResponse jwtResponse = new JwtResponse();
                jwtResponse.setAccessToken(accessToken);
                return ResponseEntity.ok(jwtResponse);}
            else {
                return ResponseEntity.ok("User not found") ;
            }
        }
        else {
            return ResponseEntity.ok("Incorrect OTP");
        }
    }

    @PostMapping("/verify")
    public Boolean verifyOTPsignup(@RequestBody TempOTP sms,HttpServletResponse response) throws Exception{

        if(sms.getOtp()== StoreOTP.getOtp()) {
            return true;
        }
        else
            return false;
    }

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
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 *10))
                .sign(algorithm);
    }

    private String getTimeStamp(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

}
