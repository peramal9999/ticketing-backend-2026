package com.peramal.ticketingsys.service;

import com.peramal.ticketingsys.dto.request.LoginRequest;
import com.peramal.ticketingsys.dto.request.RefreshTokenRequest;
import com.peramal.ticketingsys.dto.request.RegisterRequest;
import com.peramal.ticketingsys.dto.response.AuthResponse;
import com.peramal.ticketingsys.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
    UserResponse getMe();
}
