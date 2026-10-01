package com.healthscan.controller;

import com.healthscan.dto.*;
import com.healthscan.service.FreshFoodService;
import com.healthscan.service.OpenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/fresh-food")
public class FreshFoodController {

    private final FreshFoodService freshFoodService;
    private final OpenAIService openAIService;

    public FreshFoodController(FreshFoodService freshFoodService, OpenAIService openAIService) {
        this.freshFoodService = freshFoodService;
        this.openAIService = openAIService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("query") String query) {
        List<FreshFoodDto> dbResults = freshFoodService.search(query);
        if (!dbResults.isEmpty()) {
            return ResponseEntity.ok(dbResults);
        }

        // No results in DB — lookup via OpenAI or fallback engine
        FreshFoodDto aiResult = openAIService.getNutritionInfo(query);
        if (aiResult != null) {
            return ResponseEntity.ok(List.of(aiResult));
        }

        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<?> getByIdentifier(@PathVariable("identifier") String identifier) {
        Optional<FreshFoodDto> dbResult = freshFoodService.getByIdentifier(identifier);
        if (dbResult.isPresent()) {
            return ResponseEntity.ok(dbResult.get());
        }

        // Not in DB — lookup via OpenAI or fallback engine
        FreshFoodDto aiResult = openAIService.getNutritionInfo(identifier);
        if (aiResult != null) {
            return ResponseEntity.ok(aiResult);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{identifier}/nutrition")
    public ResponseEntity<NutritionDto> getNutrition(@PathVariable("identifier") String identifier) {
        return freshFoodService.getNutritionByIdentifier(identifier)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Evaluate a food item against the user's health goals.
     * If the food is not in the local database, falls back to OpenAI or fallback engine to get nutrition data,
     * then performs the evaluation using that data.
     */
    @PostMapping("/{identifier}/evaluate")
    public ResponseEntity<?> evaluate(
            @PathVariable("identifier") String identifier,
            @RequestBody(required = false) Map<String, Object> body) {

        String preparation = "raw";
        List<String> goals = List.of();

        if (body != null) {
            if (body.get("preparation") instanceof String prep) {
                preparation = prep;
            }
            if (body.get("goals") instanceof List<?> gList) {
                goals = gList.stream().map(Object::toString).toList();
            }
        }

        // Try local database first
        Optional<FreshFoodEvaluationWrapperDto> dbResult = freshFoodService.evaluate(identifier, preparation, goals);
        if (dbResult.isPresent()) {
            return ResponseEntity.ok(dbResult.get());
        }

        // Not in DB — lookup via OpenAI or fallback engine for nutrition data and build evaluation
        FreshFoodDto aiFood = openAIService.getNutritionInfo(identifier);
        if (aiFood != null) {
            // Build evaluation from the AI-generated nutrition data
            EvaluationResultDto evalResult = buildEvaluationFromDto(aiFood, preparation, goals);
            FreshFoodEvaluationWrapperDto wrapper = new FreshFoodEvaluationWrapperDto(aiFood, evalResult);
            return ResponseEntity.ok(wrapper);
        }

        return ResponseEntity.status(404).body(Map.of(
                "error", "Food not found",
                "identifier", identifier,
                "message", "'" + identifier + "' was not found in the database and AI lookup is unavailable."
        ));
    }

    @PostMapping("/{identifier}/recipes")
    public ResponseEntity<Map<String, Object>> getRecipes(
            @PathVariable("identifier") String identifier,
            @RequestBody(required = false) Map<String, Object> body) {

        List<String> goals = List.of();
        if (body != null && body.get("goals") instanceof List<?> gList) {
            goals = gList.stream().map(Object::toString).toList();
        }

        List<Map<String, Object>> recipes = freshFoodService.getRecipes(identifier, goals);
        return ResponseEntity.ok(Map.of("recipes", recipes));
    }

    @GetMapping("/{identifier}/alternatives")
    public ResponseEntity<Map<String, Object>> getAlternatives(@PathVariable("identifier") String identifier) {
        List<FreshFoodDto> alternatives = freshFoodService.getAlternatives(identifier);
        return ResponseEntity.ok(Map.of("alternatives", alternatives));
    }

    @PostMapping("/scans")
    public ResponseEntity<Map<String, Object>> saveScan(@RequestBody(required = false) Map<String, Object> payload) {
        return ResponseEntity.ok(Map.of("status", "success", "message", "Scan snapshot recorded"));
    }

    /**
     * Recognize food from an image (base64) or a text query hint.
     * Accepts JSON body: { "imageBase64": "...", "queryHint": "..." }
     * Falls back to OpenAI if food is not in the local database.
     */
    @PostMapping("/recognize")
    public ResponseEntity<?> recognize(@RequestBody(required = false) Map<String, Object> body) {
        String queryHint = null;
        String imageBase64 = null;

        if (body != null) {
            if (body.get("queryHint") instanceof String hint) {
                queryHint = hint;
            }
            if (body.get("imageBase64") instanceof String img) {
                imageBase64 = img;
            }
        }

        // Filter out generic image filenames like "media_1790610365620", "IMG_20260928", "photo_1"
        if (queryHint != null) {
            String lowerHint = queryHint.trim().toLowerCase();
            if (lowerHint.matches("^(img|image|media|dsc|photo|pic|_|\\d)+.*")) {
                queryHint = null;
            }
        }

        // Priority 1: Use queryHint to search the database if it is a clean food name
        if (queryHint != null && !queryHint.isBlank()) {
            final String hint = queryHint;

            // Check local DB first
            Optional<FreshFoodDto> dbMatch = freshFoodService.getByIdentifier(hint);
            if (dbMatch.isPresent()) {
                FreshFoodDto dto = dbMatch.get();
                Map<String, Object> result = new HashMap<>();
                result.put("foodId", dto.getSlug() != null ? dto.getSlug() : dto.getId().toString());
                result.put("name", dto.getName());
                result.put("emoji", dto.getEmoji());
                result.put("confidence", 0.95);
                result.put("matched", true);
                return ResponseEntity.ok(result);
            }

            // Try fuzzy search in DB
            List<FreshFoodDto> matches = freshFoodService.search(hint);
            if (!matches.isEmpty()) {
                FreshFoodDto top = matches.get(0);
                Map<String, Object> result = new HashMap<>();
                result.put("foodId", top.getSlug() != null ? top.getSlug() : top.getId().toString());
                result.put("name", top.getName());
                result.put("emoji", top.getEmoji());
                result.put("confidence", 0.75);
                result.put("matched", true);
                if (matches.size() > 1) {
                    result.put("possibleMatches", matches.stream().limit(5).map(m -> Map.of(
                            "id", m.getSlug() != null ? m.getSlug() : m.getId().toString(),
                            "name", m.getName(),
                            "emoji", m.getEmoji() != null ? m.getEmoji() : "🥬"
                    )).toList());
                }
                return ResponseEntity.ok(result);
            }

            // Not in DB — lookup via OpenAI or fallback engine
            FreshFoodDto aiResult = openAIService.getNutritionInfo(hint);
            if (aiResult != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("foodId", hint.toLowerCase().replaceAll("\\s+", "-"));
                result.put("name", aiResult.getName());
                result.put("emoji", aiResult.getEmoji());
                result.put("confidence", 0.85);
                result.put("matched", true);
                result.put("source", "Nutritional Engine");
                return ResponseEntity.ok(result);
            }

            return ResponseEntity.ok(Map.of("matched", false, "confidence", 0.0, "message", "No matching food found for: " + hint));
        }

        // Priority 2: Use imageBase64 with AI recognition
        if (imageBase64 != null && !imageBase64.isBlank()) {
            String foodName = openAIService.recognizeFood(imageBase64);
            if (!"unknown".equals(foodName)) {
                return freshFoodService.getByIdentifier(foodName)
                        .map(dto -> {
                            Map<String, Object> result = new HashMap<>();
                            result.put("foodId", dto.getSlug() != null ? dto.getSlug() : dto.getId().toString());
                            result.put("name", dto.getName());
                            result.put("emoji", dto.getEmoji());
                            result.put("confidence", 0.80);
                            result.put("matched", true);
                            return ResponseEntity.ok((Object) result);
                        })
                        .orElseGet(() -> ResponseEntity.ok((Object) Map.of("matched", false, "confidence", 0.0, "message", "AI recognized '" + foodName + "' but it's not in the database.")));
            }
            return ResponseEntity.ok(Map.of("matched", false, "confidence", 0.0, "message", "Could not recognize food from image."));
        }

        return ResponseEntity.badRequest().body(Map.of("error", "Request must include 'imageBase64' or 'queryHint'."));
    }

    // --- Helper: Build evaluation from an AI-generated FreshFoodDto ---

    private EvaluationResultDto buildEvaluationFromDto(FreshFoodDto food, String preparation, List<String> goals) {
        Map<String, Object> nutriments = food.getNutriments() != null ? food.getNutriments() : Map.of();

        double calories = toDouble(nutriments.get("energyServing"));
        double protein = toDouble(nutriments.get("proteinServing"));
        double carbs = toDouble(nutriments.get("carbohydratesServing"));
        double fat = toDouble(nutriments.get("fatServing"));
        double sugar = toDouble(nutriments.get("sugarsServing"));
        double sodium = toDouble(nutriments.get("sodiumServing"));
        double fiber = toDouble(nutriments.get("fiberServing"));

        double calorieMult = 1.0;
        double fatMult = 1.0;
        String prepAdvice = null;

        if ("fried".equalsIgnoreCase(preparation) || "deep_fried".equalsIgnoreCase(preparation)) {
            calorieMult = 1.35;
            fatMult = 1.8;
            prepAdvice = "Frying adds extra fats and calories compared to boiling or steaming.";
        }

        calories *= calorieMult;
        fat *= fatMult;

        Map<String, Object> adjustedNutrients = new HashMap<>(nutriments);
        adjustedNutrients.put("energyServing", Math.round(calories));
        adjustedNutrients.put("fatServing", Math.round(fat * 10.0) / 10.0);

        List<GoalEvaluationItemDto> evalGoals = new ArrayList<>();
        boolean hasWarning = false;
        boolean hasCaution = false;

        for (String goal : goals) {
            GoalEvaluationItemDto item = evaluateGoal(goal, calories, protein, carbs, fat, sugar, sodium, fiber,
                    food.isVegetarian(), food.isVegan());
            evalGoals.add(item);
            if ("WARNING".equals(item.getSeverity())) hasWarning = true;
            if ("CAUTION".equals(item.getSeverity())) hasCaution = true;
        }

        String overallStatus = "GOOD MATCH";
        if (hasWarning) overallStatus = "NOT A GOOD MATCH";
        else if (hasCaution) overallStatus = "CAUTION";

        EvaluationResultDto eval = new EvaluationResultDto();
        eval.setSuitable(!hasWarning);
        eval.setOverallStatus(overallStatus);
        eval.setPreparationAdvice(prepAdvice);
        eval.setGoals(evalGoals);
        eval.setAdjustedNutrients(adjustedNutrients);
        eval.setDisclaimer("Nutrition data sourced from OpenAI. Values are estimates and may vary. Not a substitute for medical advice.");

        return eval;
    }

    private GoalEvaluationItemDto evaluateGoal(String goal, double calories, double protein, double carbs,
                                                double fat, double sugar, double sodium, double fiber,
                                                boolean vegetarian, boolean vegan) {
        String goalTitle = formatGoalTitle(goal);
        String severity = "GOOD";
        List<String> reasons = new ArrayList<>();

        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS", "LOW_CALORIE" -> {
                if (calories <= 150) { severity = "GOOD"; reasons.add("Low energy density (~" + (int) calories + " kcal per serving)."); }
                else if (calories <= 300) { severity = "CAUTION"; reasons.add("Moderate energy (~" + (int) calories + " kcal). Monitor portion size."); }
                else { severity = "WARNING"; reasons.add("High calorie density (~" + (int) calories + " kcal per serving)."); }
            }
            case "HIGH_PROTEIN" -> {
                if (protein >= 10) { severity = "GOOD"; reasons.add("Excellent protein source (" + protein + " g per serving)."); }
                else if (protein >= 5) { severity = "GOOD"; reasons.add("Moderate protein source (" + protein + " g per serving)."); }
                else { severity = "CAUTION"; reasons.add("Low in protein (" + protein + " g per serving)."); }
            }
            case "LOW_SUGAR" -> {
                if (sugar <= 5) { severity = "GOOD"; reasons.add("Low sugar (" + sugar + " g per serving)."); }
                else if (sugar <= 12) { severity = "CAUTION"; reasons.add("Moderate natural sugars (" + sugar + " g per serving)."); }
                else { severity = "WARNING"; reasons.add("Higher sugar content (" + sugar + " g per serving)."); }
            }
            case "LOW_CARB" -> {
                if (carbs <= 5) { severity = "GOOD"; reasons.add("Very low carbs (" + carbs + " g)."); }
                else if (carbs <= 20) { severity = "CAUTION"; reasons.add("Moderate carbs (" + carbs + " g)."); }
                else { severity = "WARNING"; reasons.add("High in carbohydrates (" + carbs + " g)."); }
            }
            case "LOW_FAT" -> {
                if (fat <= 3) { severity = "GOOD"; reasons.add("Low fat (" + fat + " g)."); }
                else if (fat <= 10) { severity = "CAUTION"; reasons.add("Moderate fat (" + fat + " g)."); }
                else { severity = "WARNING"; reasons.add("Higher fat (" + fat + " g)."); }
            }
            case "LOW_SODIUM" -> {
                if (sodium <= 140) { severity = "GOOD"; reasons.add("Low sodium (" + (int) sodium + " mg)."); }
                else { severity = "WARNING"; reasons.add("Elevated sodium (" + (int) sodium + " mg)."); }
            }
            case "HIGH_FIBER" -> {
                if (fiber >= 3) { severity = "GOOD"; reasons.add("Good fiber source (" + fiber + " g)."); }
                else { severity = "CAUTION"; reasons.add("Minimal dietary fiber (" + fiber + " g)."); }
            }
            case "VEGETARIAN" -> {
                if (vegetarian) { severity = "GOOD"; reasons.add("Suitable for vegetarian diets."); }
                else { severity = "WARNING"; reasons.add("Not suitable for vegetarians."); }
            }
            case "VEGAN" -> {
                if (vegan) { severity = "GOOD"; reasons.add("100% plant-based."); }
                else { severity = "WARNING"; reasons.add("Contains animal-derived ingredients."); }
            }
            default -> { severity = "GOOD"; reasons.add("Fits within healthy dietary guidelines."); }
        }

        return new GoalEvaluationItemDto(goal, goalTitle, severity, reasons);
    }

    private String formatGoalTitle(String goal) {
        if (goal == null) return "Health Goal";
        return java.util.Arrays.stream(goal.toLowerCase().split("_"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .reduce((a, b) -> a + " " + b)
                .orElse(goal);
    }

    private double toDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return 0.0; }
    }
}
