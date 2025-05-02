package com.balanceeat.demo.domain.auth.controller;

import com.balanceeat.demo.domain.auth.dto.LoginRequest;
import com.balanceeat.demo.domain.auth.dto.RegisterRequest;
import com.balanceeat.demo.domain.auth.service.AuthService;
import com.balanceeat.demo.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공", 
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "401", description = "로그인 실패 - 잘못된 인증 정보"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<User> login(
        @Parameter(description = "로그인 요청 정보", required = true)
        @RequestBody LoginRequest loginRequest,
        HttpSession session,
        RedirectAttributes redirectAttributes) {
        try {
            log.info("로그인 시도: {}", loginRequest.getUsername());
            User user = authService.login(loginRequest, session);
            log.info("로그인 성공: {}", loginRequest.getUsername());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("로그인 실패: {} - {}", loginRequest.getUsername(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - 이미 존재하는 아이디 또는 비밀번호 불일치"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<String> register(
        @Parameter(description = "회원가입 요청 정보", required = true)
        @RequestBody RegisterRequest registerRequest,
        RedirectAttributes redirectAttributes) {
        try {
            log.info("회원가입 시도: {}", registerRequest.getUsername());
            User registeredUser = authService.register(registerRequest);
            log.info("회원가입 성공: {}", registerRequest.getUsername());
            return ResponseEntity.ok(registeredUser.getUsername());
        } catch (Exception e) {
            log.error("회원가입 실패: {} - {}", registerRequest.getUsername(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<String> logout(HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (!(userObj instanceof Map)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = ((Map<String, String>) userObj).get("username");
        log.info("로그아웃: {}", username);
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> deleteUser(
        @Parameter(description = "세션에 저장된 사용자 정보", required = true)
        @SessionAttribute("user") Map<String, String> userInfo) {
        String userId = userInfo.get("id");
        authService.signOut(userId);
        return ResponseEntity.ok().build();
    }
} 