package com.openrangelabs.middleware.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for general error responses
 * Used for non-validation errors (IllegalArgumentException, etc.)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {

    private String message;
    private LocalDateTime timestamp;
    private String path;
    private Integer status;
    private String error;
    private String trace;

    // Default constructor
    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with message
    public ErrorResponseDTO(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with message and timestamp
    public ErrorResponseDTO(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Full constructor
    public ErrorResponseDTO(String message, LocalDateTime timestamp, String path,
                            Integer status, String error, String trace) {
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.path = path;
        this.status = status;
        this.error = error;
        this.trace = trace;
    }

    // Builder pattern
    public static class Builder {
        private String message;
        private LocalDateTime timestamp;
        private String path;
        private Integer status;
        private String error;
        private String trace;

        public Builder message(String message) {
            this.message = message;
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

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder trace(String trace) {
            this.trace = trace;
            return this;
        }

        public ErrorResponseDTO build() {
            if (this.timestamp == null) {
                this.timestamp = LocalDateTime.now();
            }
            return new ErrorResponseDTO(message, timestamp, path, status, error, trace);
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    @Override
    public String toString() {
        return "ErrorResponseDTO{" +
                "message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                ", status=" + status +
                ", error='" + error + '\'' +
                '}';
    }
}