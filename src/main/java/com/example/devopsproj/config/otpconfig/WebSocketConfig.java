package com.example.devopsproj.config.otpconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ExecutorSubscribableChannel;

/**
 * The `WebSocketConfig` class is a Spring configuration class that defines and configures WebSocket-related beans for handling real-time messaging via WebSocket.
 *
 * @version 2.0
 */

@Configuration
public class WebSocketConfig {

    /**
     * Creates and configures a `SimpMessagingTemplate` bean for sending messages over WebSocket.
     *
     * @param messageChannel The `MessageChannel` used for WebSocket messaging.
     * @return A configured `SimpMessagingTemplate` bean.
     */
    @Bean
    public SimpMessagingTemplate simpMessagingTemplate(MessageChannel messageChannel) {
        return new SimpMessagingTemplate(messageChannel);
    }

    /**
     * Creates and configures a `MessageChannel` bean for handling WebSocket messages.
     *
     * @return A configured `MessageChannel` bean.
     */
    @Bean
    public MessageChannel messageChannel() {
        return new ExecutorSubscribableChannel();
    }
}
