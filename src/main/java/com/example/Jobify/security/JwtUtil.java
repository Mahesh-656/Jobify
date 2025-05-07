package com.example.Jobify.security;

import com.example.Jobify.Repo.UserRepository;
import com.example.Jobify.model.UserData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {


    private final String secret = "secret_key_mahesh-aaaaaaaa-fgdhdjdkasjska";  // You can change this to a more secure secret key
    private final UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String email) {
        UserData user=userRepository.findByEmail(email);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour
                .signWith(getKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    // Validate Token
    public boolean validateToken(String token, String email) {
        return email.equals(extractUsername(token))&&!isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }
}
