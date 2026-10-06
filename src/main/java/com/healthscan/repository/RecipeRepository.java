package com.healthscan.repository;

import com.healthscan.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, String> {

    List<RecipeEntity> findBySlot(String slot);

    List<RecipeEntity> findBySlotIgnoreCase(String slot);
}
