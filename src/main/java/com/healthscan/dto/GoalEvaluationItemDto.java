package com.healthscan.dto;

import java.util.List;

public class GoalEvaluationItemDto {
    private String goal;
    private String goalTitle;
    private String severity; // "GOOD", "CAUTION", "WARNING"
    private List<String> reasons;

    public GoalEvaluationItemDto() {}

    public GoalEvaluationItemDto(String goal, String goalTitle, String severity, List<String> reasons) {
        this.goal = goal;
        this.goalTitle = goalTitle;
        this.severity = severity;
        this.reasons = reasons;
    }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getGoalTitle() { return goalTitle; }
    public void setGoalTitle(String goalTitle) { this.goalTitle = goalTitle; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
}
