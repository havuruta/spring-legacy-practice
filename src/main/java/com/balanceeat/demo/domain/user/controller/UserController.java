package com.balanceeat.demo.domain.user.controller;

import com.balanceeat.demo.domain.user.dto.UserDTO;
import com.balanceeat.demo.domain.user.entity.User;
import com.balanceeat.demo.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 정보 관리 API")
public class UserController {
    
    private final UserService userService;
    
    // 마이페이지 조회
    @GetMapping("/info")
    @Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<UserDTO> mypage(
        @Parameter(description = "세션에 저장된 사용자 정보", required = true)
        @SessionAttribute("user") Map<String, String> userInfo) {
        String userId = userInfo.get("id");
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // 사용자 정보 수정 처리
    @PutMapping("/info")
    @Operation(summary = "사용자 정보 수정", description = "사용자의 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공",
            content = @Content(schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - 이미 존재하는 아이디"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> updateUser(
        @Parameter(description = "수정할 사용자 정보", required = true)
        @RequestBody UserDTO userDTO) {
        try {
            userService.updateUser(userDTO);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

