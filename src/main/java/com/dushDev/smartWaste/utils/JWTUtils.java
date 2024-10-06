package com.dushDev.smartWaste.utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Logger;

@Component
public class JWTUtils {

    private static final Logger LOGGER = Logger.getLogger(JWTUtils.class.getName());
    private static final long EXPIRATION_TIME = 1000 * 60 * 24 * 7; // 7 days
    private final SecretKey key;

    public JWTUtils() {
        String secretString = "reteyrurifncncbcvcdhdhdhdmdgsteye64647485ormmxnxhdhdkhwywy33737484937393ieuyrjfvbvnnhye4746758594746584938euedjhfbmmncbshd";
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
            this.key = Keys.hmacShaKeyFor(keyBytes);
            LOGGER.info("Secret key successfully initialized.");
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize secret key: " + e.getMessage());
            throw new IllegalArgumentException("Invalid secret key configuration", e);
        }
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}

