package com.balanceeat.demo.domain.diet.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.balanceeat.demo.domain.diet.dto.DietUpdateRequestDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Diet {
    private Long id;
    private Long userId;
    private LocalDate dietDate;
    private String mealType; // BREAKFAST, LUNCH, DINNER
    private String foodName;
    private Double amount; // g 단위
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbohydrates;
    private String note; // 추가 메모
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateFrom(DietUpdateRequestDTO dto) {
        if (dto.getAmount() != null) this.amount = dto.getAmount();
        if (dto.getNote() != null) this.note = dto.getNote();
    }
}