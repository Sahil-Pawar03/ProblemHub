package com.problemhub.model;

import java.sql.Timestamp;

/**
 * Bookmark model representing student-saved problem statements.
 */
public class Bookmark {
    private Integer id;
    private Integer userId;
    private Integer problemId;
    private Timestamp createdAt;

    // Joined problem details
    private Problem problem;

    public Bookmark() {}

    public Bookmark(Integer userId, Integer problemId) {
        this.userId = userId;
        this.problemId = problemId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getProblemId() { return problemId; }
    public void setProblemId(Integer problemId) { this.problemId = problemId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
}
