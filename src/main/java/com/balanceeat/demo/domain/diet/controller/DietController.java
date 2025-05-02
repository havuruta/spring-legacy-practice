package com.balanceeat.demo.domain.diet.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import com.balanceeat.demo.domain.diet.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.balanceeat.demo.domain.diet.entity.Diet;
import com.balanceeat.demo.domain.diet.entity.DietSummary;
import com.balanceeat.demo.domain.diet.service.DietService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/diet")
@RequiredArgsConstructor
@Tag(name = "식단 API", description = "식단 관리 API")
public class DietController {

    private final DietService dietService;

    private NutritionSummaryDTO buildSummary(List<Diet> diets) {
        return NutritionSummaryDTO.builder()
                .calories(roundSum(diets, Diet::getCalories))
                .protein(roundSum(diets, Diet::getProtein))
                .fat(roundSum(diets, Diet::getFat))
                .carbohydrates(roundSum(diets, Diet::getCarbohydrates))
                .build();
    }

    private int roundSum(List<Diet> diets, ToDoubleFunction<Diet> getter) {
        if (diets == null || diets.isEmpty()) {
            return 0;
        }

        return (int) Math.round(diets.stream()
                .mapToDouble(diet -> {
                    if (diet == null) return 0.0;
                    double value = getter.applyAsDouble(diet);
                    return Double.isNaN(value) ? 0.0 : value;
                })
                .sum());
    }

    @GetMapping()
    @ResponseBody
    @Operation(summary = "식단 목록 조회", description = "지정된 기간 동안의 식단 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "식단 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = DietByDateDTO.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<DietByDateDTO>> getDietSummaries(
        @Parameter(description = "조회 시작일 (yyyy-MM-dd)", required = true)
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
        @Parameter(description = "조회 종료일 (yyyy-MM-dd)", required = true)
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
        HttpSession session) {

        Map<String, String> userInfo = (Map<String, String>) session.getAttribute("user");
        if (userInfo == null || userInfo.get("id") == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.parseLong(userInfo.get("id"));
        List<DietByDateDTO> response = dietService.getDietSummariesByDateRange(userId, start, end);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    @ResponseBody
    @Operation(summary = "식단 추가", description = "새로운 식단을 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "식단 추가 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> addDiet(
        @Parameter(description = "추가할 식단 정보", required = true)
        @RequestBody DietRequestDTO dto,
        HttpSession session) {
        Map<String, String> userInfo = (Map<String, String>) session.getAttribute("user");
        if (userInfo == null || userInfo.get("id") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Long userId = Long.parseLong(userInfo.get("id"));
        dietService.addDiet(dto.toEntity(userId));
        return ResponseEntity.ok("식단이 추가되었습니다.");
    }

    @PutMapping("/{id}")
    @ResponseBody
    @Operation(summary = "식단 수정", description = "기존 식단을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "식단 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> updateDiet(
        @Parameter(description = "식단 ID", required = true)
        @PathVariable Long id,
        @Parameter(description = "수정할 식단 정보", required = true)
        @RequestBody DietUpdateRequestDTO dto,
        HttpSession session) {
        try {
            Map<String, String> userInfo = (Map<String, String>) session.getAttribute("user");
            if (userInfo == null || userInfo.get("id") == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            Diet diet = Diet.builder().id(id).build();
            diet.updateFrom(dto);

            dietService.updateDiet(diet);
            return ResponseEntity.ok("식단이 수정되었습니다.");
        } catch (Exception e) {
            log.error("식단 수정 중 오류 발생", e);
            return ResponseEntity.badRequest().body("식단 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @Operation(summary = "식단 삭제", description = "기존 식단을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "식단 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<String> deleteDiet(
        @Parameter(description = "식단 ID", required = true)
        @PathVariable Long id,
        HttpSession session) {
        try {
            Map<String, String> userInfo = (Map<String, String>) session.getAttribute("user");
            if (userInfo == null || userInfo.get("id") == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }
            
            dietService.deleteDiet(id);
            return ResponseEntity.ok().body("식단이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("식단 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}