package com.healthscan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "packaged_products",
    indexes = {
        @Index(name = "idx_pkg_product_name", columnList = "product_name"),
        @Index(name = "idx_pkg_brand", columnList = "brand")
    }
)
public class PackagedProductEntity {

    @Id
    @Column(name = "barcode", length = 64, nullable = false)
    private String barcode;

    @Column(name = "product_name", length = 500)
    private String productName;

    @Column(name = "brand", length = 255)
    private String brand;

    @Column(name = "quantity", length = 100)
    private String quantity;

    @Column(name = "serving_size", length = 100)
    private String servingSize;

    @Column(name = "categories", length = 1000)
    private String categories;

    @Column(name = "ingredients_text", columnDefinition = "TEXT")
    private String ingredientsText;

    @Column(name = "allergens", length = 500)
    private String allergens;

    @Column(name = "diet_category", length = 50)
    private String dietCategory;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    private Double energyKcal;
    private Double protein;
    private Double carbohydrates;
    private Double sugar;
    private Double fat;
    private Double saturatedFat;
    private Double transFat;
    private Double fiber;
    private Double sodium;

    @Lob
    @Column(name = "raw_json", columnDefinition = "LONGTEXT")
    private String rawJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public PackagedProductEntity() {}

    public PackagedProductEntity(String barcode, String productName, String brand, String rawJson) {
        this.barcode = barcode;
        this.productName = productName;
        this.brand = brand;
        this.rawJson = rawJson;
    }

    // Getters and Setters
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getServingSize() { return servingSize; }
    public void setServingSize(String servingSize) { this.servingSize = servingSize; }

    public String getCategories() { return categories; }
    public void setCategories(String categories) { this.categories = categories; }

    public String getIngredientsText() { return ingredientsText; }
    public void setIngredientsText(String ingredientsText) { this.ingredientsText = ingredientsText; }

    public String getAllergens() { return allergens; }
    public void setAllergens(String allergens) { this.allergens = allergens; }

    public String getDietCategory() { return dietCategory; }
    public void setDietCategory(String dietCategory) { this.dietCategory = dietCategory; }

    public Double getEnergyKcal() { return energyKcal; }
    public void setEnergyKcal(Double energyKcal) { this.energyKcal = energyKcal; }

    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }

    public Double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(Double carbohydrates) { this.carbohydrates = carbohydrates; }

    public Double getSugar() { return sugar; }
    public void setSugar(Double sugar) { this.sugar = sugar; }

    public Double getFat() { return fat; }
    public void setFat(Double fat) { this.fat = fat; }

    public Double getSaturatedFat() { return saturatedFat; }
    public void setSaturatedFat(Double saturatedFat) { this.saturatedFat = saturatedFat; }

    public Double getTransFat() { return transFat; }
    public void setTransFat(Double transFat) { this.transFat = transFat; }

    public Double getFiber() { return fiber; }
    public void setFiber(Double fiber) { this.fiber = fiber; }

    public Double getSodium() { return sodium; }
    public void setSodium(Double sodium) { this.sodium = sodium; }

    public String getRawJson() { return rawJson; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
