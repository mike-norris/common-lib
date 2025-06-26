package com.openrangelabs.middleware.logging.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class LogsUserId implements Serializable {

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be positive")
    private Integer userId;

    @Column(name = "created_dt", nullable = false)
    @NotNull(message = "Created date is required")
    @PastOrPresent(message = "Created date cannot be in the future")
    private LocalDateTime createdDt;

    // Default constructor
    public LogsUserId() {
    }

    // Constructor with parameters
    public LogsUserId(Integer userId, LocalDateTime createdDt) {
        this.userId = userId;
        this.createdDt = createdDt;
    }

    // Builder pattern
    public static class Builder {
        private Integer userId;
        private LocalDateTime createdDt;

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder createdDt(LocalDateTime createdDt) {
            this.createdDt = createdDt;
            return this;
        }

        public LogsUserId build() {
            LogsUserId id = new LogsUserId();
            id.userId = this.userId;
            id.createdDt = this.createdDt != null ? this.createdDt : LocalDateTime.now();
            return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogsUserId that = (LogsUserId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(createdDt, that.createdDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, createdDt);
    }

    @Override
    public String toString() {
        return "LogsUserId{" +
                "userId=" + userId +
                ", createdDt=" + createdDt +
                '}';
    }
}