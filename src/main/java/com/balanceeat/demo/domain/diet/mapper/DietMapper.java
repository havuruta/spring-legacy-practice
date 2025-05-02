package com.balanceeat.demo.domain.diet.mapper;

import java.time.LocalDate;
import java.util.List;

import com.balanceeat.demo.domain.diet.dto.DietByDateDTO;
import org.apache.ibatis.annotations.Mapper;

import com.balanceeat.demo.domain.diet.entity.Diet;
import com.balanceeat.demo.domain.diet.entity.DietSummary;

@Mapper
public interface DietMapper {
    List<Diet> findDietsByDateRange(Long userId, LocalDate start, LocalDate end);
    void insertDiet(Diet diet);
    void updateDiet(Diet diet);
    void deleteDiet(Long id);
    List<Diet> findDietsByDate(Long userId, LocalDate date);
} 