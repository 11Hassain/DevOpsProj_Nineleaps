package com.example.devopsproj.service;

import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceImplTest {

    private static final String SECRET_KEY = "59703273357638792F423F4528482B4D6251655468576D5A7134743677397A24432646294A404E635266556A586E327235753878214125442A472D4B61506453";
    @InjectMocks
    private JwtServiceImpl jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSigningInKey() {
        JwtServiceImpl jwtService = new JwtServiceImpl(userRepository);

        Key signingKey = jwtService.getSigningInKey();

        assertNotNull(signingKey);

        assertEquals("HmacSHA512", signingKey.getAlgorithm());
    }

//    @Test
//    void testExtractUsername() {
//        // Create a sample JWT token
//        Claims claims = Jwts.claims();
//        claims.setSubject("testUser");
//        String jwtToken = Jwts.builder().setClaims(claims).signWith(getSigningKey()).compact();
//
//        // Call the extractUsername method
//        String extractedUsername = jwtService.extractUsername(jwtToken);
//
//        // Verify that the username is correctly extracted
//        assertEquals("testUser", extractedUsername);
//    }

//    @Test
//    void testExtractClaim() {
//        // Create a sample JWT token with a custom claim
//        Claims claims = Jwts.claims();
//        claims.put("customClaim", "customValue");
//        String jwtToken = Jwts.builder().setClaims(claims).signWith(getSigningKey()).compact();
//
//        // Call the extractClaim method to extract the custom claim
//        String customClaim = jwtService.extractClaim(jwtToken, claims -> claims.get("customClaim", String.class));
//
//        // Verify that the custom claim is correctly extracted
//        assertEquals("customValue", customClaim);
//    }

    @Test
    void testGenerateToken() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "testUser",
                "testPassword",
                new ArrayList<>()
        );

        // Call the generateToken method to generate a token
        String jwtToken = jwtService.generateToken(userDetails);

        assertNotNull(jwtToken);
    }

    @Test
    void testIsTokenTrue() {
        Claims claims = Jwts.claims();
        claims.setSubject("testUser");
        String jwtToken = Jwts.builder().setClaims(claims).signWith(getSigningKey()).compact();

        when(userRepository.findUserByToken(jwtToken)).thenReturn(new User());

        boolean isTokenTrue = jwtService.isTokenTrue(jwtToken);

        assertTrue(isTokenTrue);
    }

//    @Test
//    void testIsTokenValid() {
//        // Create a sample UserDetails object
//        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
//                "testUser",
//                "testPassword",
//                new ArrayList<>()
//        );
//
//        // Create a sample JWT token
//        Claims claims = Jwts.claims();
//        claims.setSubject("testUser");
//        String jwtToken = Jwts.builder().setClaims(claims).signWith(getSigningKey()).compact();
//
//        // Mock the behavior of userRepository.findUserByToken to return a user
//        when(userRepository.findUserByToken(jwtToken)).thenReturn(new User());
//
//        // Call the isTokenValid method with the token and UserDetails
//        boolean isTokenValid = jwtService.isTokenValid(jwtToken, userDetails);
//
//        // Verify that the token is considered valid
//        assertTrue(isTokenValid);
//    }

//    @Test
//    void testIsTokenExpiredWithExpiredToken() {
//        // Create a sample JWT token with an expiration time in the past
//        Claims claims = Jwts.claims();
//        Date pastExpirationDate = new Date(System.currentTimeMillis() - 3600_000); // 1 hour ago
//        claims.setExpiration(pastExpirationDate);
//
//        String jwtToken = Jwts.builder()
//                .setClaims(claims)
//                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
//                .compact();
//
//        // Mock the getSigningInKey method to return the same key used for signing
//        jwtService.setSigningInKey(getSigningKey());
//
//        // Call the isTokenExpired method with the expired token
//        boolean tokenExpired = jwtService.isTokenExpired(jwtToken);
//
//        // Verify that the token is considered expired
//        assertTrue(tokenExpired);
//    }

//    @Test
//    void testIsTokenExpiredWithNotExpiredToken() {
//        // Create a sample JWT token with an expiration time in the future
//        Claims claims = Jwts.claims();
//        Date futureExpirationDate = new Date(System.currentTimeMillis() + 3600_000); // 1 hour from now
//        claims.setExpiration(futureExpirationDate);
//
//        String jwtToken = Jwts.builder()
//                .setClaims(claims)
//                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
//                .compact();
//
//        // Mock the getSigningInKey method to return the same key used for signing
//        jwtService.setSigningInKey(getSigningKey());
//
//        // Call the isTokenExpired method with the not expired token
//        boolean tokenExpired = jwtService.isTokenExpired(jwtToken);
//
//        // Verify that the token is not considered expired
//        assertFalse(tokenExpired);
//    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

}
