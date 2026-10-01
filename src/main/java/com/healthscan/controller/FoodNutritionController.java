package com.healthscan.controller;

import com.healthscan.dto.FoodNutritionDto;
import com.healthscan.service.FoodNutritionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodNutritionController {

    private final FoodNutritionService foodNutritionService;

    public FoodNutritionController(FoodNutritionService foodNutritionService) {
        this.foodNutritionService = foodNutritionService;
    }

    @GetMapping
    public ResponseEntity<List<FoodNutritionDto>> getAllFoods() {
        return ResponseEntity.ok(foodNutritionService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodNutritionDto>> searchFoods(@RequestParam("q") String query) {
        return ResponseEntity.ok(foodNutritionService.search(query));
    }

    @GetMapping("/{foodName}")
    public ResponseEntity<FoodNutritionDto> getFoodByName(@PathVariable("foodName") String foodName) {
        return foodNutritionService.findNutrition(foodName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
