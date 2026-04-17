package com.peramal.ticketingsys.service.impl;

import com.peramal.ticketingsys.dto.request.CreateUserRequest;
import com.peramal.ticketingsys.dto.request.UpdateUserRequest;
import com.peramal.ticketingsys.dto.response.UserResponse;
import com.peramal.ticketingsys.entity.User;
import com.peramal.ticketingsys.exception.ResourceNotFoundException;
import com.peramal.ticketingsys.repository.UserRepository;
import com.peramal.ticketingsys.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-password}")
    private String defaultPassword;

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public UserResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public UserResponse create(CreateUserRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            log.warn("User creation failed - email already in use: {}", req.getEmail());
            throw new IllegalArgumentException("Email already in use: " + req.getEmail());
        }
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(defaultPassword))
                .role(req.getRole())
                .isActive(true)
                .build();
        UserResponse response = toResponse(userRepository.save(user));
        log.info("User created: id={}, email={}, role={}", response.getId(), response.getEmail(), response.getRole());
        return response;
    }

    @Override
    public UserResponse update(UUID id, UpdateUserRequest req) {
        User user = findOrThrow(id);
        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());
        if (req.getEmail() != null) {
            if (!req.getEmail().equalsIgnoreCase(user.getEmail()) &&
                    userRepository.existsByEmail(req.getEmail())) {
                log.warn("User update failed - email already in use: {}", req.getEmail());
                throw new IllegalArgumentException("Email already in use: " + req.getEmail());
            }
            user.setEmail(req.getEmail());
        }
        if (req.getRole() != null) user.setRole(req.getRole());
        if (req.getIsActive() != null) user.setIsActive(req.getIsActive());
        UserResponse response = toResponse(userRepository.save(user));
        log.info("User updated: id={}", id);
        return response;
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        userRepository.deleteById(id);
        log.info("User deleted: id={}", id);
    }

    private User findOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public UserResponse toResponse(User user) {
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

