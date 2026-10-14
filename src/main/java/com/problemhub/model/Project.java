package com.problemhub.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Project model representing a student team's ongoing or completed project on a problem.
 */
public class Project {
    private Integer id;
    private Integer problemId;
    private Integer studentUserId;
    private String title;
    private String description;
    private String teamMembers;
    private String technologiesUsed;
    private String status; // PROPOSED, IN_PROGRESS, COMPLETED, ABANDONED
    private int progressPercent;
    private Date startDate;
    private Date completionDate;
    private String repoUrl;
    private String documentationUrl;
    private String facultyFeedback;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Joined fields for display
    private String problemTitle;
    private String problemDomain;
    private String studentName;

    public Project() {
        this.status = "IN_PROGRESS";
        this.progressPercent = 10;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProblemId() { return problemId; }
    public void setProblemId(Integer problemId) { this.problemId = problemId; }

    public Integer getStudentUserId() { return studentUserId; }
    public void setStudentUserId(Integer studentUserId) { this.studentUserId = studentUserId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTeamMembers() { return teamMembers; }
    public void setTeamMembers(String teamMembers) { this.teamMembers = teamMembers; }

    public String getTechnologiesUsed() { return technologiesUsed; }
    public void setTechnologiesUsed(String technologiesUsed) { this.technologiesUsed = technologiesUsed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getCompletionDate() { return completionDate; }
    public void setCompletionDate(Date completionDate) { this.completionDate = completionDate; }

    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }

    public String getDocumentationUrl() { return documentationUrl; }
    public void setDocumentationUrl(String documentationUrl) { this.documentationUrl = documentationUrl; }

    public String getFacultyFeedback() { return facultyFeedback; }
    public void setFacultyFeedback(String facultyFeedback) { this.facultyFeedback = facultyFeedback; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getProblemTitle() { return problemTitle; }
    public void setProblemTitle(String problemTitle) { this.problemTitle = problemTitle; }

    public String getProblemDomain() { return problemDomain; }
    public void setProblemDomain(String problemDomain) { this.problemDomain = problemDomain; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
}
