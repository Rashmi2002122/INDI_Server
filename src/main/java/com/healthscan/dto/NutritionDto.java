package com.healthscan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for nutrition information of a fresh food.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionDto {
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private Double fiber;
    private Double sugar;
    // add other nutrients as needed
}
