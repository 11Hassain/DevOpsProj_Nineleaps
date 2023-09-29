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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final String SECRET_KEY =System.getenv("JWT_SECRET_KEY");

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Override
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        logger.info(claims.getSubject());
        return claims.getSubject();
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 *60* 24))
                .signWith(getSigningInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public boolean isTokenTrue(String token) {
        User user = userRepository.findUserByToken(token);
        // True if the token is true
        return user != null;
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        User user = userRepository.findUserByToken(token);
        // True if token is valid
        return user != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Key getSigningInKey(){
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
}