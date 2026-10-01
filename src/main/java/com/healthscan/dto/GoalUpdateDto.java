package com.healthscan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for updating user goals and health preferences.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalUpdateDto {
    private List<String> goals; // e.g., ["WEIGHT_LOSS", "HIGH_PROTEIN"]
    private List<String> healthPreferences; // e.g., ["DIABETES_CONSCIOUS"]
}
