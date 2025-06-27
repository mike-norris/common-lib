package com.openrangelabs.middleware.messaging.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for user creation events sent via messaging
 * Used with ExchangeName.CREATE_USER
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreationEventDTO {

    @NotNull(message = "Event ID is required")
    private String eventId;

    @NotNull(message = "Event type is required")
    @Pattern(regexp = "^(USER_CREATED|USER_UPDATED|USER_DELETED|USER_ACTIVATED|USER_DEACTIVATED)$",
            message = "Event type must be one of: USER_CREATED, USER_UPDATED, USER_DELETED, USER_ACTIVATED, USER_DEACTIVATED")
    private String eventType;

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

    @Pattern(regexp = "^(ACTIVE|INACTIVE|PENDING|SUSPENDED)$",
            message = "Status must be one of: ACTIVE, INACTIVE, PENDING, SUSPENDED")
    private String status;

    @NotNull(message = "Event timestamp is required")
    @PastOrPresent(message = "Event timestamp cannot be in the future")
    private LocalDateTime eventTimestamp;

    @Size(max = 100, message = "Triggered by cannot exceed 100 characters")
    private String triggeredBy;

    @Size(max = 50, message = "Source service cannot exceed 50 characters")
    private String sourceService;

    @Size(max = 50, message = "Correlation ID cannot exceed 50 characters")
    private String correlationId;

    @Size(max = 500, message = "Additional data cannot exceed 500 characters")
    private String additionalData; // JSON string for extra metadata

    // Default constructor
    public UserCreationEventDTO() {
    }

    // Constructor with builder
    private UserCreationEventDTO(Builder builder) {
        this.eventId = builder.eventId;
        this.eventType = builder.eventType;
        this.userId = builder.userId;
        this.organizationId = builder.organizationId;
        this.username = builder.username;
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.status = builder.status;
        this.eventTimestamp = builder.eventTimestamp;
        this.triggeredBy = builder.triggeredBy;
        this.sourceService = builder.sourceService;
        this.correlationId = builder.correlationId;
        this.additionalData = builder.additionalData;
    }

    // Builder Pattern
    public static class Builder {
        private String eventId;
        private String eventType;
        private Integer userId;
        private Integer organizationId;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String status;
        private LocalDateTime eventTimestamp;
        private String triggeredBy;
        private String sourceService;
        private String correlationId;
        private String additionalData;

        public Builder() {
        }

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
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

        public Builder eventTimestamp(LocalDateTime eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
            return this;
        }

        public Builder triggeredBy(String triggeredBy) {
            this.triggeredBy = triggeredBy;
            return this;
        }

        public Builder sourceService(String sourceService) {
            this.sourceService = sourceService;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder additionalData(String additionalData) {
            this.additionalData = additionalData;
            return this;
        }

        public UserCreationEventDTO build() {
            // Set defaults
            if (this.eventId == null) {
                this.eventId = java.util.UUID.randomUUID().toString();
            }
            if (this.eventTimestamp == null) {
                this.eventTimestamp = LocalDateTime.now();
            }
            if (this.correlationId == null) {
                this.correlationId = java.util.UUID.randomUUID().toString();
            }
            return new UserCreationEventDTO(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public String getSourceService() {
        return sourceService;
    }

    public void setSourceService(String sourceService) {
        this.sourceService = sourceService;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    @Override
    public String toString() {
        return "UserCreationEventDTO{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", userId=" + userId +
                ", organizationId=" + organizationId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", eventTimestamp=" + eventTimestamp +
                ", sourceService='" + sourceService + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}