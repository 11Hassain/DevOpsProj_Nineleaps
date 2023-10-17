package com.example.devopsproj.service.implementations;



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

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

}