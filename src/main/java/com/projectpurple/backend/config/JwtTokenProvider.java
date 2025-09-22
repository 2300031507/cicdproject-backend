package com.projectpurple.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    // Generate a secure key for HS512 algorithm that meets the RFC 7518 specification
    private SecretKey getSigningKey() {
        // If the provided jwtSecret is long enough (>= 64 bytes or 512 bits), use it
        if (jwtSecret != null && jwtSecret.length() >= 64) {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        } else {
            // Otherwise generate a secure random key that meets the requirements
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userPrincipal.getUsername());
        
        return Jwts.builder()
                .claims(claims)
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getEmailFromJWT(String token) {
        SecretKey signingKey = getSigningKey();
        Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            SecretKey signingKey = getSigningKey();
            Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (Exception ex) {
            // Invalid JWT token
            System.err.println("JWT validation error: " + ex.getMessage());
        }
        return false;
    }
}