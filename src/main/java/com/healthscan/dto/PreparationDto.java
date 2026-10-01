package com.healthscan.dto;

public class PreparationDto {
    private String id;
    private String name;
    private String description;
    private Double calorieMultiplier;
    private Double fatMultiplier;

    public PreparationDto() {}

    public PreparationDto(String id, String name, String description, Double calorieMultiplier, Double fatMultiplier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.calorieMultiplier = calorieMultiplier;
        this.fatMultiplier = fatMultiplier;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getCalorieMultiplier() { return calorieMultiplier; }
    public void setCalorieMultiplier(Double calorieMultiplier) { this.calorieMultiplier = calorieMultiplier; }

    public Double getFatMultiplier() { return fatMultiplier; }
    public void setFatMultiplier(Double fatMultiplier) { this.fatMultiplier = fatMultiplier; }
}
