package com.healthscan.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreshFoodDto {
    private Long id;
    private String name;
    private String slug;
    private String category;
    private String emoji;
    private String servingSize;
    private String servingUnit;
    private boolean vegetarian = true;
    private boolean vegan = true;
    private boolean glutenFree = true;
    private boolean lactoseFree = true;
    private String source;
    private String sourceUrl;
    private List<PreparationDto> preparations = new ArrayList<>();
    private Map<String, Object> nutriments = new HashMap<>();

    public FreshFoodDto() {}

    public FreshFoodDto(Long id, String name, String slug, String category) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public String getServingSize() { return servingSize; }
    public void setServingSize(String servingSize) { this.servingSize = servingSize; }

    public String getServingUnit() { return servingUnit; }
    public void setServingUnit(String servingUnit) { this.servingUnit = servingUnit; }

    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }

    public boolean isVegan() { return vegan; }
    public void setVegan(boolean vegan) { this.vegan = vegan; }

    public boolean isGlutenFree() { return glutenFree; }
    public void setGlutenFree(boolean glutenFree) { this.glutenFree = glutenFree; }

    public boolean isLactoseFree() { return lactoseFree; }
    public void setLactoseFree(boolean lactoseFree) { this.lactoseFree = lactoseFree; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public List<PreparationDto> getPreparations() { return preparations; }
    public void setPreparations(List<PreparationDto> preparations) { this.preparations = preparations; }

    public Map<String, Object> getNutriments() { return nutriments; }
    public void setNutriments(Map<String, Object> nutriments) { this.nutriments = nutriments; }
}
