package com.harikrishnan.bookstore.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JWTService {

    private final JwtProperties jwtProperties;

    public SecretKey getSigningKey () {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes()
        );
    }


    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims (String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail (String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenExpired (String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid (String token,String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

}
