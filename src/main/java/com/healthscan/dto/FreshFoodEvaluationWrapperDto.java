package com.healthscan.dto;

public class FreshFoodEvaluationWrapperDto {
    private FreshFoodDto food;
    private EvaluationResultDto evaluation;

    public FreshFoodEvaluationWrapperDto() {}

    public FreshFoodEvaluationWrapperDto(FreshFoodDto food, EvaluationResultDto evaluation) {
        this.food = food;
        this.evaluation = evaluation;
    }

    public FreshFoodDto getFood() { return food; }
    public void setFood(FreshFoodDto food) { this.food = food; }

    public EvaluationResultDto getEvaluation() { return evaluation; }
    public void setEvaluation(EvaluationResultDto evaluation) { this.evaluation = evaluation; }
}
