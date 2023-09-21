package com.example.devopsproj.service.implementations;

import com.example.devopsproj.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    // Perform user logout by invalidating the JWT token
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // Retrieve the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If not, return without taking any action
            return;
        }

        // Extract the JWT token from the Authorization header
        jwt = authHeader.substring(7);

        // Retrieve the stored token associated with the JWT
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);

        // If a stored token is found, mark it as expired and revoked
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);

            // Save the updated token in the repository
            tokenRepository.save(storedToken);

            // Clear the security context to log the user out
            SecurityContextHolder.clearContext();
        }
    }
}