package com.healthscan.repository;

import com.healthscan.entity.FoodPreparationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodPreparationRepository extends JpaRepository<FoodPreparationEntity, Long> {
    List<FoodPreparationEntity> findByFoodId(Long foodId);
    Optional<FoodPreparationEntity> findByFoodIdAndNameIgnoreCase(Long foodId, String name);
}
