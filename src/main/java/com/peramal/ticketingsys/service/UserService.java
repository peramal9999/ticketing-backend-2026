package com.peramal.ticketingsys.service;

import com.peramal.ticketingsys.dto.request.CreateUserRequest;
import com.peramal.ticketingsys.dto.request.UpdateUserRequest;
import com.peramal.ticketingsys.dto.response.UserResponse;
import com.peramal.ticketingsys.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserResponse> getAll();
    UserResponse getById(UUID id);
    UserResponse create(CreateUserRequest request);
    UserResponse update(UUID id, UpdateUserRequest request);
    void delete(UUID id);
    UserResponse toResponse(User user);
}
