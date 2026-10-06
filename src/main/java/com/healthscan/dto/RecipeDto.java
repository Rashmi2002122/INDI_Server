package com.healthscan.dto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RecipeDto {

    private String id;
    private String name;
    private String slot;
    private List<String> diet;
    private List<String> goals;
    private List<String> allergens;
    private String whyItFits;
    private List<String> ingredients;
    private List<String> method;
    private Map<String, Object> nutrition;
    private String timeToMake;

    public RecipeDto() {}

    public RecipeDto(String id, String name, String slot, List<String> diet, List<String> goals,
                     List<String> allergens, String whyItFits, List<String> ingredients,
                     List<String> method, Map<String, Object> nutrition, String timeToMake) {
        this.id = id;
        this.name = name;
        this.slot = slot;
        this.diet = diet;
        this.goals = goals;
        this.allergens = allergens;
        this.whyItFits = whyItFits;
        this.ingredients = ingredients;
        this.method = method;
        this.nutrition = nutrition;
        this.timeToMake = timeToMake;
    }

    public static List<String> splitList(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(commaSeparated.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public static List<String> splitLines(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(text.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public List<String> getDiet() { return diet; }
    public void setDiet(List<String> diet) { this.diet = diet; }

    public List<String> getGoals() { return goals; }
    public void setGoals(List<String> goals) { this.goals = goals; }

    public List<String> getAllergens() { return allergens; }
    public void setAllergens(List<String> allergens) { this.allergens = allergens; }

    public String getWhyItFits() { return whyItFits; }
    public void setWhyItFits(String whyItFits) { this.whyItFits = whyItFits; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getMethod() { return method; }
    public void setMethod(List<String> method) { this.method = method; }

    public Map<String, Object> getNutrition() { return nutrition; }
    public void setNutrition(Map<String, Object> nutrition) { this.nutrition = nutrition; }

    public String getTimeToMake() { return timeToMake; }
    public void setTimeToMake(String timeToMake) { this.timeToMake = timeToMake; }
}
