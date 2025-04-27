package com.balanceeat.demo.domain.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String passwordConfirm;
} 