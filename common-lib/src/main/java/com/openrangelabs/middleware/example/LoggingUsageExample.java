package com.openrangelabs.middleware.example;

import com.openrangelabs.middleware.logging.dto.LogsSystemDTO;
import com.openrangelabs.middleware.logging.dto.LogsUserDTO;
import com.openrangelabs.middleware.logging.model.LogLevel;
import com.openrangelabs.middleware.logging.model.UserLogType;
import com.openrangelabs.middleware.logging.service.LogsSystemService;
import com.openrangelabs.middleware.logging.service.LogsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Example usage of the enhanced logging library with builder pattern and validation
 */
@Component
public class LoggingUsageExample {

    @Autowired
    private LogsUserService userLogService;

    @Autowired
    private LogsSystemService systemLogService;

    /**
     * Example of logging user activities
     */
    public void logUserActivities() {
        // Log a user login using the builder pattern
        LogsUserDTO loginLog = LogsUserDTO.builder()
                .userId(12345)
                .organizationId(100)
                .type(UserLogType.LOGIN.getCode())
                .description("User logged in from IP: 192.168.1.100")
                .createdDt(LocalDateTime.now())
                .build();

        userLogService.saveLog(loginLog);

        // Log using convenience method
        userLogService.logLogin(12345, 100, "Browser: Chrome, OS: Windows 10");

        // Log a resource creation
        LogsUserDTO createLog = LogsUserDTO.builder()
                .userId(12345)
                .organizationId(100)
                .type(UserLogType.CREATE.getCode())
                .description("Created new document: DOC-2024-001")
                .build(); // createdDt will be set automatically to now()

        userLogService.saveLog(createLog);

        // Log an error event
        userLogService.logUserAction(
                12345,
                100,
                UserLogType.ERROR.getCode(),
                "Failed to upload file: file_too_large.pdf"
        );
    }

    /**
     * Example of logging system events
     */
    public void logSystemEvents() {
        // Log a system info message
        LogsSystemDTO infoLog = LogsSystemDTO.builder()
                .serviceName("user-service")
                .hostName("prod-server-01")
                .logLevel(LogLevel.INFO.toString())
                .loggerName("com.openrangelabs.UserController")
                .threadName("http-nio-8080-exec-1")
                .message("Successfully processed user registration")
                .correlationId("req-12345-67890")
                .userId(12345)
                .organizationId(100)
                .requestUri("/api/v1/users/register")
                .requestMethod("POST")
                .responseStatus(201)
                .executionTimeMs(145L)
                .environment("production")
                .version("1.2.3")
                .timestamp(LocalDateTime.now())
                .build();

        systemLogService.saveLog(infoLog);

        // Log an error with stack trace
        try {
            // Some operation that might fail
            throw new RuntimeException("Database connection failed");
        } catch (Exception e) {
            systemLogService.logException("user-service", e, 12345, 100);
        }

        // Log a warning for slow request
        LogsSystemDTO slowRequestLog = LogsSystemDTO.builder()
                .serviceName("api-gateway")
                .logLevel(LogLevel.WARN.toString())
                .message("Slow request detected")
                .requestUri("/api/v1/reports/generate")
                .requestMethod("GET")
                .responseStatus(200)
                .executionTimeMs(5234L)
                .correlationId("req-99999-88888")
                .build();

        systemLogService.saveLog(slowRequestLog);
    }

    /**
     * Example of querying logs
     */
    public void queryLogs() {
        // Find user logs for last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        var userLogs = userLogService.findByUserId(12345, thirtyDaysAgo, false);

        // Find organization logs
        var orgLogs = userLogService.findByOrganizationId(100, thirtyDaysAgo, true);

        // Find system errors
        var errorLogs = systemLogService.findErrorLogs(
                LocalDateTime.now().minusDays(7),
                org.springframework.data.domain.PageRequest.of(0, 20)
        );

        // Find slow requests (over 3 seconds)
        var slowRequests = systemLogService.findSlowRequests(3000L, thirtyDaysAgo);

        // Search logs by multiple criteria
        var searchResults = systemLogService.searchLogs(
                "user-service",     // service name
                LogLevel.ERROR.toString(), // log level
                12345,              // user ID
                100,                // organization ID
                "database",         // search term in message
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now(),
                org.springframework.data.domain.PageRequest.of(0, 50)
        );

        // Get log statistics
        var logStats = systemLogService.getLogStatsByLevel(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        // Find logs by correlation ID (useful for tracing requests)
        var correlatedLogs = systemLogService.findByCorrelationId("req-12345-67890");
    }

    /**
     * Example of batch operations
     */
    public void batchOperations() {
        // Prepare multiple user logs
        var batchLogs = java.util.List.of(
                LogsUserDTO.builder()
                        .userId(12345)
                        .organizationId(100)
                        .type(UserLogType.VIEW.getCode())
                        .description("Viewed dashboard")
                        .build(),
                LogsUserDTO.builder()
                        .userId(12345)
                        .organizationId(100)
                        .type(UserLogType.DOWNLOAD.getCode())
                        .description("Downloaded report: monthly-sales.pdf")
                        .build(),
                LogsUserDTO.builder()
                        .userId(12345)
                        .organizationId(100)
                        .type(UserLogType.UPDATE.getCode())
                        .description("Updated profile information")
                        .build()
        );

        // Save all logs in one operation
        userLogService.saveLogsBatch(batchLogs);

        // System logs batch
        var systemBatch = java.util.List.of(
                LogsSystemDTO.builder()
                        .serviceName("batch-processor")
                        .logLevel(LogLevel.INFO.toString())
                        .message("Batch job started: daily-cleanup")
                        .build(),
                LogsSystemDTO.builder()
                        .serviceName("batch-processor")
                        .logLevel(LogLevel.INFO.toString())
                        .message("Batch job completed: daily-cleanup")
                        .executionTimeMs(45000L)
                        .build()
        );

        systemLogService.saveLogsBatch(systemBatch);
    }

    /**
     * Example of handling validation errors
     */
    public void validationExamples() {
        try {
            // This will fail validation - userId is required
            LogsUserDTO invalidLog = LogsUserDTO.builder()
                    .organizationId(100)
                    .type(UserLogType.LOGIN.getCode())
                    .build();

            userLogService.saveLog(invalidLog);
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
        }

        try {
            // This will fail - invalid log type
            LogsUserDTO invalidType = LogsUserDTO.builder()
                    .userId(12345)
                    .organizationId(100)
                    .type("INVALID_TYPE")
                    .build();

            userLogService.saveLog(invalidType);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid log type: " + e.getMessage());
        }

        try {
            // This will fail - description too long
            LogsUserDTO tooLongDesc = LogsUserDTO.builder()
                    .userId(12345)
                    .organizationId(100)
                    .type(UserLogType.CREATE.getCode())
                    .description("A".repeat(300)) // Max is 255
                    .build();

            userLogService.saveLog(tooLongDesc);
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
        }
    }

    /**
     * Example of maintenance operations
     */
    public void maintenanceOperations() {
        // Delete logs older than 1 year (data retention policy)
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        userLogService.deleteOldLogs(oneYearAgo);
        systemLogService.deleteOldLogs(oneYearAgo);

        // Get user activity summary
        var userActivitySummary = userLogService.countUserLogsByType(
                12345,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        );

        // Print summary
        userActivitySummary.forEach(summary -> {
            String type = (String) summary[0];
            Long count = (Long) summary[1];
            System.out.println(type + ": " + count + " occurrences");
        });
    }
}