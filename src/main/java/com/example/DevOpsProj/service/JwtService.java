package com.example.DevOpsProj.service;

import com.example.DevOpsProj.model.User;
import com.example.DevOpsProj.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JwtService {
    @Autowired
    private UserRepository userRepository;
    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    //change it
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Key signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }
    public boolean isTokenTrue(String token) {
        User user = userRepository.findUserByToken(token);
        if (user != null ) {
            // Token is valid
            return true;
        }
        // Token is invalid
        return false;
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        User user = userRepository.findUserByToken(token);
        if (user != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token)) {
            // Token is valid
            return true;
        }
        // Token is invalid
        return false;
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}