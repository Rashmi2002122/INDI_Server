package com.healthscan.repository;

import com.healthscan.entity.FoodNutritionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodNutritionRepository extends JpaRepository<FoodNutritionEntity, Long> {

    Optional<FoodNutritionEntity> findByNormalizedName(String normalizedName);

    Optional<FoodNutritionEntity> findByFoodNameIgnoreCase(String foodName);

    @Query("SELECT fn FROM FoodNutritionEntity fn WHERE LOWER(fn.foodName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(fn.normalizedName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FoodNutritionEntity> searchByQuery(@Param("query") String query);
}
