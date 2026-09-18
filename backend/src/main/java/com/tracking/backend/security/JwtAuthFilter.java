package com.tracking.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Skip the normal HTTP JWT filter for the WebSocket endpoint.
     *
     * Why?
     *
     * The browser establishes the WebSocket connection first.
     * The JWT is then sent inside the STOMP CONNECT frame and
     * authenticated by WebSocketAuthInterceptor.
     */
    @Override
    protected boolean shouldNotFilter(
            @NonNull HttpServletRequest request) {

        String servletPath = request.getServletPath();

        return "/ws".equals(servletPath)
                || servletPath.startsWith("/ws/");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        /*
         * Get Authorization header from normal HTTP request.
         *
         * Expected:
         *
         * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
         */
        String authHeader =
                request.getHeader("Authorization");

        /*
         * No JWT supplied.
         *
         * Do not reject the request here.
         * Spring Security will decide later whether
         * the requested endpoint requires authentication.
         */
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Remove "Bearer " from the Authorization header.
         */
        String token = authHeader.substring(7);

        /*
         * Reject an empty Bearer token.
         */
        if (token.isBlank()) {

            System.out.println(
                    "JWT authentication failed: empty token"
            );

            filterChain.doFilter(request, response);
            return;
        }

        try {

            /*
             * Extract username from JWT.
             */
            String username =
                    jwtService.extractUsername(token);

            /*
             * Only authenticate if:
             *
             * 1. Username exists
             * 2. SecurityContext does not already
             *    contain an authenticated user
             */
            if (username != null &&
                    !username.isBlank() &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                /*
                 * Load the corresponding user from database.
                 */
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);

                /*
                 * Validate:
                 *
                 * - signature
                 * - expiration
                 * - username
                 *
                 * according to JwtService implementation.
                 */
                if (jwtService.isTokenValid(
                        token,
                        userDetails)) {

                    /*
                     * Create Spring Security Authentication.
                     */
                    UsernamePasswordAuthenticationToken
                            authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * Attach request details.
                     *
                     * Useful for normal HTTP authentication.
                     */
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /*
                     * Store authenticated user
                     * in SecurityContext.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );

                    System.out.println(
                            "HTTP JWT authentication successful: "
                                    + username
                    );

                } else {

                    System.out.println(
                            "HTTP JWT authentication failed: "
                                    + "invalid or expired token"
                    );
                }
            }

        } catch (Exception e) {

            /*
             * Do not create an authenticated context
             * when JWT processing fails.
             */
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(null);

            System.out.println(
                    "Invalid JWT: "
                            + e.getMessage()
            );
        }

        /*
         * Continue through the Spring Security filter chain.
         */
        filterChain.doFilter(request, response);
    }
}