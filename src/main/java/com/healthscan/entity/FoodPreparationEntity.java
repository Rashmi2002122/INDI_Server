package com.healthscan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "food_preparations")
public class FoodPreparationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private FreshFoodEntity food;

    @Column(nullable = false)
    private String name; // RAW, BOILED, STEAMED, GRILLED, FRIED, DEEP_FRIED, COOKED_WITH_OIL

    @Column(length = 500)
    private String description;

    @Column(name = "calorie_multiplier")
    private Double calorieMultiplier = 1.0;

    @Column(name = "fat_multiplier")
    private Double fatMultiplier = 1.0;

    public FoodPreparationEntity() {}

    public FoodPreparationEntity(FreshFoodEntity food, String name, String description, Double calorieMultiplier, Double fatMultiplier) {
        this.food = food;
        this.name = name;
        this.description = description;
        this.calorieMultiplier = calorieMultiplier;
        this.fatMultiplier = fatMultiplier;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FreshFoodEntity getFood() { return food; }
    public void setFood(FreshFoodEntity food) { this.food = food; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getCalorieMultiplier() { return calorieMultiplier; }
    public void setCalorieMultiplier(Double calorieMultiplier) { this.calorieMultiplier = calorieMultiplier; }

    public Double getFatMultiplier() { return fatMultiplier; }
    public void setMultiplier(Double multiplier) {
        this.calorieMultiplier = multiplier;
        this.fatMultiplier = multiplier;
    }
}
