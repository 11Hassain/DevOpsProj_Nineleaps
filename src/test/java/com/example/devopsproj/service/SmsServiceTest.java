package com.example.devopsproj.service;

import com.example.devopsproj.dto.otpdto.SmsPojo;
import com.example.devopsproj.dto.otpdto.StoreOTP;
import com.example.devopsproj.service.implementations.SmsService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class SmsServiceTest {

    @InjectMocks
    private SmsService smsService;

    @Value("${twilio.trial-number}")
    private String twilioFromNumber;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSetPhoneNumber() {
        smsService.setPhoneNumber("1234567890");

        assertEquals("1234567890", smsService.getPhoneNumber());
    }

    @Test
    void testSendWithInvalidPhoneNumber() {
        SmsPojo sms = new SmsPojo();
        sms.setPhoneNumber("invalidPhoneNumber");

        assertThrows(com.twilio.exception.AuthenticationException.class, () -> smsService.send(sms));
    }

}
