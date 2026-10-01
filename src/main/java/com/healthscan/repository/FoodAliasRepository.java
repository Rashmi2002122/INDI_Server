package com.healthscan.repository;

import com.healthscan.entity.FoodAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodAliasRepository extends JpaRepository<FoodAliasEntity, Long> {
    Optional<FoodAliasEntity> findByAliasIgnoreCase(String alias);
    List<FoodAliasEntity> findByFoodId(Long foodId);
}
