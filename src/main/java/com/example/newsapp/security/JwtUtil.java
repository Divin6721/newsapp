package com.example.newsapp.security;

import com.example.newsapp.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.expiration-time}")
    private long accessTokenExpiration;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    // Извлечение имени пользователя из токена
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Проверка, просрочен ли токен
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Генерация токена
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail()) // email будет именем пользователя
                .setIssuedAt(new Date()) // когда токен создан
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 минут
                .signWith(getSignKey(), SignatureAlgorithm.HS256)// подпись
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 дней
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // Валидация токена: совпадает ли имя и не просрочен ли
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Вспомогательный метод для извлечения всех Claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
