package com.problemhub.model;

import java.sql.Timestamp;

/**
 * User model representing students, faculty, organizations, and administrators.
 */
public class User {
    private Integer id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String role; // STUDENT, FACULTY, ORGANIZATION, ADMIN
    private String college;
    private String department;
    private String yearOfStudy;
    private String skills;
    private String interests;
    private String bio;
    private boolean active;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {
        this.active = true;
    }

    public User(Integer id, String fullName, String email, String passwordHash, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = true;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Helpers
    public boolean isStudent() { return "STUDENT".equalsIgnoreCase(role); }
    public boolean isFaculty() { return "FACULTY".equalsIgnoreCase(role); }
    public boolean isOrganization() { return "ORGANIZATION".equalsIgnoreCase(role); }
    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }
}
