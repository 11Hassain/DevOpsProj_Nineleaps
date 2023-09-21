package com.example.devopsproj.service.implementations;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.service.interfaces.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final UserRepository userRepository;

    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");

    // Extract the username from a JWT token
    @Override
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    // Extract a specific claim from a JWT token using a claims resolver function
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generate a JWT token for a UserDetails object
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generate a JWT token with extra claims for a UserDetails object
    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        // Generate a signing key for JWT
        Key signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // Build and sign the JWT token with the specified claims
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Check if a token exists in the UserRepository, indicating its validity
    @Override
    public boolean isTokenTrue(String token) {
        User user = userRepository.findUserByToken(token);
        if (user != null) {
            // Token is valid
            return true;
        }
        // Token is invalid
        return false;
    }

    // Check if a token is valid by verifying the username, expiration, and token expiry
    @Override
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

    // Check if a token is expired based on its expiration date
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract the expiration date from a JWT token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract all claims from a JWT token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Get the signing key for JWT from the secret key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}