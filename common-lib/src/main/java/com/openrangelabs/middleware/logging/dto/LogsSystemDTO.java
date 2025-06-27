package com.openrangelabs.middleware.logging.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.openrangelabs.middleware.validation.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for LogsSystem entity
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogsSystemDTO {

    private Long id;

    @NotNull(message = "Timestamp is required")
    @PastOrPresent(message = "Timestamp cannot be in the future")
    private LocalDateTime timestamp;

    @NotBlank(message = "Service name is required")
    @Size(max = 100, message = "Service name cannot exceed 100 characters")
    private String serviceName;

    @Size(max = 255, message = "Host name cannot exceed 255 characters")
    private String hostName;

    @NotNull(message = "Log level is required")
    @ValidLogLevel
    private String logLevel;

    @Size(max = 255, message = "Logger name cannot exceed 255 characters")
    private String loggerName;

    @Size(max = 255, message = "Thread name cannot exceed 255 characters")
    private String threadName;

    @NotBlank(message = "Message is required")
    private String message;

    private String stackTrace;
    private String mdcData;

    @Size(max = 50, message = "Correlation ID cannot exceed 50 characters")
    private String correlationId;

    @Min(value = 1, message = "User ID must be positive")
    private Integer userId;

    @Min(value = 1, message = "Organization ID must be positive")
    private Integer organizationId;

    @Size(max = 500, message = "Request URI cannot exceed 500 characters")
    private String requestUri;

    @ValidHttpMethod
    private String requestMethod;

    @Min(value = 100, message = "Response status must be a valid HTTP status code")
    @Max(value = 599, message = "Response status must be a valid HTTP status code")
    private Integer responseStatus;

    @Min(value = 0, message = "Execution time cannot be negative")
    private Long executionTimeMs;

    @Size(max = 50, message = "Environment cannot exceed 50 characters")
    @ValidEnvironment
    private String environment;

    @Size(max = 50, message = "Version cannot exceed 50 characters")
    private String version;

    // Default constructor
    public LogsSystemDTO() {
    }

    // Builder pattern implementation
    public static class Builder {
        private final LogsSystemDTO dto = new LogsSystemDTO();

        public Builder id(Long id) {
            dto.id = id;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            dto.timestamp = timestamp;
            return this;
        }

        public Builder serviceName(String serviceName) {
            dto.serviceName = serviceName;
            return this;
        }

        public Builder hostName(String hostName) {
            dto.hostName = hostName;
            return this;
        }

        public Builder logLevel(String logLevel) {
            dto.logLevel = logLevel;
            return this;
        }

        public Builder loggerName(String loggerName) {
            dto.loggerName = loggerName;
            return this;
        }

        public Builder threadName(String threadName) {
            dto.threadName = threadName;
            return this;
        }

        public Builder message(String message) {
            dto.message = message;
            return this;
        }

        public Builder stackTrace(String stackTrace) {
            dto.stackTrace = stackTrace;
            return this;
        }

        public Builder mdcData(String mdcData) {
            dto.mdcData = mdcData;
            return this;
        }

        public Builder correlationId(String correlationId) {
            dto.correlationId = correlationId;
            return this;
        }

        public Builder userId(Integer userId) {
            dto.userId = userId;
            return this;
        }

        public Builder organizationId(Integer organizationId) {
            dto.organizationId = organizationId;
            return this;
        }

        public Builder requestUri(String requestUri) {
            dto.requestUri = requestUri;
            return this;
        }

        public Builder requestMethod(String requestMethod) {
            dto.requestMethod = requestMethod;
            return this;
        }

        public Builder responseStatus(Integer responseStatus) {
            dto.responseStatus = responseStatus;
            return this;
        }

        public Builder executionTimeMs(Long executionTimeMs) {
            dto.executionTimeMs = executionTimeMs;
            return this;
        }

        public Builder environment(String environment) {
            dto.environment = environment;
            return this;
        }

        public Builder version(String version) {
            dto.version = version;
            return this;
        }

        public LogsSystemDTO build() {
            return dto;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getMdcData() {
        return mdcData;
    }

    public void setMdcData(String mdcData) {
        this.mdcData = mdcData;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
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

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "LogsSystemDTO{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", serviceName='" + serviceName + '\'' +
                ", logLevel='" + logLevel + '\'' +
                ", message='" + message + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}