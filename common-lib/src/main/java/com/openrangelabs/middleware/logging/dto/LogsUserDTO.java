package com.openrangelabs.middleware.logging.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for LogsUser entity
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogsUserDTO {

    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be positive")
    private Integer userId;

    @NotNull(message = "Created date is required")
    @PastOrPresent(message = "Created date cannot be in the future")
    private LocalDateTime createdDt;

    @NotNull(message = "Organization ID is required")
    @Min(value = 1, message = "Organization ID must be positive")
    private Integer organizationId;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Pattern(regexp = "^(LOGIN|LOGOUT|CREATE|UPDATE|DELETE|VIEW|DOWNLOAD|UPLOAD|ERROR|AUDIT)$",
            message = "Type must be one of: LOGIN, LOGOUT, CREATE, UPDATE, DELETE, VIEW, DOWNLOAD, UPLOAD, ERROR, AUDIT")
    private String type;

    // Default constructor
    public LogsUserDTO() {
    }

    // Constructor with all fields
    private LogsUserDTO(Builder builder) {
        this.userId = builder.userId;
        this.createdDt = builder.createdDt;
        this.organizationId = builder.organizationId;
        this.description = builder.description;
        this.type = builder.type;
    }

    // Builder Pattern
    public static class Builder {
        private Integer userId;
        private LocalDateTime createdDt;
        private Integer organizationId;
        private String description;
        private String type;

        public Builder() {
        }

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder createdDt(LocalDateTime createdDt) {
            this.createdDt = createdDt;
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

        public LogsUserDTO build() {
            // Set default created date if not provided
            if (this.createdDt == null) {
                this.createdDt = LocalDateTime.now();
            }
            return new LogsUserDTO(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(LocalDateTime createdDt) {
        this.createdDt = createdDt;
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

    @Override
    public String toString() {
        return "LogsUserDTO{" +
                "userId=" + userId +
                ", createdDt=" + createdDt +
                ", organizationId=" + organizationId +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}