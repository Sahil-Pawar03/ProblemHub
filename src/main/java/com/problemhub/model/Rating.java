package com.problemhub.model;

import java.sql.Timestamp;

/**
 * Rating and review model for archived solutions.
 */
public class Rating {
    private Integer id;
    private Integer solutionId;
    private Integer userId;
    private int ratingValue; // 1 to 5
    private String reviewComment;
    private Timestamp createdAt;

    // Joined fields
    private String userName;
    private String userRole;

    public Rating() {}

    public Rating(Integer solutionId, Integer userId, int ratingValue, String reviewComment) {
        this.solutionId = solutionId;
        this.userId = userId;
        this.ratingValue = ratingValue;
        this.reviewComment = reviewComment;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSolutionId() { return solutionId; }
    public void setSolutionId(Integer solutionId) { this.solutionId = solutionId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public int getRatingValue() { return ratingValue; }
    public void setRatingValue(int ratingValue) { this.ratingValue = ratingValue; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
}
