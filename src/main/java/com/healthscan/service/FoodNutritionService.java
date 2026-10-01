package com.healthscan.service;

import com.healthscan.dto.FoodNutritionDto;
import com.healthscan.entity.FoodNutritionEntity;
import com.healthscan.entity.FreshFoodEntity;
import com.healthscan.repository.FoodNutritionRepository;
import com.healthscan.repository.FreshFoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodNutritionService {

    private final FoodNutritionRepository nutritionRepository;
    private final FreshFoodRepository freshFoodRepository;

    public FoodNutritionService(FoodNutritionRepository nutritionRepository, FreshFoodRepository freshFoodRepository) {
        this.nutritionRepository = nutritionRepository;
        this.freshFoodRepository = freshFoodRepository;
    }

    public static String normalizeQuery(String rawName) {
        if (rawName == null) return "";
        return rawName.trim().toLowerCase().replaceAll("[_\\s]+", " ");
    }

    public static String toSlug(String rawName) {
        if (rawName == null) return "";
        return rawName.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
    }

    public Optional<FoodNutritionDto> findNutrition(String foodName) {
        if (foodName == null || foodName.isBlank()) {
            return Optional.empty();
        }

        String normalized = normalizeQuery(foodName);
        String slug = toSlug(foodName);

        // 1. Search in FoodNutritionRepository by normalized name or exact name
        Optional<FoodNutritionEntity> nutOpt = nutritionRepository.findByNormalizedName(normalized);
        if (nutOpt.isPresent()) {
            return nutOpt.map(this::toDto);
        }

        nutOpt = nutritionRepository.findByFoodNameIgnoreCase(normalized);
        if (nutOpt.isPresent()) {
            return nutOpt.map(this::toDto);
        }

        // 2. Search in FreshFoodRepository by slug, name, or alias
        Optional<FreshFoodEntity> foodOpt = freshFoodRepository.findBySlug(slug);
        if (foodOpt.isEmpty()) {
            foodOpt = freshFoodRepository.findByNameIgnoreCase(foodName.trim());
        }
        if (foodOpt.isEmpty()) {
            List<FreshFoodEntity> matches = freshFoodRepository.searchByQuery(normalized);
            if (!matches.isEmpty()) {
                foodOpt = Optional.of(matches.get(0));
            }
        }

        if (foodOpt.isPresent() && foodOpt.get().getNutrition() != null) {
            return Optional.of(toDto(foodOpt.get().getNutrition()));
        }

        return Optional.empty();
    }

    public List<FoodNutritionDto> search(String query) {
        String normalized = normalizeQuery(query);
        return nutritionRepository.searchByQuery(normalized)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<FoodNutritionDto> getAll() {
        return nutritionRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public FoodNutritionDto toDto(FoodNutritionEntity entity) {
        if (entity == null) return null;
        return new FoodNutritionDto(
                entity.getId(),
                entity.getFoodName() != null ? entity.getFoodName() : (entity.getFood() != null ? entity.getFood().getName() : "Unknown"),
                entity.getNormalizedName(),
                entity.getCategory() != null ? entity.getCategory() : (entity.getFood() != null ? entity.getFood().getCategory() : "General"),
                entity.getServingSize(),
                entity.getServingUnit(),
                entity.getCalories(),
                entity.getProtein(),
                entity.getCarbohydrates(),
                entity.getTotalFat(),
                entity.getFiber(),
                entity.getSugar(),
                entity.getSodium(),
                entity.getCholesterol(),
                entity.getVitamins(),
                entity.getMinerals(),
                entity.getSource(),
                entity.isVerified(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
