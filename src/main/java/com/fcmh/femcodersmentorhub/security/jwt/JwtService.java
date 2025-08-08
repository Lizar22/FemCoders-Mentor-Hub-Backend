package com.fcmh.femcodersmentorhub.security.jwt;

import com.fcmh.femcodersmentorhub.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final Long JWT_EXPIRATION = 1800000L;

    public String generateToken(CustomUserDetails userDetails) {
        return buildToken(userDetails, JWT_EXPIRATION);
    }

    private String buildToken(CustomUserDetails userDetails, Long jwtExpiration) {
        return Jwts
                .builder()
                .claim("role", userDetails.getAuthorities().toString())
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey())
                .compact();
    }

    public String extractUsername (String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration (String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isValidToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        byte[] bytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }
}
