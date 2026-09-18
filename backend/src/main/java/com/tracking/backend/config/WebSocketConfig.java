package com.tracking.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig
        implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    public WebSocketConfig(
            WebSocketAuthInterceptor webSocketAuthInterceptor) {

        this.webSocketAuthInterceptor =
                webSocketAuthInterceptor;
    }

    /**
     * Configure STOMP message broker.
     */
    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry registry) {

        /*
         * Messages sent to /topic are handled
         * by Spring's simple broker.
         */
        registry.enableSimpleBroker("/topic");

        /*
         * Client -> Server messages must start
         * with /app.
         *
         * Example:
         * /app/sendLocation
         */
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Register the native WebSocket endpoint.
     */
    @Override
    public void registerStompEndpoints(
            StompEndpointRegistry registry) {

        registry
                .addEndpoint("/ws")

                /*
                 * React development server.
                 */
                .setAllowedOriginPatterns(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000"
                );

        /*
         * IMPORTANT:
         * Do NOT add .withSockJS()
         */
    }

    /**
     * Register our JWT authentication interceptor.
     */
    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {

        registration.interceptors(
                webSocketAuthInterceptor
        );
    }
}