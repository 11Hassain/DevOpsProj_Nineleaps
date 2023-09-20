package com.exAmple.DevOpsProj.otp.OTPConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ExecutorSubscribableChannel;

@Configuration
public class WebSocketConfig {

    @Bean
    public SimpMessagingTemplate simpMessagingTemplate(MessageChannel messageChannel) {
        SimpMessagingTemplate template = new SimpMessagingTemplate(messageChannel);
        // Additional configuration, if needed
        return template;
    }

    @Bean
    public MessageChannel messageChannel() {
        return new ExecutorSubscribableChannel();
    }
}
