package com.balanceeat.demo.domain.auth.service.impl;

import com.balanceeat.demo.domain.auth.dto.LoginRequest;
import com.balanceeat.demo.domain.auth.dto.RegisterRequest;
import com.balanceeat.demo.domain.auth.service.AuthService;
import com.balanceeat.demo.domain.user.entity.User;
import com.balanceeat.demo.domain.user.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    @Override
    public void login(LoginRequest loginRequest, HttpSession session) {
        log.debug("로그인 처리 시작: {}", loginRequest.getUsername());
        User user = userMapper.findByUsername(loginRequest.getUsername());
        
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            log.warn("로그인 실패 - 잘못된 인증 정보: {}", loginRequest.getUsername());
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        
        if (!user.isActive()) {
            log.warn("로그인 실패 - 탈퇴한 회원: {}", loginRequest.getUsername());
            throw new RuntimeException("탈퇴한 회원입니다.");
        }

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("id", user.getId().toString());
        session.setAttribute("user", userInfo);
        log.debug("로그인 처리 완료: {}", user.getUsername());
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        log.debug("회원가입 처리 시작: {}", registerRequest.getUsername());
        if (userMapper.findByUsername(registerRequest.getUsername()) != null) {
            log.warn("회원가입 실패 - 이미 존재하는 아이디: {}", registerRequest.getUsername());
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirm())) {
            log.warn("회원가입 실패 - 비밀번호 불일치: {}", registerRequest.getPassword(), registerRequest.getPasswordConfirm());
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setActive(true);
        userMapper.insert(user);
        log.debug("회원가입 처리 완료: {}", user.getUsername());
    }
} 