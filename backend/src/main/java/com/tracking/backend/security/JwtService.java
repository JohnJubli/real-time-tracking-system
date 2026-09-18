package com.tracking.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final Key signingKey;
    private final long jwtExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long jwtExpiration
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.jwtExpiration = jwtExpiration;
    }

    // ==============================
    // Generate Token
    // ==============================
    public String generateToken(UserDetails userDetails) {

        String username = userDetails.getUsername();

        System.out.println("Token generated for: " + username);

        return Jwts.builder()
                .setSubject(username)
                .claim("role",
                        userDetails.getAuthorities().iterator().next().getAuthority())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ==============================
    // Extract Username
    // ==============================
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ==============================
    // Extract Role
    // ==============================
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // ==============================
    // Token Validation
    // ==============================
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ==============================
    // Expiration Check
    // ==============================
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ==============================
    // Generic Claim Extractor
    // ==============================
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // ==============================
    // Parse All Claims
    // ==============================
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}