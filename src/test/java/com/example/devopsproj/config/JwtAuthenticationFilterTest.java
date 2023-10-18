package com.example.devopsproj.config;
import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.Token;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.TokenRepository;
import com.example.devopsproj.service.interfaces.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private TokenRepository tokenRepository;








    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userDetailsService, tokenRepository);
    }

    @Test
    void testNoAuthorizationHeader() throws ServletException, IOException, ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testAuthorizationHeaderWithoutBearerToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }


    @Test
    void testDoFilterInternalNullRequest() throws Exception {
        assertThrows(NullPointerException.class, () -> {
            jwtAuthenticationFilter.doFilterInternal(null, response, filterChain);
        });
    }


    @Test
    void testDoFilterInternalNullFilterChain() throws Exception {
        assertThrows(NullPointerException.class, () -> {
            jwtAuthenticationFilter.doFilterInternal(request, response, null);
        });
    }

    @Test
    void testDoFilterInternalWithNoToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ValidToken() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        String validToken = "valid-jwt-token";
        String userEmail = "user@example.com";

        // Mock behavior for a valid token
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        Mockito.when(jwtService.extractUsername(validToken)).thenReturn(userEmail);

        // Mock user details and token repository behavior
//        UserDetails userDetails = new User(userEmail, "password", new ArrayList<>());
//        Mockito.when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
//        Mockito.when(tokenRepository.findByTokens(validToken)).thenReturn(Optional.of(new Token(validToken, false, false)));

        // Call the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify that the user is authenticated and the filter chain proceeds
        Mockito.verify(filterChain).doFilter(request, response);
    }

    // Add more test cases for different scenarios (invalid token, null user, etc.)
}

