package com.example.devopsproj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
/**
 * Configuration class for Spring Security to define security settings and filters.
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public SimpMessagingTemplate simpMessagingTemplate(MessageChannel messageChannel) {
        return new SimpMessagingTemplate(messageChannel);
    }

    @Bean
    public MessageChannel messageChannel() {
        return new ExecutorSubscribableChannel();
    }
}