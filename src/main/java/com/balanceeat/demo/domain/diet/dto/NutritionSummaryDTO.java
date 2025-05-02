package com.balanceeat.demo.domain.diet.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NutritionSummaryDTO {
    private int calories;
    private int protein;
    private int fat;
    private int carbohydrates;
}
