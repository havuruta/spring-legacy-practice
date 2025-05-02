package com.balanceeat.demo.domain.diet.dto;

import com.balanceeat.demo.domain.diet.entity.Diet;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DietByMealDTO {
    private String mealType;
    private List<Diet> diets;
    private NutritionSummaryDTO summary;
}
