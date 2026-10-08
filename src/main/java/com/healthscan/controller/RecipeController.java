package com.healthscan.controller;

import com.healthscan.dto.RecipeDto;
import com.healthscan.entity.RecipeEntity;
import com.healthscan.service.RecipeService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeDto>> getAllRecipes() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(15, TimeUnit.MINUTES).cachePublic())
                .body(recipeService.getAllRecipes());
    }

    @GetMapping("/slot/{slot}")
    public ResponseEntity<List<RecipeDto>> getRecipesBySlot(@PathVariable("slot") String slot) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES).cachePublic())
                .body(recipeService.getRecipesBySlot(slot));
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<RecipeDto>> getRecommendedRecipes(
            @RequestParam(value = "slot", required = false) String slot,
            @RequestParam(value = "dietType", required = false, defaultValue = "veg") String dietType,
            @RequestParam(value = "goal", required = false, defaultValue = "fat_loss") String goal,
            @RequestParam(value = "allergies", required = false) List<String> allergies,
            @RequestParam(value = "eatenFoods", required = false) List<String> eatenFoods,
            @RequestParam(value = "limit", required = false, defaultValue = "3") int limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES).cachePublic())
                .body(recipeService.getRecommendedRecipes(slot, dietType, goal, allergies, eatenFoods, limit, offset));
    }

    @PostMapping
    public ResponseEntity<RecipeEntity> createRecipe(@RequestBody RecipeEntity recipe) {
        if (recipe.getId() == null || recipe.getId().isBlank()) {
            recipe.setId("rec-" + System.currentTimeMillis());
        }
        return ResponseEntity.ok(recipeService.saveRecipe(recipe));
    }
}
