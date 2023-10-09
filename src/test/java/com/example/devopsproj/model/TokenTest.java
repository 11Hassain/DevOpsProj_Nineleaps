package com.example.devopsproj.model;

import com.example.devopsproj.commons.enumerations.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TokenTest {

    private Token token;

    @BeforeEach
    void setUp() {
        token = Token.builder()
                .id(1)
                .token("example-token")
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .user(new User())
                .build();
    }

    @Test
    void testTokenInitialization() {
        // Arrange
        Token newToken = new Token();

        // Assert
        assertNull(newToken.getId());
        assertNull(newToken.getToken());
        assertEquals(TokenType.BEARER, newToken.getTokenType());
        assertFalse(newToken.isRevoked());
        assertFalse(newToken.isExpired());
        assertNull(newToken.getUser());
    }

    @Test
    void testTokenBuilder() {
        // Assert
        assertEquals(1, token.getId());
        assertEquals("example-token", token.getToken());
        assertEquals(TokenType.BEARER, token.getTokenType());
        assertFalse(token.isRevoked());
        assertFalse(token.isExpired());
        assertNotNull(token.getUser());
    }



    @Test
    void testNotEquals() {
        // Arrange
        Token differentToken = Token.builder()
                .id(2)
                .token("different-token")
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .user(new User())
                .build();

        // Assert
        assertNotEquals(token, differentToken);
        assertNotEquals(token.hashCode(), differentToken.hashCode());
    }

    @Test
    void testToString() {
        // Assert
        assertNotNull(token.toString());
    }
}
