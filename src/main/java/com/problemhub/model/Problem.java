package com.problemhub.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Problem statement model representing authentic challenges from industries, NGOs, etc.
 */
public class Problem {
    private Integer id;
    private String title;
    private String description;
    private String realWorldContext;
    private String currentChallenges;
    private String expectedOutcome;
    private String whyItMatters;
    private String deliverables;
    private String possibleExtensions;
    private String domain;
    private String difficulty; // Beginner, Intermediate, Advanced
    private String duration;   // 1–2 Weeks, 1 Month, 2–3 Months, 3+ Months
    private String projectType; // Mini Project, Major Project, Hackathon, Research
    private String sourceType; // Industry, Startup, NGO, Government, Educational, Hackathon, Innovation Cell
    private String organizationName;
    private String contactInfo;
    private String requiredSkills;
    private String suggestedTech;
    private String status; // PENDING, APPROVED, REJECTED
    private boolean verified;
    private int viewsCount;
    private Integer createdByUserId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Supplementary UI fields
    private int solutionsCount;
    private boolean bookmarkedByCurrentUser;

    public Problem() {
        this.status = "APPROVED";
        this.verified = true;
        this.viewsCount = 0;
    }

    // Helper to get suggested tech as a list of strings
    public List<String> getTechList() {
        if (suggestedTech == null || suggestedTech.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(suggestedTech.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    // Helper to get required skills as a list of strings
    public List<String> getSkillsList() {
        if (requiredSkills == null || requiredSkills.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(requiredSkills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRealWorldContext() { return realWorldContext; }
    public void setRealWorldContext(String realWorldContext) { this.realWorldContext = realWorldContext; }

    public String getCurrentChallenges() { return currentChallenges; }
    public void setCurrentChallenges(String currentChallenges) { this.currentChallenges = currentChallenges; }

    public String getExpectedOutcome() { return expectedOutcome; }
    public void setExpectedOutcome(String expectedOutcome) { this.expectedOutcome = expectedOutcome; }

    public String getWhyItMatters() { return whyItMatters; }
    public void setWhyItMatters(String whyItMatters) { this.whyItMatters = whyItMatters; }

    public String getDeliverables() { return deliverables; }
    public void setDeliverables(String deliverables) { this.deliverables = deliverables; }

    public String getPossibleExtensions() { return possibleExtensions; }
    public void setPossibleExtensions(String possibleExtensions) { this.possibleExtensions = possibleExtensions; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getSuggestedTech() { return suggestedTech; }
    public void setSuggestedTech(String suggestedTech) { this.suggestedTech = suggestedTech; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public int getViewsCount() { return viewsCount; }
    public void setViewsCount(int viewsCount) { this.viewsCount = viewsCount; }

    public Integer getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Integer createdByUserId) { this.createdByUserId = createdByUserId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public int getSolutionsCount() { return solutionsCount; }
    public void setSolutionsCount(int solutionsCount) { this.solutionsCount = solutionsCount; }

    public boolean isBookmarkedByCurrentUser() { return bookmarkedByCurrentUser; }
    public void setBookmarkedByCurrentUser(boolean bookmarkedByCurrentUser) { this.bookmarkedByCurrentUser = bookmarkedByCurrentUser; }
}
