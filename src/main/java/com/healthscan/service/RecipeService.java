package com.healthscan.service;

import com.healthscan.dto.RecipeDto;
import com.healthscan.entity.RecipeEntity;
import com.healthscan.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Transactional(readOnly = true)
    public List<RecipeDto> getAllRecipes() {
        return recipeRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<RecipeDto> getRecipesBySlot(String slot) {
        if (slot == null || slot.isBlank()) {
            return getAllRecipes();
        }
        return recipeRepository.findBySlotIgnoreCase(slot.trim()).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<RecipeDto> getRecommendedRecipes(String slot, String dietType, String goal, 
                                                List<String> allergies, List<String> eatenFoods, int limit, int offset) {
        List<RecipeEntity> candidates;
        if (slot != null && !slot.isBlank()) {
            candidates = recipeRepository.findBySlotIgnoreCase(slot.trim());
            if (candidates.isEmpty()) {
                candidates = recipeRepository.findAll();
            }
        } else {
            candidates = recipeRepository.findAll();
        }

        final String cleanDiet = dietType != null ? dietType.trim().toLowerCase() : "veg";
        final String cleanGoal = goal != null ? goal.trim().toLowerCase() : "fat_loss";
        final List<String> cleanAllergies = allergies != null 
                ? allergies.stream().map(a -> a.trim().toLowerCase()).filter(s -> !s.isEmpty()).toList() 
                : Collections.emptyList();
        final List<String> cleanEaten = eatenFoods != null 
                ? eatenFoods.stream().map(e -> e.trim().toLowerCase()).filter(s -> !s.isEmpty()).toList() 
                : Collections.emptyList();

        // 1. Strict Diet Type Filtering
        List<RecipeEntity> filtered = candidates.stream().filter(recipe -> {
            String rDiet = recipe.getDiet() != null ? recipe.getDiet().toLowerCase() : "";
            if ("veg".equals(cleanDiet)) {
                return rDiet.contains("veg") && !rDiet.contains("non-veg");
            }
            if ("vegan".equals(cleanDiet)) {
                return rDiet.contains("vegan");
            }
            if ("eggetarian".equals(cleanDiet)) {
                return rDiet.contains("veg") || rDiet.contains("eggetarian");
            }
            return true; // non-veg can eat any
        }).toList();

        // 2. Strict Allergies / Dislikes Filtering
        if (!cleanAllergies.isEmpty()) {
            filtered = filtered.stream().filter(recipe -> {
                String rAllergens = recipe.getAllergens() != null ? recipe.getAllergens().toLowerCase() : "";
                for (String allergy : cleanAllergies) {
                    if (!allergy.isEmpty() && rAllergens.contains(allergy)) {
                        return false;
                    }
                }
                String rIngredients = recipe.getIngredients() != null ? recipe.getIngredients().toLowerCase() : "";
                for (String allergy : cleanAllergies) {
                    if (allergy.length() > 2 && rIngredients.contains(allergy)) {
                        return false;
                    }
                }
                return true;
            }).toList();
        }

        // 3. Avoid repeating foods eaten earlier today
        if (!cleanEaten.isEmpty()) {
            filtered = filtered.stream().filter(recipe -> {
                String rName = recipe.getName() != null ? recipe.getName().toLowerCase() : "";
                for (String eaten : cleanEaten) {
                    if (eaten.length() > 2 && rName.contains(eaten)) {
                        return false;
                    }
                }
                return true;
            }).toList();
        }

        // 4. Score matching goal
        List<RecipeEntity> mutableList = new ArrayList<>(filtered);
        mutableList.sort((a, b) -> {
            int scoreA = (a.getGoals() != null && a.getGoals().toLowerCase().contains(cleanGoal)) ? 10 : 0;
            int scoreB = (b.getGoals() != null && b.getGoals().toLowerCase().contains(cleanGoal)) ? 10 : 0;
            return Integer.compare(scoreB, scoreA);
        });

        int total = mutableList.size();
        if (total == 0) return Collections.emptyList();
        int safeLimit = limit > 0 ? Math.min(limit, total) : total;
        int safeOffset = Math.max(0, offset);
        int startIndex = (safeOffset * safeLimit) % total;

        List<RecipeEntity> result = new ArrayList<>();
        for (int i = 0; i < safeLimit; i++) {
            result.add(mutableList.get((startIndex + i) % total));
        }
        return result.stream().map(this::toDto).toList();
    }

    public RecipeEntity saveRecipe(RecipeEntity recipe) {
        return recipeRepository.save(recipe);
    }

    public RecipeDto toDto(RecipeEntity entity) {
        if (entity == null) return null;

        Map<String, Object> nutrition = new LinkedHashMap<>();
        nutrition.put("calories", entity.getCalories() != null ? entity.getCalories().intValue() : 0);
        nutrition.put("protein", entity.getProtein() != null ? entity.getProtein().intValue() : 0);
        nutrition.put("carbs", entity.getCarbohydrates() != null ? entity.getCarbohydrates().intValue() : 0);
        nutrition.put("fat", entity.getFat() != null ? entity.getFat().intValue() : 0);

        return new RecipeDto(
                entity.getId(),
                entity.getName(),
                entity.getSlot(),
                RecipeDto.splitList(entity.getDiet()),
                RecipeDto.splitList(entity.getGoals()),
                RecipeDto.splitList(entity.getAllergens()),
                entity.getWhyItFits(),
                RecipeDto.splitLines(entity.getIngredients()),
                RecipeDto.splitLines(entity.getMethod()),
                nutrition,
                entity.getTimeToMake()
        );
    }
}
