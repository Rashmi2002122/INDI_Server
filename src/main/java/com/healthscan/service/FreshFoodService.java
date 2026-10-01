package com.healthscan.service;

import com.healthscan.dto.*;
import com.healthscan.entity.FoodNutritionEntity;
import com.healthscan.entity.FoodPreparationEntity;
import com.healthscan.entity.FreshFoodEntity;
import com.healthscan.repository.FreshFoodRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FreshFoodService {

    private final FreshFoodRepository freshFoodRepository;

    public FreshFoodService(FreshFoodRepository freshFoodRepository) {
        this.freshFoodRepository = freshFoodRepository;
    }

    public List<FreshFoodDto> search(String query) {
        if (query == null || query.isBlank()) return Collections.emptyList();
        String normalized = FoodNutritionService.normalizeQuery(query);
        return freshFoodRepository.searchByQuery(normalized)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<FreshFoodEntity> findEntity(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return Optional.empty();
        }

        String raw = identifier.trim();

        // 1. Try numeric ID
        if (raw.matches("\\d+")) {
            try {
                Long id = Long.parseLong(raw);
                Optional<FreshFoodEntity> byId = freshFoodRepository.findById(id);
                if (byId.isPresent()) return byId;
            } catch (NumberFormatException ignored) {}
        }

        // 2. Try slug match
        String slug = FoodNutritionService.toSlug(raw);
        Optional<FreshFoodEntity> bySlug = freshFoodRepository.findBySlug(slug);
        if (bySlug.isPresent()) return bySlug;

        // 3. Try name match
        Optional<FreshFoodEntity> byName = freshFoodRepository.findByNameIgnoreCase(raw);
        if (byName.isPresent()) return byName;

        // 4. Try normalized query search
        String normalized = FoodNutritionService.normalizeQuery(raw);
        List<FreshFoodEntity> matches = freshFoodRepository.searchByQuery(normalized);
        if (!matches.isEmpty()) {
            return Optional.of(matches.get(0));
        }

        return Optional.empty();
    }

    public Optional<FreshFoodDto> getByIdentifier(String identifier) {
        return findEntity(identifier).map(this::toDto);
    }

    public Optional<NutritionDto> getNutritionByIdentifier(String identifier) {
        return findEntity(identifier)
                .map(FreshFoodEntity::getNutrition)
                .map(this::nutritionToDto);
    }

    public Optional<FreshFoodDto> findByName(String name) {
        return getByIdentifier(name);
    }

    public Optional<FreshFoodEvaluationWrapperDto> evaluate(String identifier, String preparation, List<String> goals) {
        Optional<FreshFoodEntity> foodOpt = findEntity(identifier);
        if (foodOpt.isEmpty()) {
            return Optional.empty();
        }

        FreshFoodEntity foodEntity = foodOpt.get();
        FreshFoodDto foodDto = toDto(foodEntity);
        FoodNutritionEntity nut = foodEntity.getNutrition();

        double calorieMult = 1.0;
        double fatMult = 1.0;
        String prepAdvice = null;

        if (preparation != null && foodEntity.getPreparations() != null) {
            for (FoodPreparationEntity prep : foodEntity.getPreparations()) {
                if (prep.getName() != null && prep.getName().equalsIgnoreCase(preparation)) {
                    if (prep.getCalorieMultiplier() != null) calorieMult = prep.getCalorieMultiplier();
                    if (prep.getFatMultiplier() != null) fatMult = prep.getFatMultiplier();
                    prepAdvice = prep.getDescription();
                    break;
                }
            }
        }

        if (preparation != null && (preparation.equalsIgnoreCase("fried") || preparation.equalsIgnoreCase("deep_fried"))) {
            calorieMult = Math.max(calorieMult, 1.35);
            fatMult = Math.max(fatMult, 1.8);
            prepAdvice = "Frying adds extra fats and calories compared to boiling or steaming.";
        }

        Map<String, Object> adjustedNutrients = new HashMap<>(foodDto.getNutriments());
        if (nut != null) {
            double origCal = nut.getCalories() != null ? nut.getCalories() : 0.0;
            double origFat = nut.getTotalFat() != null ? nut.getTotalFat() : 0.0;
            adjustedNutrients.put("energyServing", Math.round(origCal * calorieMult));
            adjustedNutrients.put("fatServing", Math.round((origFat * fatMult) * 10.0) / 10.0);
        }

        List<GoalEvaluationItemDto> evalGoals = new ArrayList<>();
        List<String> activeGoals = goals != null ? goals : List.of();

        boolean hasWarning = false;
        boolean hasCaution = false;

        for (String goal : activeGoals) {
            GoalEvaluationItemDto item = evaluateSingleGoal(goal, foodEntity, nut, calorieMult);
            evalGoals.add(item);
            if ("WARNING".equals(item.getSeverity())) hasWarning = true;
            if ("CAUTION".equals(item.getSeverity())) hasCaution = true;
        }

        String overallStatus = "GOOD MATCH";
        if (hasWarning) {
            overallStatus = "NOT A GOOD MATCH";
        } else if (hasCaution) {
            overallStatus = "CAUTION";
        }

        EvaluationResultDto evalResult = new EvaluationResultDto();
        evalResult.setSuitable(!hasWarning);
        evalResult.setOverallStatus(overallStatus);
        evalResult.setPreparationAdvice(prepAdvice);
        evalResult.setGoals(evalGoals);
        evalResult.setAdjustedNutrients(adjustedNutrients);

        return Optional.of(new FreshFoodEvaluationWrapperDto(foodDto, evalResult));
    }

    private GoalEvaluationItemDto evaluateSingleGoal(String goal, FreshFoodEntity food, FoodNutritionEntity nut, double calorieMult) {
        String goalTitle = formatGoalTitle(goal);
        String severity = "GOOD";
        List<String> reasons = new ArrayList<>();

        double calories = nut != null && nut.getCalories() != null ? nut.getCalories() * calorieMult : 0.0;
        double protein = nut != null && nut.getProtein() != null ? nut.getProtein() : 0.0;
        double carbs = nut != null && nut.getCarbohydrates() != null ? nut.getCarbohydrates() : 0.0;
        double fat = nut != null && nut.getTotalFat() != null ? nut.getTotalFat() : 0.0;
        double sugar = nut != null && nut.getSugar() != null ? nut.getSugar() : 0.0;
        double sodium = nut != null && nut.getSodium() != null ? nut.getSodium() : 0.0;
        double fiber = nut != null && nut.getFiber() != null ? nut.getFiber() : 0.0;

        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS", "LOW_CALORIE" -> {
                if (calories <= 150) {
                    severity = "GOOD";
                    reasons.add("Low energy density with ~" + (int) calories + " kcal per serving.");
                } else if (calories <= 300) {
                    severity = "CAUTION";
                    reasons.add("Moderate energy content (~" + (int) calories + " kcal per serving). Monitor portion size.");
                } else {
                    severity = "WARNING";
                    reasons.add("High calorie density (~" + (int) calories + " kcal per serving).");
                }
            }
            case "HIGH_PROTEIN" -> {
                if (protein >= 10.0) {
                    severity = "GOOD";
                    reasons.add("Excellent protein source providing " + protein + " g per serving.");
                } else if (protein >= 5.0) {
                    severity = "GOOD";
                    reasons.add("Moderate protein source providing " + protein + " g per serving.");
                } else {
                    severity = "CAUTION";
                    reasons.add("Low in protein (" + protein + " g per serving).");
                }
            }
            case "LOW_SUGAR" -> {
                if (sugar <= 5.0) {
                    severity = "GOOD";
                    reasons.add("Low sugar content (" + sugar + " g per serving).");
                } else if (sugar <= 12.0) {
                    severity = "CAUTION";
                    reasons.add("Moderate natural sugars (" + sugar + " g per serving).");
                } else {
                    severity = "WARNING";
                    reasons.add("Higher sugar content (" + sugar + " g per serving).");
                }
            }
            case "LOW_CARB" -> {
                if (carbs <= 5.0) {
                    severity = "GOOD";
                    reasons.add("Very low carbs (" + carbs + " g per serving).");
                } else if (carbs <= 20.0) {
                    severity = "CAUTION";
                    reasons.add("Moderate carbohydrates (" + carbs + " g per serving).");
                } else {
                    severity = "WARNING";
                    reasons.add("High in carbohydrates (" + carbs + " g per serving).");
                }
            }
            case "LOW_FAT" -> {
                if (fat <= 3.0) {
                    severity = "GOOD";
                    reasons.add("Low fat content (" + fat + " g per serving).");
                } else if (fat <= 10.0) {
                    severity = "CAUTION";
                    reasons.add("Moderate fat content (" + fat + " g per serving).");
                } else {
                    severity = "WARNING";
                    reasons.add("Higher fat content (" + fat + " g per serving).");
                }
            }
            case "LOW_SODIUM" -> {
                if (sodium <= 140.0) {
                    severity = "GOOD";
                    reasons.add("Low sodium content (" + (int) sodium + " mg per serving).");
                } else {
                    severity = "WARNING";
                    reasons.add("Elevated sodium (" + (int) sodium + " mg per serving).");
                }
            }
            case "HIGH_FIBER" -> {
                if (fiber >= 3.0) {
                    severity = "GOOD";
                    reasons.add("Good fiber source (" + fiber + " g per serving).");
                } else {
                    severity = "CAUTION";
                    reasons.add("Contains minimal dietary fiber (" + fiber + " g).");
                }
            }
            case "VEGETARIAN" -> {
                if (food.isVegetarian()) {
                    severity = "GOOD";
                    reasons.add("Suitable for vegetarian diets.");
                } else {
                    severity = "WARNING";
                    reasons.add("Contains meat or poultry — not suitable for vegetarians.");
                }
            }
            case "VEGAN" -> {
                if (food.isVegan()) {
                    severity = "GOOD";
                    reasons.add("100% plant-based and vegan suitable.");
                } else {
                    severity = "WARNING";
                    reasons.add("Contains animal-derived ingredients (dairy, eggs, or meat).");
                }
            }
            default -> {
                severity = "GOOD";
                reasons.add("Fits within healthy dietary guidelines.");
            }
        }

        return new GoalEvaluationItemDto(goal, goalTitle, severity, reasons);
    }

    private String formatGoalTitle(String goal) {
        if (goal == null) return "Health Goal";
        return Arrays.stream(goal.toLowerCase().split("_"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(Collectors.joining(" "));
    }

    public List<FreshFoodDto> getAlternatives(String identifier) {
        Optional<FreshFoodEntity> current = findEntity(identifier);
        String category = current.map(FreshFoodEntity::getCategory).orElse("Protein");
        Long excludeId = current.map(FreshFoodEntity::getId).orElse(-1L);

        return freshFoodRepository.findByCategoryIgnoreCase(category)
                .stream()
                .filter(f -> !f.getId().equals(excludeId))
                .limit(4)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getRecipes(String identifier, List<String> goals) {
        Optional<FreshFoodEntity> foodOpt = findEntity(identifier);
        String name = foodOpt.map(FreshFoodEntity::getName).orElse("Food Item");

        List<Map<String, Object>> recipes = new ArrayList<>();
        Map<String, Object> r1 = new HashMap<>();
        r1.put("id", "r1");
        r1.put("name", "Healthy " + name + " Bowl");
        r1.put("calories", 320);
        r1.put("prepTime", "15 mins");
        r1.put("description", "A quick & nutritious meal combining fresh " + name + " with steamed vegetables and herbs.");
        r1.put("ingredients", List.of("1 serving " + name, "1 cup mixed greens", "1 tsp olive oil", "Salt & pepper to taste"));
        recipes.add(r1);

        Map<String, Object> r2 = new HashMap<>();
        r2.put("id", "r2");
        r2.put("name", "Steamed " + name + " Salad");
        r2.put("calories", 210);
        r2.put("prepTime", "10 mins");
        r2.put("description", "Lightly prepared " + name + " tossed with fresh cucumber, lemon juice, and seeds.");
        r2.put("ingredients", List.of("1 serving " + name, "1/2 cucumber sliced", "1 tbsp lemon juice", "1 tsp chia seeds"));
        recipes.add(r2);

        return recipes;
    }

    public FreshFoodDto toDto(FreshFoodEntity entity) {
        if (entity == null) return null;
        FreshFoodDto dto = new FreshFoodDto();
        dto.setId(entity.getId());
        dto.setSlug(entity.getSlug());
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        dto.setEmoji(entity.getEmoji() != null ? entity.getEmoji() : "🥬");
        dto.setVegetarian(entity.isVegetarian());
        dto.setVegan(entity.isVegan());
        dto.setGlutenFree(entity.isGlutenFree());
        dto.setLactoseFree(entity.isLactoseFree());

        FoodNutritionEntity nut = entity.getNutrition();
        if (nut != null) {
            dto.setServingSize(nut.getServingSize() != null ? nut.getServingSize() : (nut.getServingDescription() != null ? nut.getServingDescription() : "100g"));
            dto.setServingUnit(nut.getServingUnit() != null ? nut.getServingUnit() : "g");
            dto.setSource(nut.getSource() != null ? nut.getSource() : "USDA Reference Data");
            dto.setSourceUrl(nut.getSourceUrl());

            Map<String, Object> nutMap = new HashMap<>();
            nutMap.put("energyServing", nut.getCalories() != null ? nut.getCalories() : 0.0);
            nutMap.put("proteinServing", nut.getProtein() != null ? nut.getProtein() : 0.0);
            nutMap.put("carbohydratesServing", nut.getCarbohydrates() != null ? nut.getCarbohydrates() : 0.0);
            nutMap.put("fatServing", nut.getTotalFat() != null ? nut.getTotalFat() : 0.0);
            nutMap.put("saturatedFatServing", nut.getSaturatedFat() != null ? nut.getSaturatedFat() : 0.0);
            nutMap.put("transFatServing", nut.getTransFat() != null ? nut.getTransFat() : 0.0);
            nutMap.put("fiberServing", nut.getFiber() != null ? nut.getFiber() : 0.0);
            nutMap.put("sugarsServing", nut.getSugar() != null ? nut.getSugar() : 0.0);
            nutMap.put("sodiumServing", nut.getSodium() != null ? nut.getSodium() : 0.0);
            nutMap.put("cholesterolServing", nut.getCholesterol() != null ? nut.getCholesterol() : 0.0);
            nutMap.put("calciumServing", nut.getCalcium() != null ? nut.getCalcium() : 0.0);
            nutMap.put("ironServing", nut.getIron() != null ? nut.getIron() : 0.0);
            nutMap.put("potassiumServing", nut.getPotassium() != null ? nut.getPotassium() : 0.0);
            nutMap.put("vitaminCServing", nut.getVitaminC() != null ? nut.getVitaminC() : 0.0);
            dto.setNutriments(nutMap);
        } else {
            dto.setServingSize("100g");
            dto.setSource("USDA Reference Data");
        }

        if (entity.getPreparations() != null && !entity.getPreparations().isEmpty()) {
            List<PreparationDto> prepDtos = entity.getPreparations().stream()
                    .map(p -> new PreparationDto(
                            p.getName().toLowerCase(),
                            p.getName(),
                            p.getDescription(),
                            p.getCalorieMultiplier(),
                            p.getFatMultiplier()
                    )).collect(Collectors.toList());
            dto.setPreparations(prepDtos);
        } else {
            List<PreparationDto> defaultPreps = List.of(
                    new PreparationDto("raw", "Raw", "Uncooked, fresh state", 1.0, 1.0),
                    new PreparationDto("boiled", "Boiled", "Boiled in water", 1.0, 1.0),
                    new PreparationDto("fried", "Fried", "Pan fried with oil", 1.35, 1.8)
            );
            dto.setPreparations(defaultPreps);
        }

        return dto;
    }

    public NutritionDto nutritionToDto(FoodNutritionEntity nutrition) {
        if (nutrition == null) return null;
        return new NutritionDto(
                nutrition.getCalories(),
                nutrition.getProtein(),
                nutrition.getCarbohydrates(),
                nutrition.getTotalFat(),
                nutrition.getFiber(),
                nutrition.getSugar()
        );
    }
}
