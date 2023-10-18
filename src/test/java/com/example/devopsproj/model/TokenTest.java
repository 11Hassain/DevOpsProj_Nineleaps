package com.example.devopsproj.model;

import com.example.devopsproj.commons.enumerations.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

 class TokenTest {

    private Token token;

    @BeforeEach
    void setUp() {
        token = Token.builder()
                .id(1)
                .tokens("example-token")
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
        assertNull(newToken.getTokens());
        assertEquals(TokenType.BEARER, newToken.getTokenType());
        assertFalse(newToken.isRevoked());
        assertFalse(newToken.isExpired());
        assertNull(newToken.getUser());
    }

    @Test
    void testTokenBuilder() {
        // Assert
        assertEquals(1, token.getId());
        assertEquals("example-token", token.getTokens());
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
                .tokens("different-token")
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
    @Test
     void testGettersAndSetters() {
        // Create a Token instance
        Token token = new Token();

        // Set values using setters
        token.setId(1);
        token.setTokens("example_token");
        token.setTokenType(TokenType.BEARER);
        token.setRevoked(false);
        token.setExpired(false);

        // Retrieve values using getters
        assertEquals(1, token.getId());
        assertEquals("example_token", token.getTokens());
        assertEquals(TokenType.BEARER, token.getTokenType());
        assertEquals(false, token.isRevoked());
        assertEquals(false, token.isExpired());
    }
}
