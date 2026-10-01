package com.healthscan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "food_aliases")
public class FoodAliasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private FreshFoodEntity food;

    @Column(nullable = false)
    private String alias;

    private String language = "en";

    public FoodAliasEntity() {}

    public FoodAliasEntity(FreshFoodEntity food, String alias, String language) {
        this.food = food;
        this.alias = alias;
        this.language = language;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FreshFoodEntity getFood() { return food; }
    public void setFood(FreshFoodEntity food) { this.food = food; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
