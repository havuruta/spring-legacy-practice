package com.balanceeat.demo.domain.auth.service;

import com.balanceeat.demo.domain.auth.dto.LoginRequest;
import com.balanceeat.demo.domain.auth.dto.RegisterRequest;
import com.balanceeat.demo.domain.user.entity.User;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
    User login(LoginRequest loginRequest, HttpSession session);
    User register(RegisterRequest registerRequest);
    void signOut(String userId);
} 