package com.example.devopsproj.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.devopsproj.commons.enumerations.TokenType;
import com.example.devopsproj.model.Token;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//util for decoding the jwt token generated by the frontend using signup with Google
@Component
public class JwtUtils {

    @Autowired
    private TokenRepository tokenRepository;

    //get the email from the token
    public static String getEmailFromJwt(String jwt){
        DecodedJWT decodedJWT = JWT.decode(jwt);
        return decodedJWT.getClaim("email").asString();
    }

    public  void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .tokenId(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

}
