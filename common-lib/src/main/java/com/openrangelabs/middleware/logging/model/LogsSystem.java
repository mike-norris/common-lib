package com.openrangelabs.middleware.logging.model;

import com.openrangelabs.middleware.validation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_system", indexes = {
        @Index(name = "idx_service_name_timestamp", columnList = "service_name, timestamp"),
        @Index(name = "idx_log_level_timestamp", columnList = "log_level, timestamp"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class LogsSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "timestamp", nullable = false)
    @NotNull(message = "Timestamp is required")
    @PastOrPresent(message = "Timestamp cannot be in the future")
    private LocalDateTime timestamp;

    @Column(name = "service_name", nullable = false, length = 100)
    @NotBlank(message = "Service name is required")
    @Size(max = 100, message = "Service name cannot exceed 100 characters")
    private String serviceName;

    @Column(name = "host_name", length = 255)
    @Size(max = 255, message = "Host name cannot exceed 255 characters")
    private String hostName;

    @Column(name = "log_level", nullable = false, length = 20)
    @NotNull(message = "Log level is required")
    @ValidLogLevel
    private String logLevel;

    @Column(name = "logger_name", length = 255)
    @Size(max = 255, message = "Logger name cannot exceed 255 characters")
    private String loggerName;

    @Column(name = "thread_name", length = 255)
    @Size(max = 255, message = "Thread name cannot exceed 255 characters")
    private String threadName;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Message is required")
    private String message;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "mdc_data", columnDefinition = "TEXT")
    private String mdcData; // For storing MDC (Mapped Diagnostic Context) as JSON

    @Column(name = "correlation_id", length = 50)
    @Size(max = 50, message = "Correlation ID cannot exceed 50 characters")
    private String correlationId;

    @Column(name = "user_id")
    @Min(value = 1, message = "User ID must be positive")
    private Integer userId;

    @Column(name = "organization_id")
    @Min(value = 1, message = "Organization ID must be positive")
    private Integer organizationId;

    @Column(name = "request_uri", length = 500)
    @Size(max = 500, message = "Request URI cannot exceed 500 characters")
    private String requestUri;

    @Column(name = "request_method", length = 10)
    @ValidHttpMethod
    private String requestMethod;

    @Column(name = "response_status")
    @Min(value = 100, message = "Response status must be a valid HTTP status code")
    @Max(value = 599, message = "Response status must be a valid HTTP status code")
    private Integer responseStatus;

    @Column(name = "execution_time_ms")
    @Min(value = 0, message = "Execution time cannot be negative")
    private Long executionTimeMs;

    @Column(name = "environment", length = 50)
    @Size(max = 50, message = "Environment cannot exceed 50 characters")
    @ValidEnvironment
    private String environment;

    @Column(name = "version", length = 50)
    @Size(max = 50, message = "Version cannot exceed 50 characters")
    private String version;

    // Constructors
    public LogsSystem() {
        this.timestamp = LocalDateTime.now();
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

    // Builder pattern for easier object creation
    public static class Builder {
        private final LogsSystem logsSystem = new LogsSystem();

        public Builder serviceName(String serviceName) {
            logsSystem.serviceName = serviceName;
            return this;
        }

        public Builder hostName(String hostName) {
            logsSystem.hostName = hostName;
            return this;
        }

        public Builder logLevel(String logLevel) {
            logsSystem.logLevel = logLevel;
            return this;
        }

        public Builder loggerName(String loggerName) {
            logsSystem.loggerName = loggerName;
            return this;
        }

        public Builder threadName(String threadName) {
            logsSystem.threadName = threadName;
            return this;
        }

        public Builder message(String message) {
            logsSystem.message = message;
            return this;
        }

        public Builder stackTrace(String stackTrace) {
            logsSystem.stackTrace = stackTrace;
            return this;
        }

        public Builder mdcData(String mdcData) {
            logsSystem.mdcData = mdcData;
            return this;
        }

        public Builder correlationId(String correlationId) {
            logsSystem.correlationId = correlationId;
            return this;
        }

        public Builder userId(Integer userId) {
            logsSystem.userId = userId;
            return this;
        }

        public Builder organizationId(Integer organizationId) {
            logsSystem.organizationId = organizationId;
            return this;
        }

        public Builder requestUri(String requestUri) {
            logsSystem.requestUri = requestUri;
            return this;
        }

        public Builder requestMethod(String requestMethod) {
            logsSystem.requestMethod = requestMethod;
            return this;
        }

        public Builder responseStatus(Integer responseStatus) {
            logsSystem.responseStatus = responseStatus;
            return this;
        }

        public Builder executionTimeMs(Long executionTimeMs) {
            logsSystem.executionTimeMs = executionTimeMs;
            return this;
        }

        public Builder environment(String environment) {
            logsSystem.environment = environment;
            return this;
        }

        public Builder version(String version) {
            logsSystem.version = version;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            logsSystem.timestamp = timestamp;
            return this;
        }

        public LogsSystem build() {
            if (logsSystem.timestamp == null) {
                logsSystem.timestamp = LocalDateTime.now();
            }
            return logsSystem;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}