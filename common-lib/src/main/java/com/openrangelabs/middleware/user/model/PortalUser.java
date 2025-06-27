package com.openrangelabs.middleware.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Entity representing a portal user creation/management event
 */
@Entity
@Table(name = "portal_user", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_organization_id", columnList = "organization_id"),
        @Index(name = "idx_created_dt", columnList = "created_dt")
})
public class PortalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be positive")
    private Integer userId;

    @Column(name = "organization_id", nullable = false)
    @NotNull(message = "Organization ID is required")
    @Min(value = 1, message = "Organization ID must be positive")
    private Integer organizationId;

    @Column(name = "username", nullable = false, length = 100)
    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username cannot exceed 100 characters")
    private String username;

    @Column(name = "email", nullable = false, length = 255)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Column(name = "first_name", length = 100)
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Column(name = "last_name", length = 100)
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Column(name = "status", nullable = false, length = 20)
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|PENDING|SUSPENDED)$",
            message = "Status must be one of: ACTIVE, INACTIVE, PENDING, SUSPENDED")
    private String status;

    @Column(name = "operation_type", nullable = false, length = 20)
    @NotBlank(message = "Operation type is required")
    @Pattern(regexp = "^(CREATE|UPDATE|DELETE|ACTIVATE|DEACTIVATE)$",
            message = "Operation type must be one of: CREATE, UPDATE, DELETE, ACTIVATE, DEACTIVATE")
    private String operationType;

    @Column(name = "created_dt", nullable = false)
    @NotNull(message = "Created date is required")
    private LocalDateTime createdDt;

    @Column(name = "created_by", length = 100)
    @Size(max = 100, message = "Created by cannot exceed 100 characters")
    private String createdBy;

    @Column(name = "notes", length = 500)
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    // Constructors
    public PortalUser() {
        this.createdDt = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Builder pattern
    public static class Builder {
        private final PortalUser portalUser = new PortalUser();

        public Builder userId(Integer userId) {
            portalUser.userId = userId;
            return this;
        }

        public Builder organizationId(Integer organizationId) {
            portalUser.organizationId = organizationId;
            return this;
        }

        public Builder username(String username) {
            portalUser.username = username;
            return this;
        }

        public Builder email(String email) {
            portalUser.email = email;
            return this;
        }

        public Builder firstName(String firstName) {
            portalUser.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            portalUser.lastName = lastName;
            return this;
        }

        public Builder status(String status) {
            portalUser.status = status;
            return this;
        }

        public Builder operationType(String operationType) {
            portalUser.operationType = operationType;
            return this;
        }

        public Builder createdBy(String createdBy) {
            portalUser.createdBy = createdBy;
            return this;
        }

        public Builder notes(String notes) {
            portalUser.notes = notes;
            return this;
        }

        public Builder createdDt(LocalDateTime createdDt) {
            portalUser.createdDt = createdDt;
            return this;
        }

        public PortalUser build() {
            return portalUser;
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
        return "PortalUser{" +
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