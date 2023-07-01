package com.example.DevOpsProj.service.TrilioService;

import com.example.DevOpsProj.config.TwilioProperties;
import com.twilio.exception.ApiException;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.verify.v2.service.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneVerificationService {

    @Autowired
    private TwilioProperties twilioProperties;

    public VerificationResult startVerification(String phone){
        try {
            System.out.println(twilioProperties.getServiceId());
            Verification verification = Verification.creator(twilioProperties.getServiceId(), phone,"sms").create();
            System.out.println(verification.getStatus());
            if ("approved".equals(verification.getStatus()) || "pending".equals(verification.getStatus())){
                return new VerificationResult(verification.getSid());
            }
        } catch (ApiException exception){
            return new VerificationResult(new String[]
                    {exception.getMessage()}
            );
        }
        return null;
    }

}
