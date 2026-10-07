package com.healthscan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "recipes",
    indexes = {
        @Index(name = "idx_recipe_slot", columnList = "slot"),
        @Index(name = "idx_recipe_name", columnList = "name")
    }
)
public class RecipeEntity {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "slot", length = 50, nullable = false)
    private String slot; // breakfast, mid_morning, lunch, evening_snack, dinner, bedtime

    @Column(name = "diet", length = 255)
    private String diet; // comma-separated: "veg,eggetarian"

    @Column(name = "goals", length = 500)
    private String goals; // comma-separated: "muscle_gain,fat_loss,maintenance"

    @Column(name = "allergens", length = 255)
    private String allergens; // comma-separated: "dairy,gluten"

    @Column(name = "why_it_fits", length = 1000)
    private String whyItFits;

    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients; // newline or JSON formatted

    @Column(name = "method", columnDefinition = "TEXT")
    private String method; // newline or JSON formatted

    @Column(name = "calories")
    private Double calories;

    @Column(name = "protein")
    private Double protein;

    @Column(name = "carbohydrates")
    private Double carbohydrates;

    @Column(name = "fat")
    private Double fat;

    @Column(name = "time_to_make", length = 50)
    private String timeToMake;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public RecipeEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public RecipeEntity(String id, String name, String slot, String diet, String goals, 
                        String allergens, String whyItFits, String ingredients, String method, 
                        Double calories, Double protein, Double carbohydrates, Double fat, 
                        String timeToMake) {
        this.id = id;
        this.name = name;
        this.slot = slot;
        this.diet = diet;
        this.goals = goals;
        this.allergens = allergens;
        this.whyItFits = whyItFits;
        this.ingredients = ingredients;
        this.method = method;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.timeToMake = timeToMake;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public String getDiet() { return diet; }
    public void setDiet(String diet) { this.diet = diet; }

    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }

    public String getAllergens() { return allergens; }
    public void setAllergens(String allergens) { this.allergens = allergens; }

    public String getWhyItFits() { return whyItFits; }
    public void setWhyItFits(String whyItFits) { this.whyItFits = whyItFits; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }

    public Double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(Double carbohydrates) { this.carbohydrates = carbohydrates; }

    public Double getFat() { return fat; }
    public void setFat(Double fat) { this.fat = fat; }

    public String getTimeToMake() { return timeToMake; }
    public void setTimeToMake(String timeToMake) { this.timeToMake = timeToMake; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
