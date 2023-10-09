package com.example.devopsproj.model;

import com.example.devopsproj.commons.enumerations.TokenType;
import com.example.devopsproj.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenTest {

    @InjectMocks
    private Token token;
    @Mock
    private TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize a Token instance before each test
        token = Token.builder()
                .id(1)
                .tokenId("token123")
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .user(new User())  // You may need to create a User instance
                .build();
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1, token.getId());
        assertEquals("token123", token.getTokenId());
        assertEquals(TokenType.BEARER, token.getTokenType());
        assertFalse(token.isRevoked());
        assertFalse(token.isExpired());
        assertNotNull(token.getUser());

        token.setId(2);
        token.setTokenId("newToken");
        token.setTokenType(TokenType.BEARER);
        token.setRevoked(true);
        token.setExpired(true);
        User newUser = new User();
        token.setUser(newUser);

        assertEquals(2, token.getId());
        assertEquals("newToken", token.getTokenId());
        assertEquals(TokenType.BEARER, token.getTokenType());
        assertTrue(token.isRevoked());
        assertTrue(token.isExpired());
        assertEquals(newUser, token.getUser());
    }

    @Test
    void testToString() {
        assertNotNull(token.toString());
    }

    @Test
    void testConstructorWithDefaultValues() {
        Token defaultToken = new Token();

        assertEquals(TokenType.BEARER, defaultToken.getTokenType());
        assertFalse(defaultToken.isRevoked());
        assertFalse(defaultToken.isExpired());
        assertNull(defaultToken.getUser());
    }

}
