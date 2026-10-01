package com.healthscan.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationResultDto {
	private boolean suitable;
	private String message;
	private double score;
	private String overallStatus = "GOOD MATCH"; // "GOOD MATCH", "CAUTION", "NOT A GOOD MATCH"
	private String preparationAdvice;
	private String disclaimer = "Notice: HealthScan provides general nutritional guidance and does not replace medical advice from a doctor or registered dietitian.";
	private List<GoalEvaluationItemDto> goals = new ArrayList<>();
	private Map<String, Object> adjustedNutrients = new HashMap<>();

	public EvaluationResultDto() {
	}

	public EvaluationResultDto(boolean suitable, String message, double score) {
		this.suitable = suitable;
		this.message = message;
		this.score = score;
	}

	public boolean isSuitable() {
		return suitable;
	}

	public void setSuitable(boolean suitable) {
		this.suitable = suitable;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getOverallStatus() {
		return overallStatus;
	}

	public void setOverallStatus(String overallStatus) {
		this.overallStatus = overallStatus;
	}

	public String getPreparationAdvice() {
		return preparationAdvice;
	}

	public void setPreparationAdvice(String preparationAdvice) {
		this.preparationAdvice = preparationAdvice;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

	public List<GoalEvaluationItemDto> getGoals() {
		return goals;
	}

	public void setGoals(List<GoalEvaluationItemDto> goals) {
		this.goals = goals;
	}

	public Map<String, Object> getAdjustedNutrients() {
		return adjustedNutrients;
	}

	public void setAdjustedNutrients(Map<String, Object> adjustedNutrients) {
		this.adjustedNutrients = adjustedNutrients;
	}
}
