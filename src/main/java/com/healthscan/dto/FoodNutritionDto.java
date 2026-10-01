package com.healthscan.dto;

import java.time.LocalDateTime;

public class FoodNutritionDto {
    private Long id;
    private String foodName;
    private String normalizedName;
    private String category;
    private String servingSize;
    private String servingUnit;
    private Double calories;
    private Double protein;
    private Double carbohydrates;
    private Double fat;
    private Double fiber;
    private Double sugar;
    private Double sodium;
    private Double cholesterol;
    private String vitamins;
    private String minerals;
    private String source;
    private boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FoodNutritionDto() {}

    public FoodNutritionDto(Long id, String foodName, String normalizedName, String category,
                            String servingSize, String servingUnit, Double calories, Double protein,
                            Double carbohydrates, Double fat, Double fiber, Double sugar,
                            Double sodium, Double cholesterol, String vitamins, String minerals,
                            String source, boolean verified, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.foodName = foodName;
        this.normalizedName = normalizedName;
        this.category = category;
        this.servingSize = servingSize;
        this.servingUnit = servingUnit;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.fiber = fiber;
        this.sugar = sugar;
        this.sodium = sodium;
        this.cholesterol = cholesterol;
        this.vitamins = vitamins;
        this.minerals = minerals;
        this.source = source;
        this.verified = verified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getNormalizedName() { return normalizedName; }
    public void setNormalizedName(String normalizedName) { this.normalizedName = normalizedName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getServingSize() { return servingSize; }
    public void setServingSize(String servingSize) { this.servingSize = servingSize; }

    public String getServingUnit() { return servingUnit; }
    public void setServingUnit(String servingUnit) { this.servingUnit = servingUnit; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }

    public Double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(Double carbohydrates) { this.carbohydrates = carbohydrates; }

    public Double getFat() { return fat; }
    public void setFat(Double fat) { this.fat = fat; }

    public Double getFiber() { return fiber; }
    public void setFiber(Double fiber) { this.fiber = fiber; }

    public Double getSugar() { return sugar; }
    public void setSugar(Double sugar) { this.sugar = sugar; }

    public Double getSodium() { return sodium; }
    public void setSodium(Double sodium) { this.sodium = sodium; }

    public Double getCholesterol() { return cholesterol; }
    public void setCholesterol(Double cholesterol) { this.cholesterol = cholesterol; }

    public String getVitamins() { return vitamins; }
    public void setVitamins(String vitamins) { this.vitamins = vitamins; }

    public String getMinerals() { return minerals; }
    public void setMinerals(String minerals) { this.minerals = minerals; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
