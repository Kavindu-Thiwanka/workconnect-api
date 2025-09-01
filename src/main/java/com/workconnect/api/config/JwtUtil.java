package com.workconnect.api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "V9OJtOZUm/AtC6nfhwVd9nZ7+P6+E6nUGgPkfFyzbQ8=";
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30 minutes
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 days

    private final Key signingKey;

    public JwtUtil() {
        // Create the Key object from the secret string
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Use the signingKey object to parse the token
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String generateAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(Authentication authentication) {
        return createToken(authentication, REFRESH_TOKEN_EXPIRATION);
    }

    private String createToken(Authentication authentication, long expirationTime) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
