package com.balanceeat.demo.domain.auth.controller;

import com.balanceeat.demo.domain.auth.dto.LoginRequest;
import com.balanceeat.demo.domain.auth.dto.RegisterRequest;
import com.balanceeat.demo.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(LoginRequest loginRequest, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            log.info("로그인 시도: {}", loginRequest.getUsername());
            authService.login(loginRequest, session);
            log.info("로그인 성공: {}", loginRequest.getUsername());
            return "redirect:/";
        } catch (Exception e) {
            log.error("로그인 실패: {} - {}", loginRequest.getUsername(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(RegisterRequest registerRequest, RedirectAttributes redirectAttributes) {
        try {
            log.info("회원가입 시도: {}", registerRequest.getUsername());
            authService.register(registerRequest);
            log.info("회원가입 성공: {}", registerRequest.getUsername());
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("회원가입 실패: {} - {}", registerRequest.getUsername(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String username = (String) ((Map<String, String>) session.getAttribute("user")).get("username");
        log.info("로그아웃: {}", username);
        session.invalidate();
        return "redirect:/";
    }
} 