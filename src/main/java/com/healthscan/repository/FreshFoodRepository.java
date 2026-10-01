package com.healthscan.repository;

import com.healthscan.entity.FreshFoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FreshFoodRepository extends JpaRepository<FreshFoodEntity, Long> {

    Optional<FreshFoodEntity> findBySlug(String slug);
    Optional<FreshFoodEntity> findByNameIgnoreCase(String name);

    @Query("SELECT DISTINCT f FROM FreshFoodEntity f LEFT JOIN f.aliases a " +
           "WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(f.commonName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(a.alias) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FreshFoodEntity> searchByQuery(@Param("query") String query);

    List<FreshFoodEntity> findByCategoryIgnoreCase(String category);

    List<FreshFoodEntity> findByVegetarianTrue();
    List<FreshFoodEntity> findByVeganTrue();
}
