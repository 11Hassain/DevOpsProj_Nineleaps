package com.example.devopsproj.utils;

import com.example.devopsproj.model.Token;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;
    @Mock
    private TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUserToken() {
        User user = new User();
        user.setId(1L);

        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        jwtUtils.saveUserToken(user, "jwt_token");

        verify(tokenRepository, times(1)).save(any(Token.class));
    }

}
