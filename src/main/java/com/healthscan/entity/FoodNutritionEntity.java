package com.healthscan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_nutrition")
public class FoodNutritionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_id", nullable = true)
    private FreshFoodEntity food;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "normalized_name")
    private String normalizedName;

    private String category;

    @Column(name = "serving_size")
    private String servingSize;

    @Column(name = "serving_unit")
    private String servingUnit;

    @Column(name = "serving_description")
    private String servingDescription;

    @Column(name = "serving_grams")
    private Double servingGrams;

    private Double calories;
    private Double protein;
    private Double carbohydrates;

    @Column(name = "total_fat")
    private Double totalFat;

    @Column(name = "saturated_fat")
    private Double saturatedFat;

    @Column(name = "trans_fat")
    private Double transFat;

    private Double fiber;
    private Double sugar;
    private Double sodium;
    private Double cholesterol;

    private Double calcium;
    private Double iron;
    private Double potassium;
    private Double vitaminC;

    @Column(length = 1000)
    private String vitamins;

    @Column(length = 1000)
    private String minerals;

    private String source;

    @Column(name = "source_url")
    private String sourceUrl;

    private boolean verified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public FoodNutritionEntity() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FreshFoodEntity getFood() { return food; }
    public void setFood(FreshFoodEntity food) { this.food = food; }

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

    public String getServingDescription() { return servingDescription; }
    public void setServingDescription(String servingDescription) { this.servingDescription = servingDescription; }

    public Double getServingGrams() { return servingGrams; }
    public void setServingGrams(Double servingGrams) { this.servingGrams = servingGrams; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }

    public Double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(Double carbohydrates) { this.carbohydrates = carbohydrates; }
    public Double getCarbs() { return carbohydrates; }
    public void setCarbs(Double carbs) { this.carbohydrates = carbs; }

    public Double getTotalFat() { return totalFat; }
    public void setTotalFat(Double totalFat) { this.totalFat = totalFat; }
    public Double getFat() { return totalFat; }
    public void setFat(Double fat) { this.totalFat = fat; }

    public Double getSaturatedFat() { return saturatedFat; }
    public void setSaturatedFat(Double saturatedFat) { this.saturatedFat = saturatedFat; }

    public Double getTransFat() { return transFat; }
    public void setTransFat(Double transFat) { this.transFat = transFat; }

    public Double getFiber() { return fiber; }
    public void setFiber(Double fiber) { this.fiber = fiber; }

    public Double getSugar() { return sugar; }
    public void setSugar(Double sugar) { this.sugar = sugar; }

    public Double getSodium() { return sodium; }
    public void setSodium(Double sodium) { this.sodium = sodium; }

    public Double getCholesterol() { return cholesterol; }
    public void setCholesterol(Double cholesterol) { this.cholesterol = cholesterol; }

    public Double getCalcium() { return calcium; }
    public void setCalcium(Double calcium) { this.calcium = calcium; }

    public Double getIron() { return iron; }
    public void setIron(Double iron) { this.iron = iron; }

    public Double getPotassium() { return potassium; }
    public void setPotassium(Double potassium) { this.potassium = potassium; }

    public Double getVitaminC() { return vitaminC; }
    public void setVitaminC(Double vitaminC) { this.vitaminC = vitaminC; }

    public String getVitamins() { return vitamins; }
    public void setVitamins(String vitamins) { this.vitamins = vitamins; }

    public String getMinerals() { return minerals; }
    public void setMinerals(String minerals) { this.minerals = minerals; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
