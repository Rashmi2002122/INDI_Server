package com.healthscan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fresh_foods")
public class FreshFoodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(name = "common_name")
    private String commonName;

    @Column(name = "scientific_name")
    private String scientificName;

    @Column(nullable = false)
    private String category; // Vegetables, Fruits, Protein, Legumes, Grains

    @Column(length = 1000)
    private String description;

    private String emoji;

    @Column(nullable = false)
    private boolean vegetarian = true;

    @Column(nullable = false)
    private boolean vegan = true;

    @Column(nullable = false)
    private boolean glutenFree = true;

    @Column(nullable = false)
    private boolean lactoseFree = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private FoodNutritionEntity nutrition;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodAliasEntity> aliases = new ArrayList<>();

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodPreparationEntity> preparations = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public FreshFoodEntity() {}

    public FreshFoodEntity(String slug, String name, String category, String emoji, boolean vegetarian, boolean vegan) {
        this.slug = slug;
        this.name = name;
        this.category = category;
        this.emoji = emoji;
        this.vegetarian = vegetarian;
        this.vegan = vegan;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCommonName() { return commonName; }
    public void setCommonName(String commonName) { this.commonName = commonName; }

    public String getScientificName() { return scientificName; }
    public void setScientificName(String scientificName) { this.scientificName = scientificName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }

    public boolean isVegan() { return vegan; }
    public void setVegan(boolean vegan) { this.vegan = vegan; }

    public boolean isGlutenFree() { return glutenFree; }
    public void setGlutenFree(boolean glutenFree) { this.glutenFree = glutenFree; }

    public boolean isLactoseFree() { return lactoseFree; }
    public void setLactoseFree(boolean lactoseFree) { this.lactoseFree = lactoseFree; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public FoodNutritionEntity getNutrition() { return nutrition; }
    public void setNutrition(FoodNutritionEntity nutrition) {
        this.nutrition = nutrition;
        if (nutrition != null) nutrition.setFood(this);
    }

    public List<FoodAliasEntity> getAliases() { return aliases; }
    public void setAliases(List<FoodAliasEntity> aliases) { this.aliases = aliases; }

    public List<FoodPreparationEntity> getPreparations() { return preparations; }
    public void setPreparations(List<FoodPreparationEntity> preparations) { this.preparations = preparations; }
}
