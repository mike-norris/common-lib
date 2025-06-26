package com.openrangelabs.middleware.logging.model;

import jakarta.persistence.*;

@Entity
@Table(name = "logs_user")
public class LogsUser {

    @EmbeddedId
    private LogsUserId id;  // Composite key

    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "type", length = 50)
    private String type;

    // Getters and Setters

    public LogsUserId getId() {
        return id;
    }

    public void setId(LogsUserId id) {
        this.id = id;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}