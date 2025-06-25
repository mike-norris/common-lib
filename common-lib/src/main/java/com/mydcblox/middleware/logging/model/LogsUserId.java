package com.mydcblox.middleware.logging.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class LogsUserId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    // Getters, Setters, equals(), and hashCode() methods

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
}