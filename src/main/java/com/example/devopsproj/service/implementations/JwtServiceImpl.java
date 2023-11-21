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

/**
 * Service implementation for handling JWT (JSON Web Token) operations.
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final UserRepository userRepository;

    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);


    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token.
     * @return the extracted username.
     */
    @Override
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        logger.info("Extracted username '{}' from JWT token.", username);
        return username;
    }

    /**
     * Extracts a specific claim from a JWT token using a custom claims resolver function.
     *
     * @param token           the JWT token.
     * @param claimsResolver  the custom claims resolver function.
     * @param <T>             the type of the claim.
     * @return the extracted claim.
     */
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        T claim = claimsResolver.apply(claims);
        logger.info("Extracted claim '{}' from JWT token.", claim);
        return claim;
    }
    /**
     * Generates a JWT token for a given user details.
     *
     * @param userDetails the user details.
     * @return the generated JWT token.
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        String token = generateToken(new HashMap<>(), userDetails);
        logger.info("Generated JWT token for user: {}", userDetails.getUsername());
        return token;
    }

    /**
     * Generates a JWT token with extra claims for a given user details.
     *
     * @param extraClaims    the extra claims to include.
     * @param userDetails    the user details.
     * @return the generated JWT token with extra claims.
     */
    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Key signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String token = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
        logger.info("Generated JWT token with extra claims for user: {}", userDetails.getUsername());
        return token;
    }

    /**
     * Checks if a token is present in the system.
     *
     * @param token the token to check.
     * @return true if the token is found, false otherwise.
     */
    @Override
    public boolean isTokenTrue(String token) {
        User user = userRepository.findUserByToken(token);
        if (user != null) {
            logger.info("Token is true for user: {}", user.getUsername());
            return true;
        } else {
            logger.info("Token is not found in the system");
            return false;
        }
    }
    /**
     * Checks if a token is valid for a given user details.
     *
     * @param token         the token to validate.
     * @param userDetails   the user details.
     * @return true if the token is valid, false otherwise.
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        User user = userRepository.findUserByToken(token);
        if (user != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token)) {
            logger.info("Token is valid for user: {}", userDetails.getUsername());
            return true;
        } else {
            logger.info("Token is not valid for user: {}", userDetails.getUsername());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        boolean isExpired = extractExpiration(token).before(new Date());
        logger.info("Token expiration checked. Expired: {}", isExpired);
        return isExpired;
    }

    private Date extractExpiration(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        logger.info("Extracted token expiration: {}", expiration);
        return expiration;
    }

    private Claims extractAllClaims(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        logger.info("Extracted all claims from JWT token.");
        return claims;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key signingKey = Keys.hmacShaKeyFor(keyBytes);
        logger.info("Retrieved signing key for JWT from the secret key.");
        return signingKey;
    }
}