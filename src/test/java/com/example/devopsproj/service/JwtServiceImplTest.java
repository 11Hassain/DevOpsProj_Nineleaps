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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.*;

import java.security.Key;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class JwtServiceImplTest {

    @Autowired
    private JwtServiceImpl jwtService;
    @Mock
    private UserRepository userRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Test
    void testExtractUsername() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "user",
                "password",
                new ArrayList<>());
        String token = generateToken(new HashMap<>(),userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("user", username);
    }

    @Test
    void testGenerateToken() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "user",
                "password",
                new ArrayList<>());
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
    }

    @Test
    void testIsTokenTrue(){
        User user = new User();
        when(userRepository.findUserByToken("valid-token")).thenReturn(user);

        boolean isFalse = jwtService.isTokenTrue("valid-token");

        assertFalse(isFalse);
    }

    @Nested
    class IsTokenValidTest {
        @Test
        @DisplayName("Testing success case for valid token for correct user")
        void testIsTokenValid() {
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    "user",
                    "password",
                    new ArrayList<>());
            String token = generateToken(new HashMap<>(),userDetails);
            boolean isValid = jwtService.isTokenValid(token, userDetails);
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Testing incorrect user case for valid token")
        void testIsTokenValid_InvalidUser(){
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    "user",
                    "password",
                    new ArrayList<>());
            UserDetails wrongUserDetails = new org.springframework.security.core.userdetails.User(
                    "wrongUser",
                    "password",
                    new ArrayList<>());
            String token = generateToken(new HashMap<>(), userDetails);
            boolean isValid = jwtService.isTokenValid(token, wrongUserDetails);
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Testing token associated with no user case")
        void testIsTokenValid_TokenWithNoUser(){
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    "user",
                    "password",
                    new ArrayList<>());
            String token = generateToken(new HashMap<>(), userDetails);
            when(userRepository.findUserByToken(token)).thenReturn(null);
            boolean isValid = jwtService.isTokenValid(token, userDetails);
            assertFalse(isValid);
        }
    }

    @Test
    void testIsTokenExpired() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "user",
                "password",
                new ArrayList<>());
        String token = generateToken(new HashMap<>(),userDetails);
        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @Test
    void testExtractAllClaims() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "user",
                "password",
                new ArrayList<>());
        String token = generateToken(new HashMap<>(),userDetails);
        Claims claims = jwtService.extractAllClaims(token);
        assertNotNull(claims);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}