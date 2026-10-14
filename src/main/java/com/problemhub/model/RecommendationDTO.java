package com.problemhub.model;

import java.util.List;

/**
 * Data Transfer Object for score-based problem recommendations with explainable reasons.
 * Ideal for viva demonstration:
 * - Tech match: +30
 * - Domain match: +25
 * - Difficulty match: +20
 * - Duration match: +15
 * - Project type match: +10
 */
public class RecommendationDTO {
    private Problem problem;
    private int score;
    private int matchPercentage;
    private String explanation;
    private List<String> matchedSkills;
    private boolean domainMatched;

    public RecommendationDTO() {}

    public RecommendationDTO(Problem problem, int score, String explanation) {
        this.problem = problem;
        this.score = score;
        this.matchPercentage = Math.min(100, Math.max(10, score));
        this.explanation = explanation;
    }

    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }

    public int getScore() { return score; }
    public void setScore(int score) {
        this.score = score;
        this.matchPercentage = Math.min(100, Math.max(10, score));
    }

    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) { this.matchPercentage = matchPercentage; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public boolean isDomainMatched() { return domainMatched; }
    public void setDomainMatched(boolean domainMatched) { this.domainMatched = domainMatched; }

    public String getBadgeClass() {
        if (matchPercentage >= 80) return "bg-success";
        if (matchPercentage >= 60) return "bg-primary";
        if (matchPercentage >= 40) return "bg-warning text-dark";
        return "bg-secondary";
    }
}
