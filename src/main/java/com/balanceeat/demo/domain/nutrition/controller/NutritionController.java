package com.balanceeat.demo.domain.nutrition.controller;

import com.balanceeat.demo.domain.nutrition.entity.Nutrition;
import com.balanceeat.demo.domain.nutrition.service.NutritionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/nutrition")
@Tag(name = "영양 정보 API", description = "영양 정보 관리 API")
@RequiredArgsConstructor
public class NutritionController {

    private static final Logger logger = LoggerFactory.getLogger(NutritionController.class);
    private final NutritionService nutritionService;

    @GetMapping()
    @Operation(summary = "영양 정보 목록 조회", description = "모든 영양 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "영양 정보 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = Nutrition.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<Nutrition>> listNutrition(Model model) {
        logger.info("영양 정보 목록 페이지 요청");
        List<Nutrition> nutrition = nutritionService.getAllNutritions();
        logger.info("목록 조회 - 전체 항목 수: {}", nutrition.size());
        model.addAttribute("nutritions", nutrition);
        return ResponseEntity.ok().body(nutrition);
    }

    @GetMapping("/{id}")
    @Operation(summary = "영양 정보 상세 조회", description = "특정 영양 정보의 상세 내용을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "영양 정보 상세 조회 성공",
            content = @Content(schema = @Schema(implementation = Nutrition.class))),
        @ApiResponse(responseCode = "404", description = "해당 ID의 영양 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Nutrition> getNutritionDetail(
        @Parameter(description = "영양 정보 ID", required = true)
        @PathVariable Long id,
        Model model) {
        logger.info("영양 정보 상세 페이지 요청: ID={}", id);
        Nutrition nutrition = nutritionService.getNutritionById(id);
        if (nutrition == null) {
            return ResponseEntity.notFound().build();
        }
        model.addAttribute("nutrition", nutrition);
        return ResponseEntity.ok().body(nutrition);
    }

    @GetMapping("/search")
    @Operation(summary = "영양 정보 검색", description = "설명이나 이름으로 영양 정보를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "영양 정보 검색 성공",
            content = @Content(schema = @Schema(implementation = Nutrition.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<Nutrition>> searchNutritions(
        @Parameter(description = "검색할 설명", required = false)
        @RequestParam(required = false) String description,
        @Parameter(description = "검색할 이름", required = false)
        @RequestParam(required = false) String name,
        Model model,
        HttpServletRequest request) {
        logger.info("검색어: {}", name != null ? name : description);
        List<Nutrition> nutritions;
        
        String decodedName = name != null ? java.net.URLDecoder.decode(name, java.nio.charset.StandardCharsets.UTF_8) : null;
        String decodedDescription = description != null ? java.net.URLDecoder.decode(description, java.nio.charset.StandardCharsets.UTF_8) : null;
        
        if (decodedDescription != null && !decodedDescription.trim().isEmpty()) {
            nutritions = nutritionService.searchByDescription(decodedDescription);
            logger.info("검색 결과 수: {}", nutritions.size());
        } else if (decodedName != null && !decodedName.trim().isEmpty()) {
            nutritions = nutritionService.searchByName(decodedName);
            logger.info("검색 결과 수: {}", nutritions.size());
        } else {
            nutritions = nutritionService.getAllNutritions();
            logger.info("검색어가 없어 전체 목록 조회. 결과 수: {}", nutritions.size());
        }

        model.addAttribute("nutritions", nutritions);
        logger.info("모델에 추가된 데이터 - nutritions 크기: {}", nutritions.size());
        
        return ResponseEntity.ok().body(nutritions);
    }
} 