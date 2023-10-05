package com.example.devopsproj.service.implementations;

import com.example.devopsproj.otp.OTPDTO.SmsPojo;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;


class SmsServiceTest {

    private SmsService smsService;

    @Mock
    private SmsPojo smsPojo;

    @Mock
    private Logger logger;
    private final String accountsid = System.getenv("TWILIO_ACCOUNT_SID");
    private final String authtoken = System.getenv("TWILIO_AUTH");


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        smsService = new SmsService();
        smsService.setPhoneNumber("+1234567890"); // Set a sample phone number
    }


}

