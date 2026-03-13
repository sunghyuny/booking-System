package com.booking.api.service;

import com.booking.api.dto.AuthDto;
import com.booking.api.entity.Role;
import com.booking.api.entity.User;
import com.booking.api.repository.UserRepository;
import com.booking.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Long signup(AuthDto.SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Role roleEnum;
        try {
            roleEnum = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            roleEnum = Role.USER; // Default to normal user if invalid
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(roleEnum)
                .build();

        return userRepository.save(user).getId();
    }

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Generate JWT Token
        String token = jwtTokenProvider.createToken(authentication);

        return new AuthDto.TokenResponse(token);
    }
}
