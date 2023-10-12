package com.example.devopsproj.config;

import com.example.devopsproj.service.implementations.JwtServiceImpl;
import com.example.devopsproj.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * The JwtAuthenticationFilter class is responsible for filtering and processing JWT-based authentication for incoming
 * requests. It extracts JWT tokens from the "Authorization" header, verifies the token's validity, and sets the
 * authenticated user in the Spring Security context if the token is valid.
 * .
 * This filter is applied to incoming requests and integrates JWT-based authentication into the application's security system.
 *
 * @version 2.0
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServiceImpl jwtServiceImpl;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    public void doFilterInternal(
            @NonNull HttpServletRequest request,//THIS REQUEST IS OUR REQUEST
            @NonNull HttpServletResponse response,// THIS RESPONSE IS ALSO OUR RESPONSE
            @NonNull FilterChain filterChain//THIS BASICALLY CONTAIN THE LIST OF THE FILTER
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        //HERE WE ARE CHECKING THAT OUR TOKEN EXIST OR NOT
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //HERE WE ARE EXTRACTING THE JWT TOKEN AND USERNAME IN THE NEXT LINE
        jwt = authHeader.substring(7);
        userEmail = jwtServiceImpl.extractUsername(jwt);
        //NOW WE EXTRACTING THE USER FRO TH DATABASE FOR THE VERIFICATION
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            var isTokenValid = tokenRepository.findByTokenId(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtServiceImpl.isTokenValid(jwt, userDetails) && Boolean.TRUE.equals(isTokenValid)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
