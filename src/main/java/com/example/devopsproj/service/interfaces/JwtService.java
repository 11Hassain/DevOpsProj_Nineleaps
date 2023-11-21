package com.example.devopsproj.service.interfaces;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;
/**
 * Service interface for managing JWT-related operations, including extracting
 * username and claims from tokens, generating tokens, and validating token authenticity.
 */

public interface JwtService {

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(UserDetails userDetails);

    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    boolean isTokenTrue(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}





























