package com.openrangelabs.middleware.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for validation error responses
 * Used when request validation fails
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationErrorResponseDTO {

    private String message;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
    private String path;
    private Integer status;

    // Default constructor
    public ValidationErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with required fields
    public ValidationErrorResponseDTO(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    // Full constructor
    public ValidationErrorResponseDTO(String message, Map<String, String> errors,
                                      LocalDateTime timestamp, String path, Integer status) {
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.path = path;
        this.status = status;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private Map<String, String> errors;
        private LocalDateTime timestamp;
        private String path;
        private Integer status;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder errors(Map<String, String> errors) {
            this.errors = errors;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder status(Integer status) {
            this.status = status;
            return this;
        }

        public ValidationErrorResponseDTO build() {
            if (this.timestamp == null) {
                this.timestamp = LocalDateTime.now();
            }
            return new ValidationErrorResponseDTO(message, errors, timestamp, path, status);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ValidationErrorResponseDTO{" +
                "message='" + message + '\'' +
                ", errors=" + errors +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                ", status=" + status +
                '}';
    }
}