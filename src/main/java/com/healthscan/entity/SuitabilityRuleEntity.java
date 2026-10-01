package com.healthscan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "suitability_rules")
public class SuitabilityRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String goal;

    @Column(nullable = false)
    private String nutrient;

    @Column(nullable = false)
    private String operator;

    @Column(nullable = false)
    private Double threshold;

    private String unit;

    @Column(nullable = false)
    private String severity;

    @Column(length = 500)
    private String message;

    @Column(nullable = false)
    private boolean active = true;

    public SuitabilityRuleEntity() {}

    public SuitabilityRuleEntity(String goal, String nutrient, String operator, Double threshold, String unit, String severity, String message) {
        this.goal = goal;
        this.nutrient = nutrient;
        this.operator = operator;
        this.threshold = threshold;
        this.unit = unit;
        this.severity = severity;
        this.message = message;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getNutrient() { return nutrient; }
    public void setNutrient(String nutrient) { this.nutrient = nutrient; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
