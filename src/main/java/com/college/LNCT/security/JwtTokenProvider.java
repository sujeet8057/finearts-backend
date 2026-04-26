package com.college.LNCT.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public String generateToken(String email, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", name);
        claims.put("role", "ADMIN");
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)           // ✅ Fixed: removed deprecated SignatureAlgorithm.HS512
                .compact();
    }

    public String getEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()                         // ✅ Fixed: parserBuilder() → parser()
                .verifyWith(key)                     // ✅ Fixed: setSigningKey() → verifyWith()
                .build()
                .parseSignedClaims(token)            // ✅ Fixed: parseClaimsJws() → parseSignedClaims()
                .getPayload()                        // ✅ Fixed: getBody() → getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()                            // ✅ Fixed: parserBuilder() → parser()
                    .verifyWith(key)                 // ✅ Fixed: setSigningKey() → verifyWith()
                    .build()
                    .parseSignedClaims(token);       // ✅ Fixed: parseClaimsJws() → parseSignedClaims()
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }

    public String getNameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return (String) Jwts.parser()               // ✅ Fixed: parserBuilder() → parser()
                .verifyWith(key)                     // ✅ Fixed: setSigningKey() → verifyWith()
                .build()
                .parseSignedClaims(token)            // ✅ Fixed: parseClaimsJws() → parseSignedClaims()
                .getPayload()                        // ✅ Fixed: getBody() → getPayload()
                .get("name");
    }
}
