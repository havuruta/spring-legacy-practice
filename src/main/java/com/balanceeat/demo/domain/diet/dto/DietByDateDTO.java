package com.balanceeat.demo.domain.diet.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DietByDateDTO {
    private LocalDate date;
    private List<DietByMealDTO> meals;
}
