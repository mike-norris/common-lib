# Logging Guide

The middleware library provides comprehensive logging capabilities with two main types: User Activity Logs and System Logs. Both are backed by JPA entities and provide rich querying capabilities.

## Overview

- **User Logs** (`LogsUser`) - Track user activities and actions
- **System Logs** (`LogsSystem`) - Track system events, errors, and performance metrics

## User Activity Logging

### LogsUserService

The `LogsUserService` handles user activity tracking with audit trails.

#### Basic Usage

```java
@Autowired
private LogsUserService logsUserService;

// Log a user login
LogsUserDTO loginLog = LogsUserDTO.builder()
    .userId(12345)
    .organizationId(100)
    .type(UserLogType.LOGIN.getCode())
    .description("User logged in from mobile app")
    .build();

logsUserService.saveLog(loginLog);
```

#### Convenience Methods

```java
// Quick login logging
logsUserService.logLogin(userId, organizationId, "IP: 192.168.1.100");

// Quick logout logging
logsUserService.logLogout(userId, organizationId, "Session timeout");

// Generic user action
logsUserService.logUserAction(userId, organizationId, 
    UserLogType.CREATE.getCode(), "Created new document");
```

#### Batch Operations

```java
List<LogsUserDTO> batchLogs = Arrays.asList(
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
        .description("Downloaded report")
        .build()
);

logsUserService.saveLogsBatch(batchLogs);
```

### User Log Types

The `UserLogType` enum provides standardized activity types:

```java
public enum UserLogType {
    LOGIN("LOGIN", "User login event"),
    LOGOUT("LOGOUT", "User logout event"),
    CREATE("CREATE", "Resource creation"),
    UPDATE("UPDATE", "Resource update"),
    DELETE("DELETE", "Resource deletion"),
    VIEW("VIEW", "Resource view/read"),
    DOWNLOAD("DOWNLOAD", "File download"),
    UPLOAD("UPLOAD", "File upload"),
    ERROR("ERROR", "Error event"),
    AUDIT("AUDIT", "Audit event");
}
```

### Querying User Logs

```java
// Find logs for a specific user (last year by default)
List<LogsUserDTO> userLogs = logsUserService.findByUserId(12345, null, false);

// Find logs for an organization
LocalDateTime startDate = LocalDateTime.now().minusDays(30);
List<LogsUserDTO> orgLogs = logsUserService.findByOrganizationId(100, startDate, true);

// Find specific log by composite key
Optional<LogsUserDTO> specificLog = logsUserService.findById(12345, timestamp);

// Get activity statistics
List<Object[]> stats = logsUserService.countUserLogsByType(
    12345, 
    LocalDateTime.now().minusDays(30),
    LocalDateTime.now()
);
```

## System Logging

### LogsSystemService

The `LogsSystemService` handles system events, errors, and performance metrics.

#### Basic Usage

```java
@Autowired
private LogsSystemService logsSystemService;

// Log a system event
LogsSystemDTO systemLog = LogsSystemDTO.builder()
    .serviceName("user-service")
    .logLevel(LogLevel.INFO.toString())
    .message("User registration completed")
    .userId(12345)
    .organizationId(100)
    .requestUri("/api/v1/users/register")
    .requestMethod(HttpMethod.POST.getMethod())
    .responseStatus(HttpStatus.CREATED.getCode())
    .executionTimeMs(145L)
    .correlationId("req-12345-67890")
    .build();

logsSystemService.saveLog(systemLog);
```

#### Exception Logging

```java
try {
    // Some operation that might fail
    performRiskyOperation();
} catch (Exception e) {
    // Automatically logs with stack trace
    logsSystemService.logException("user-service", e, userId, organizationId);
}
```

### Log Levels

The `LogLevel` enum provides standard logging levels:

```java
public enum LogLevel {
    TRACE("TRACE", 0),
    DEBUG("DEBUG", 1),
    INFO("INFO", 2),
    WARN("WARN", 3),
    ERROR("ERROR", 4),
    FATAL("FATAL", 5);
    
    // Check if level is enabled
    if (LogLevel.ERROR.isEnabledFor(LogLevel.WARN)) {
        // Log the message
    }
}
```

### Advanced System Logging

#### HTTP Request Logging

```java
LogsSystemDTO httpLog = LogsSystemDTO.builder()
    .serviceName("api-gateway")
    .logLevel(LogLevel.INFO.toString())
    .message("HTTP request processed")
    .requestUri("/api/v1/orders/12345")
    .requestMethod(HttpMethod.GET.getMethod())
    .responseStatus(HttpStatus.OK.getCode())
    .executionTimeMs(234L)
    .userId(12345)
    .organizationId(100)
    .correlationId("req-abc-123")
    .environment(Environment.PRODUCTION.getName())
    .version("1.2.3")
    .build();

logsSystemService.saveLog(httpLog);
```

#### Performance Monitoring

