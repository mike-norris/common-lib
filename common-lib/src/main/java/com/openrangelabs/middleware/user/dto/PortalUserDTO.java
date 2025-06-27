package com.openrangelabs.middleware.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for PortalUser entity
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortalUserDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be positive")
    private Integer userId;

    @NotNull(message = "Organization ID is required")
    @Min(value = 1, message = "Organization ID must be positive")
    private Integer organizationId;

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username cannot exceed 100 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|PENDING|SUSPENDED)$",
            message = "Status must be one of: ACTIVE, INACTIVE, PENDING, SUSPENDED")
    private String status;

    @NotBlank(message = "Operation type is required")
    @Pattern(regexp = "^(CREATE|UPDATE|DELETE|ACTIVATE|DEACTIVATE)$",
            message = "Operation type must be one of: CREATE, UPDATE, DELETE, ACTIVATE, DEACTIVATE")
    private String operationType;

    @NotNull(message = "Created date is required")
    @PastOrPresent(message = "Created date cannot be in the future")
    private LocalDateTime createdDt;

    @Size(max = 100, message = "Created by cannot exceed 100 characters")
    private String createdBy;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    // Default constructor
    public PortalUserDTO() {
    }

    // Constructor with builder
    private PortalUserDTO(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.organizationId = builder.organizationId;
        this.username = builder.username;
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.status = builder.status;
        this.operationType = builder.operationType;
        this.createdDt = builder.createdDt;
        this.createdBy = builder.createdBy;
        this.notes = builder.notes;
    }

    // Builder Pattern
    public static class Builder {
        private Long id;
        private Integer userId;
        private Integer organizationId;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String status;
        private String operationType;
        private LocalDateTime createdDt;
        private String createdBy;
        private String notes;

        public Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder organizationId(Integer organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder operationType(String operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder createdDt(LocalDateTime createdDt) {
            this.createdDt = createdDt;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public PortalUserDTO build() {
            // Set defaults
            if (this.createdDt == null) {
                this.createdDt = LocalDateTime.now();
            }
            if (this.status == null) {
                this.status = "PENDING";
            }
            return new PortalUserDTO(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(LocalDateTime createdDt) {
        this.createdDt = createdDt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "PortalUserDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", organizationId=" + organizationId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", operationType='" + operationType + '\'' +
                ", createdDt=" + createdDt +
                '}';
    }
}