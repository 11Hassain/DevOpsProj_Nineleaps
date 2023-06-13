//
//package com.example.DevOpsProj.auth;
//
//import com.example.DevOpsProj.commons.enumerations.EnumRole;
//import com.example.DevOpsProj.model.User;
//import com.example.DevOpsProj.repository.UserRepository;
//import com.example.DevOpsProj.service.JwtService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthenticationService {
//
//    private final UserRepository repository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;
//    public AuthenticationResponse register(RegisterRequest request) {
//        var user = User.builder()
//                .name(request.getName())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .enumRole(request.getRole())
//                .deleted(false)
//                .build();
//        repository.save(user);
//        var jwtToken = jwtService.generateToken(user);
//        return AuthenticationResponse.builder()
//                .accessToken(jwtToken)
//                .build();
//    }
//
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//        var user = repository.findByEmail(request.getEmail());
//        var jwtToken = jwtService.generateToken(user);
//        return AuthenticationResponse.builder()
//                .accessToken(jwtToken)
//                .build();
//    }
//}
//
