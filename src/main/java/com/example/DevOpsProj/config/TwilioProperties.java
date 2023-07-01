package com.example.DevOpsProj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("twilio")
@Data
public class TwilioProperties {

    private String accountSid;
    private String authToken;
    private String serviceId;

//    public TwilioProperties() {
//    }
//
//    public TwilioProperties(String accountSid, String authToken, String serviceId) {
//        this.accountSid = accountSid;
//        this.authToken = authToken;
//        this.serviceId = serviceId;
//    }
//
//    public String getAccountSid() {
//        return accountSid;
//    }
//
//    public void setAccountSid(String accountSid) {
//        this.accountSid = accountSid;
//    }
//
//    public String getAuthToken() {
//        return authToken;
//    }
//
//    public void setAuthToken(String authToken) {
//        this.authToken = authToken;
//    }
//
//    public String getServiceId() {
//        return serviceId;
//    }
//
//    public void setServiceId(String serviceId) {
//        this.serviceId = serviceId;
//    }
}
