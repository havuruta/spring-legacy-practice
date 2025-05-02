package com.balanceeat.demo.domain.diet.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.balanceeat.demo.domain.diet.dto.DietByDateDTO;
import com.balanceeat.demo.domain.diet.dto.DietByMealDTO;
import com.balanceeat.demo.domain.diet.dto.NutritionSummaryDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.balanceeat.demo.domain.diet.entity.Diet;
import com.balanceeat.demo.domain.diet.entity.DietSummary;
import com.balanceeat.demo.domain.diet.mapper.DietMapper;
import com.balanceeat.demo.domain.diet.service.DietService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DietServiceImpl implements DietService {

    private final DietMapper dietMapper;

    @Override
    public List<DietByDateDTO> getDietSummariesByDateRange(Long userId, LocalDate start, LocalDate end) {
        List<Diet> diets = dietMapper.findDietsByDateRange(userId, start, end);

        // 1단계: 날짜별 그룹핑
        return diets.stream()
                .collect(Collectors.groupingBy(Diet::getDietDate, TreeMap::new, Collectors.toList()))
                .entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Diet> dayDiets = entry.getValue();

                    // 2단계: 식사 유형별 그룹핑
                    Map<String, List<Diet>> mealMap = dayDiets.stream()
                            .collect(Collectors.groupingBy(Diet::getMealType));

                    List<DietByMealDTO> meals = mealMap.entrySet().stream()
                            .map(mealEntry -> {
                                List<Diet> mealDiets = mealEntry.getValue();
                                return DietByMealDTO.builder()
                                        .mealType(mealEntry.getKey())
                                        .diets(mealDiets)
                                        .summary(NutritionSummaryDTO.builder()
                                                .calories(roundSum(mealDiets, Diet::getCalories))
                                                .protein(roundSum(mealDiets, Diet::getProtein))
                                                .fat(roundSum(mealDiets, Diet::getFat))
                                                .carbohydrates(roundSum(mealDiets, Diet::getCarbohydrates))
                                                .build())
                                        .build();
                            }).toList();

                    return DietByDateDTO.builder()
                            .date(date)
                            .meals(meals)
                            .build();
                }).toList();
    }

    private int roundSum(List<Diet> diets, Function<Diet, Double> getter) {
        if (diets == null || diets.isEmpty()) return 0;
        return (int) Math.round(diets.stream()
                .filter(Objects::nonNull)
                .map(getter)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum());
    }


    @Override
    public void addDiet(Diet diet) {
        log.info("식단 삽입 서비스 호출: {}", diet);
        dietMapper.insertDiet(diet);
        log.info("식단 삽입 완료");
    }

    @Override
    public void updateDiet(Diet diet) {
        log.info("식단 수정 서비스 호출: {}", diet);
        dietMapper.updateDiet(diet);
        log.info("식단 수정 완료");
    }

    @Override
    public void deleteDiet(Long id) {
        dietMapper.deleteDiet(id);
    }

    @Override
    public List<Diet> getDietsByDate(Long userId, LocalDate date) {
        return dietMapper.findDietsByDate(userId, date);
    }
} 