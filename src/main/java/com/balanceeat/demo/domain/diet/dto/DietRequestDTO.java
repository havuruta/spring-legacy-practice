package com.balanceeat.demo.domain.diet.dto;

import lombok.Data;

import java.time.LocalDate;

import com.balanceeat.demo.domain.diet.entity.Diet;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DietRequestDTO {
    private final LocalDate dietDate;
    private final String mealType;
    private final String foodName;
    private final Double amount;
    private final Double calories;
    private final Double protein;
    private final Double fat;
    private final Double carbohydrates;
    private final String note;

    public Diet toEntity(Long userId) {
        return Diet.builder()
                .userId(userId)
                .dietDate(dietDate)
                .mealType(mealType)
                .foodName(foodName)
                .amount(amount)
                .calories(calories)
                .protein(protein)
                .fat(fat)
                .carbohydrates(carbohydrates)
                .note(note)
                .build();
    }
}