```java
// Find slow requests (> 3 seconds)
List<LogsSystemDTO> slowRequests = logsSystemService.findSlowRequests(
    3000L, 
    LocalDateTime.now().minusDays(1)
);

// Get log statistics by level
Map<String, Long> logStats = logsSystemService.getLogStatsByLevel(
    LocalDateTime.now().minusDays(7),
    LocalDateTime.now()
);
// Result: {"INFO": 1000, "WARN": 50, "ERROR": 10}
```

### Querying System Logs

#### Basic Queries

```java
// Find logs by service and date range
List<LogsSystemDTO> serviceLogs = logsSystemService.findByServiceAndDateRange(
    "user-service",
    LocalDateTime.now().minusDays(7),
    LocalDateTime.now()
);

// Find error logs
Page<LogsSystemDTO> errorLogs = logsSystemService.findErrorLogs(
    LocalDateTime.now().minusDays(1),
    PageRequest.of(0, 20)
);

// Find logs by correlation ID (request tracing)
List<LogsSystemDTO> correlatedLogs = logsSystemService.findByCorrelationId("req-abc-123");
```

#### Advanced Searches

```java
// Multi-criteria search
Page<LogsSystemDTO> searchResults = logsSystemService.searchLogs(
    "user-service",                    // service name
    LogLevel.ERROR.toString(),         // log level
    12345,                            // user ID
    100,                              // organization ID
    "database",                       // search term in message
    LocalDateTime.now().minusDays(7), // start date
    LocalDateTime.now(),              // end date
    PageRequest.of(0, 50)            // pagination
);

// Find by HTTP status range (4xx client errors)
Page<LogsSystemDTO> clientErrors = logsSystemService.findByResponseStatusRange(
    400, 500,
    LocalDateTime.now().minusDays(1),
    PageRequest.of(0, 20)
);
```

## Validation

Both logging services include comprehensive validation:

### User Log Validation

```java
@Valid
public class LogsUserDTO {
    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be positive")
    private Integer userId;
    
    @NotNull(message = "Organization ID is required")
    @Min(value = 1, message = "Organization ID must be positive")
    private Integer organizationId;
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
    
    @Pattern(regexp = "^(LOGIN|LOGOUT|CREATE|UPDATE|DELETE|VIEW|DOWNLOAD|UPLOAD|ERROR|AUDIT)$")
    private String type;
}
```

### System Log Validation

```java
@Valid
public class LogsSystemDTO {
    @NotBlank(message = "Service name is required")
    @Size(max = 100, message = "Service name cannot exceed 100 characters")
    private String serviceName;
    
    @NotNull(message = "Log level is required")
    @ValidLogLevel
    private String logLevel;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @ValidHttpMethod
    private String requestMethod;
    
    @ValidEnvironment
    private String environment;
}
```

## Data Retention

Both services provide cleanup capabilities:

```java
// Delete old logs (recommended for data retention policies)
LocalDateTime cutoffDate = LocalDateTime.now().minusYears(1);

// Clean up user logs
logsUserService.deleteOldLogs(cutoffDate);

// Clean up system logs
logsSystemService.deleteOldLogs(cutoffDate);
```

## Best Practices

### 1. Use Correlation IDs

Always include correlation IDs for request tracing:

```java
String correlationId = UUID.randomUUID().toString();

LogsSystemDTO log = LogsSystemDTO.builder()
    .correlationId(correlationId)
    // ... other fields
    .build();
```

### 2. Include Context

Provide rich context in your logs:

```java
LogsUserDTO userLog = LogsUserDTO.builder()
    .userId(userId)
    .organizationId(orgId)
    .type(UserLogType.UPDATE.getCode())
    .description("Updated profile: changed email from old@example.com to new@example.com")
    .build();
```

### 3. Log Asynchronously for High Volume

For high-volume applications, consider async logging:

```java
@Async
public CompletableFuture<Void> logUserActionAsync(LogsUserDTO log) {
    logsUserService.saveLog(log);
    return CompletableFuture.completedFuture(null);
}
```

### 4. Use Batch Operations

For multiple logs, use batch operations for better performance:

```java
List<LogsUserDTO> logs = // collect multiple logs
logsUserService.saveLogsBatch(logs);
```

### 5. Monitor Log Statistics

Regularly monitor log statistics for insights:

```java
// Weekly error report
Map<String, Long> weeklyErrors = logsSystemService.getLogStatsByLevel(
    LocalDateTime.now().minusDays(7),
    LocalDateTime.now()
);

if (weeklyErrors.getOrDefault("ERROR", 0L) > 100) {
    // Alert operations team
}
```

## Performance Considerations

1. **Indexing**: The entities include database indexes on commonly queried fields
2. **Pagination**: Use pagination for large result sets
3. **Date Ranges**: Always specify reasonable date ranges for queries
4. **Batch Operations**: Use batch operations when saving multiple logs
5. **Async Processing**: Consider async logging for high-throughput applications

## Integration Examples

See the [Examples](examples/) directory for complete integration examples including:
- Spring Boot application setup
- Async logging configuration
- Custom log aggregation
- Monitoring and alerting
- Performance optimization