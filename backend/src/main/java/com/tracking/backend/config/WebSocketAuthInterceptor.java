package com.tracking.backend.config;

import com.tracking.backend.security.JwtService;
import com.tracking.backend.service.CustomUserDetailsService;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor
        implements ChannelInterceptor {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public WebSocketAuthInterceptor(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor == null) {
            return message;
        }

        /*
         * Authenticate only when the STOMP client
         * establishes the connection.
         */
        if (StompCommand.CONNECT.equals(
                accessor.getCommand())) {

            String authorization =
                    accessor.getFirstNativeHeader(
                            "Authorization"
                    );

            System.out.println(
                    "WebSocket STOMP CONNECT received"
            );

            /*
             * Check Authorization header.
             */
            if (authorization == null ||
                    !authorization.startsWith("Bearer ")) {

                System.out.println(
                        "WebSocket authentication failed: " +
                                "Authorization header missing"
                );

                throw new IllegalArgumentException(
                        "Missing Authorization header"
                );
            }

            /*
             * Remove "Bearer ".
             */
            String token =
                    authorization.substring(7);

            try {

                /*
                 * Extract username from JWT.
                 */
                String username =
                        jwtService.extractUsername(token);

                if (username == null ||
                        username.isBlank()) {

                    throw new IllegalArgumentException(
                            "Username not found in JWT"
                    );
                }

                System.out.println(
                        "WebSocket JWT username: "
                                + username
                );

                /*
                 * Load user from database.
                 */
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);

                /*
                 * Validate token.
                 */
                if (!jwtService.isTokenValid(
                        token,
                        userDetails)) {

                    System.out.println(
                            "WebSocket authentication failed: " +
                                    "Invalid JWT"
                    );

                    throw new IllegalArgumentException(
                            "Invalid JWT token"
                    );
                }

                /*
                 * Create Spring Security Authentication.
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                /*
                 * Attach authenticated user to STOMP session.
                 *
                 * This is what allows:
                 *
                 * Principal principal
                 *
                 * inside WebSocketLocationController.
                 */
                accessor.setUser(authentication);

                System.out.println(
                        "WebSocket authentication successful: "
                                + username
                );

            } catch (Exception exception) {

                System.out.println(
                        "WebSocket JWT authentication error: "
                                + exception.getMessage()
                );

                throw new IllegalArgumentException(
                        "WebSocket authentication failed",
                        exception
                );
            }
        }

        return message;
    }
}