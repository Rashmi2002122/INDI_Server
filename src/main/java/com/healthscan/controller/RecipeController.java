package com.healthscan.controller;

import com.healthscan.dto.RecipeDto;
import com.healthscan.entity.RecipeEntity;
import com.healthscan.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeDto>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/slot/{slot}")
    public ResponseEntity<List<RecipeDto>> getRecipesBySlot(@PathVariable("slot") String slot) {
        return ResponseEntity.ok(recipeService.getRecipesBySlot(slot));
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<RecipeDto>> getRecommendedRecipes(
            @RequestParam(value = "slot", required = false) String slot,
            @RequestParam(value = "dietType", required = false, defaultValue = "veg") String dietType,
            @RequestParam(value = "goal", required = false, defaultValue = "fat_loss") String goal,
            @RequestParam(value = "allergies", required = false) List<String> allergies,
            @RequestParam(value = "eatenFoods", required = false) List<String> eatenFoods,
            @RequestParam(value = "limit", required = false, defaultValue = "3") int limit
    ) {
        return ResponseEntity.ok(recipeService.getRecommendedRecipes(slot, dietType, goal, allergies, eatenFoods, limit));
    }

    @PostMapping
    public ResponseEntity<RecipeEntity> createRecipe(@RequestBody RecipeEntity recipe) {
        if (recipe.getId() == null || recipe.getId().isBlank()) {
            recipe.setId("rec-" + System.currentTimeMillis());
        }
        return ResponseEntity.ok(recipeService.saveRecipe(recipe));
    }
}
