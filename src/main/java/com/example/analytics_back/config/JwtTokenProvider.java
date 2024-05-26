package com.example.analytics_back.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtTokenProvider {
    private final Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);;
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().iterator().next())
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            // Обработка случая, когда токен истек
            return false;
        } catch (Exception ex) {
            // Обработка других ошибок валидации токена
            return false;
        }
    }

}
