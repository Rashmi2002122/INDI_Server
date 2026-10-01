package com.healthscan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_goals")
public class UserGoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String goal;

    @Column(nullable = false)
    private boolean active = true;

    public UserGoalEntity() {}

    public UserGoalEntity(String goal) {
        this.goal = goal;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
