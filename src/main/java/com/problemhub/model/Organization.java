package com.problemhub.model;

import java.sql.Timestamp;

/**
 * Organization model for industry partners, startups, NGOs, and innovation cells.
 */
public class Organization {
    private Integer id;
    private Integer userId;
    private String orgName;
    private String orgType;
    private String website;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private boolean verified;
    private Timestamp createdAt;

    public Organization() {
        this.verified = true;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }

    public String getOrgType() { return orgType; }
    public void setOrgType(String orgType) { this.orgType = orgType; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
