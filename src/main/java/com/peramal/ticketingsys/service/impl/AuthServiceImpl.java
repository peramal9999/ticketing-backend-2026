package com.peramal.ticketingsys.service.impl;

import com.peramal.ticketingsys.config.JwtConfig;
import com.peramal.ticketingsys.dto.request.LoginRequest;
import com.peramal.ticketingsys.dto.request.RefreshTokenRequest;
import com.peramal.ticketingsys.dto.request.RegisterRequest;
import com.peramal.ticketingsys.dto.response.AuthResponse;
import com.peramal.ticketingsys.dto.response.UserResponse;
import com.peramal.ticketingsys.entity.User;
import com.peramal.ticketingsys.entity.enums.Role;
import com.peramal.ticketingsys.exception.ResourceNotFoundException;
import com.peramal.ticketingsys.repository.UserRepository;
import com.peramal.ticketingsys.security.JwtService;
import com.peramal.ticketingsys.service.AuthService;
import com.peramal.ticketingsys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already in use: {}", request.getEmail());
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.END_USER)
                .isActive(true)
                .build();
        userRepository.save(user);
        log.info("New user registered: email={}", request.getEmail());
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt: email={}", request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
        log.info("Login successful: email={}, role={}", user.getEmail(), user.getRole());
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        String email;
        try {
            email = jwtService.extractRefreshTokenUsername(request.getRefreshToken());
        } catch (Exception e) {
            log.warn("Refresh token extraction failed: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid refresh token");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        if (!jwtService.isRefreshTokenValid(request.getRefreshToken(), user)) {
            log.warn("Refresh token validation failed for: {}", email);
            throw new IllegalArgumentException("Refresh token is expired or invalid");
        }
        log.info("Access token refreshed for: {}", email);
        return buildAuthResponse(user);
    }

    @Override
    public UserResponse getMe() {
        return toUserResponse(SecurityUtils.getCurrentUser());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(User user) {
        String accessToken  = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAuthExpirationMs())
                .user(toUserResponse(user))
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
