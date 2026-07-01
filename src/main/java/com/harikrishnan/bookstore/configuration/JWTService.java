package com.harikrishnan.bookstore.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.Any;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JWTService {

    private final JwtProperties jwtProperties;

    private final UserDetailsService userDetailsService;

    public SecretKey getSecretKey () {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateToken (String subject, Date expiration) {
        return Jwts.builder().signWith(getSecretKey())
                .issuedAt(new Date())
                .subject(subject)
                .expiration(expiration)
                .compact();
    }

    public String generateAccessToken (String email) {
        return generateToken(email, new Date(System.currentTimeMillis() + jwtProperties.getExpiration()));
    }

    public Claims getClaimsFromToken (String token) {
        return Jwts.parser().verifyWith(getSecretKey())
                .build().parseSignedClaims(token).getPayload();
    }

    public boolean isTokenExpired (String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().before(new Date());
    }

    public String extractEmail (String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token, String email) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject().equals(email) && claims.getExpiration().after(new Date());
    }

}
