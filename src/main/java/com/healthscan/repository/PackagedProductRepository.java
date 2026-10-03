package com.healthscan.repository;

import com.healthscan.entity.PackagedProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackagedProductRepository extends JpaRepository<PackagedProductEntity, String> {
    Optional<PackagedProductEntity> findByBarcode(String barcode);
}
