package com.openrangelabs.middleware.logging.model;

import com.openrangelabs.middleware.validation.ValidUserLogType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "logs_user")
public class LogsUser {

    @EmbeddedId
    @Valid
    @NotNull(message = "User log ID is required")
    private LogsUserId id;

    @Column(name = "organization_id", nullable = false)
    @NotNull(message = "Organization ID is required")
    @Min(value = 1, message = "Organization ID must be positive")
    private Integer organizationId;

    @Column(name = "description", length = 255)
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Column(name = "type", length = 50)
    @ValidUserLogType
    private String type;

    // Default constructor
    public LogsUser() {
    }

    // Constructor for Builder
    private LogsUser(Builder builder) {
        this.id = builder.id;
        this.organizationId = builder.organizationId;
        this.description = builder.description;
        this.type = builder.type;
    }

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

    // Builder Pattern
    public static class Builder {
        private LogsUserId id;
        private Integer organizationId;
        private String description;
        private String type;

        public Builder() {
        }

        public Builder id(LogsUserId id) {
            this.id = id;
            return this;
        }

        public Builder userId(Integer userId) {
            if (this.id == null) {
                this.id = new LogsUserId();
            }
            this.id.setUserId(userId);
            return this;
        }

        public Builder createdDt(java.time.LocalDateTime createdDt) {
            if (this.id == null) {
                this.id = new LogsUserId();
            }
            this.id.setCreatedDt(createdDt);
            return this;
        }

        public Builder organizationId(Integer organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public LogsUser build() {
            // Set default created date if not provided
            if (this.id != null && this.id.getCreatedDt() == null) {
                this.id.setCreatedDt(java.time.LocalDateTime.now());
            }
            return new LogsUser(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "LogsUser{" +
                "id=" + id +
                ", organizationId=" + organizationId +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}