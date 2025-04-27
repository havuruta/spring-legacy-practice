package com.balanceeat.demo.domain.auth.service;

import com.balanceeat.demo.domain.auth.dto.LoginRequest;
import com.balanceeat.demo.domain.auth.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
    void login(LoginRequest loginRequest, HttpSession session);
    void register(RegisterRequest registerRequest);
} 