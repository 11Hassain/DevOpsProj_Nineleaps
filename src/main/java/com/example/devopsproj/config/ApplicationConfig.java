package com.example.devopsproj.config;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The ApplicationConfig class provides configuration for various components in the DevOps project application.
 * It defines beans for a ModelMapper, GitHubUserValidation, CollaboratorDTO, UserDetailsService, AuthenticationProvider,
 * AuthenticationManager, and PasswordEncoder.
 * .
 * This configuration class is responsible for configuring and managing application components and their dependencies.
 *
 * @version 2.0
 */

@Configuration
@ConfigurationProperties
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public CollaboratorDTO collaboratorDTO() {
        return new CollaboratorDTO();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return repository::findByEmail;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
