package com.healthscan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "health_preferences")
public class HealthPreferenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private boolean active = true;

    public HealthPreferenceEntity() {}

    public HealthPreferenceEntity(String type) {
        this.type = type;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
