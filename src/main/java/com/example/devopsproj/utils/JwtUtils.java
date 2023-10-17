package com.example.devopsproj.utils;


import com.example.devopsproj.commons.enumerations.TokenType;
import com.example.devopsproj.model.Token;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.TokenRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final TokenRepository tokenRepository;

    public  void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .tokens(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

}