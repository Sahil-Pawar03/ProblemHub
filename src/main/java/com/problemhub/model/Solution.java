package com.problemhub.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Solution model representing an archived student project in the ProblemHub knowledge base.
 */
public class Solution {
    private Integer id;
    private Integer projectId;
    private Integer problemId;
    private Integer submittedByUserId;
    private String title;
    private String teamNames;
    private String college;
    private int completionYear;
    private String technologies;
    private String solutionOverview;
    private String architectureDetails;
    private String implementationDetails;
    private String keyFeatures;
    private String results;
    private String challenges;
    private String limitations;
    private String futureScope;
    private String githubUrl;
    private String documentationUrl;
    private String demoUrl;
    private String screenshotsJson;
    private String status; // PENDING, APPROVED, REJECTED
    private boolean verified;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Joined fields for display
    private String problemTitle;
    private String problemDomain;
    private double averageRating;
    private int ratingsCount;

    public Solution() {
        this.status = "APPROVED";
        this.verified = true;
    }

    public List<String> getTechList() {
        if (technologies == null || technologies.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(technologies.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProjectId() { return projectId; }
    public void setProjectId(Integer projectId) { this.projectId = projectId; }

    public Integer getProblemId() { return problemId; }
    public void setProblemId(Integer problemId) { this.problemId = problemId; }

    public Integer getSubmittedByUserId() { return submittedByUserId; }
    public void setSubmittedByUserId(Integer submittedByUserId) { this.submittedByUserId = submittedByUserId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTeamNames() { return teamNames; }
    public void setTeamNames(String teamNames) { this.teamNames = teamNames; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public int getCompletionYear() { return completionYear; }
    public void setCompletionYear(int completionYear) { this.completionYear = completionYear; }

    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }

    public String getSolutionOverview() { return solutionOverview; }
    public void setSolutionOverview(String solutionOverview) { this.solutionOverview = solutionOverview; }

    public String getArchitectureDetails() { return architectureDetails; }
    public void setArchitectureDetails(String architectureDetails) { this.architectureDetails = architectureDetails; }

    public String getImplementationDetails() { return implementationDetails; }
    public void setImplementationDetails(String implementationDetails) { this.implementationDetails = implementationDetails; }

    public String getKeyFeatures() { return keyFeatures; }
    public void setKeyFeatures(String keyFeatures) { this.keyFeatures = keyFeatures; }

    public String getResults() { return results; }
    public void setResults(String results) { this.results = results; }

    public String getChallenges() { return challenges; }
    public void setChallenges(String challenges) { this.challenges = challenges; }

    public String getLimitations() { return limitations; }
    public void setLimitations(String limitations) { this.limitations = limitations; }

    public String getFutureScope() { return futureScope; }
    public void setFutureScope(String futureScope) { this.futureScope = futureScope; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getDocumentationUrl() { return documentationUrl; }
    public void setDocumentationUrl(String documentationUrl) { this.documentationUrl = documentationUrl; }

    public String getDemoUrl() { return demoUrl; }
    public void setDemoUrl(String demoUrl) { this.demoUrl = demoUrl; }

    public String getScreenshotsJson() { return screenshotsJson; }
    public void setScreenshotsJson(String screenshotsJson) { this.screenshotsJson = screenshotsJson; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getProblemTitle() { return problemTitle; }
    public void setProblemTitle(String problemTitle) { this.problemTitle = problemTitle; }

    public String getProblemDomain() { return problemDomain; }
    public void setProblemDomain(String problemDomain) { this.problemDomain = problemDomain; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getRatingsCount() { return ratingsCount; }
    public void setRatingsCount(int ratingsCount) { this.ratingsCount = ratingsCount; }
}
