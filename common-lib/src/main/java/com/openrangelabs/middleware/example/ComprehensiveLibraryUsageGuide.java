package com.openrangelabs.middleware.example;

import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.middleware.exception.dto.ErrorResponseDTO;
import com.openrangelabs.middleware.exception.dto.ValidationErrorResponseDTO;
import com.openrangelabs.middleware.logging.dto.LogsSystemDTO;
import com.openrangelabs.middleware.logging.dto.LogsUserDTO;
import com.openrangelabs.middleware.logging.model.LogLevel;
import com.openrangelabs.middleware.logging.model.UserLogType;
import com.openrangelabs.middleware.logging.service.LogsSystemService;
import com.openrangelabs.middleware.logging.service.LogsUserService;
import com.openrangelabs.middleware.messaging.ExchangeName;
import com.openrangelabs.middleware.messaging.QueueName;
import com.openrangelabs.middleware.messaging.dto.UserCreationEventDTO;
import com.openrangelabs.middleware.user.dto.PortalUserDTO;
import com.openrangelabs.middleware.user.service.PortalUserService; // You'll need to create this
import com.openrangelabs.middleware.web.HttpMethod;
import com.openrangelabs.middleware.web.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Comprehensive guide showing how to use all library components together
 */
@Component
public class ComprehensiveLibraryUsageGuide {

    @Autowired
    private LogsUserService logsUserService;

    @Autowired
    private LogsSystemService logsSystemService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    /**
     * Example 1: Complete user workflow with logging
     */
    public void completeUserWorkflow() {
        try {
            // 1. Create a portal user
            PortalUserDTO newUser = PortalUserDTO.builder()
                    .userId(12345)
                    .organizationId(100)
                    .username("john.doe")
                    .email("john.doe@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .operationType("CREATE")
                    .status("PENDING")
                    .createdBy("admin")
                    .build();

            // 2. Log the user creation activity
            LogsUserDTO userLog = LogsUserDTO.builder()
                    .userId(12345)
                    .organizationId(100)
                    .type(UserLogType.CREATE.getCode())
                    .description("User account created via portal")
                    .build();

            logsUserService.saveLog(userLog);

            // 3. Create messaging event for other services
            UserCreationEventDTO event = UserCreationEventDTO.builder()
                    .eventType("USER_CREATED")
                    .userId(12345)
                    .organizationId(100)
                    .username("john.doe")
                    .email("john.doe@example.com")
                    .status("PENDING")
                    .sourceService("user-management")
                    .triggeredBy("admin")
                    .build();

            // 4. Log system event
            LogsSystemDTO systemLog = LogsSystemDTO.builder()
                    .serviceName("user-service")
                    .logLevel(LogLevel.INFO.toString())
                    .message("User creation workflow completed successfully")
                    .userId(12345)
                    .organizationId(100)
                    .requestMethod(HttpMethod.POST.getMethod())
                    .responseStatus(HttpStatus.CREATED.getCode())
                    .correlationId(event.getCorrelationId())
                    .build();

            logsSystemService.saveLog(systemLog);

            System.out.println("✅ User workflow completed successfully");
            System.out.println("📤 Event would be sent to: " + QueueName.PORTAL_USER.getQueueName());
            System.out.println("🔄 Via exchange: " + ExchangeName.CREATE_USER.getExchangeName());

        } catch (Exception e) {
            handleWorkflowError(e);
        }
    }

    /**
     * Example 2: Enhanced WebClient integration with logging
     */
    public Mono<String> enhancedApiCallWithLogging() {
        String correlationId = java.util.UUID.randomUUID().toString();

        WebClient client = webClientBuilder
                .baseUrl("https://api.external-service.com")
                .defaultHeader("X-Correlation-ID", correlationId)
                .build();

        // Log the outgoing request
        LogsSystemDTO requestLog = LogsSystemDTO.builder()
                .serviceName("integration-service")
                .logLevel(LogLevel.INFO.toString())
                .message("Making external API call")
                .requestUri("/external-api/data")
                .requestMethod(HttpMethod.GET.getMethod())
                .correlationId(correlationId)
                .build();

        logsSystemService.saveLog(requestLog);

        return client.get()
                .uri("/data")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    // Log successful response
                    LogsSystemDTO responseLog = LogsSystemDTO.builder()
                            .serviceName("integration-service")
                            .logLevel(LogLevel.INFO.toString())
                            .message("External API call completed successfully")
                            .responseStatus(HttpStatus.OK.getCode())
                            .correlationId(correlationId)
                            .executionTimeMs(calculateExecutionTime()) // Implement this method
                            .build();

                    logsSystemService.saveLog(responseLog);
                    return "API call successful: " + response.toString();
                })
                .onErrorResume(WebClientConfig.WebClientClientException.class, ex -> {
                    // Log client errors (4xx)
                    LogsSystemDTO errorLog = LogsSystemDTO.builder()
                            .serviceName("integration-service")
                            .logLevel(LogLevel.WARN.toString())
                            .message("External API returned client error: " + ex.getMessage())
                            .responseStatus(ex.getStatusCode().value())
                            .correlationId(correlationId)
                            .build();

                    logsSystemService.saveLog(errorLog);
                    return Mono.just("Client error occurred");
                })
                .onErrorResume(WebClientConfig.WebClientServerException.class, ex -> {
                    // Log server errors (5xx)
                    LogsSystemDTO errorLog = LogsSystemDTO.builder()
                            .serviceName("integration-service")
                            .logLevel(LogLevel.ERROR.toString())
                            .message("External API server error: " + ex.getMessage())
                            .responseStatus(ex.getStatusCode().value())
                            .stackTrace(getStackTrace(ex))
                            .correlationId(correlationId)
                            .build();

                    logsSystemService.saveLog(errorLog);
                    return Mono.just("Server error occurred");
                });
    }

    /**
     * Example 3: Error handling with standardized responses
     */
    public void demonstrateErrorHandling() {
        try {
            // Simulate validation error
            ValidationErrorResponseDTO validationError = ValidationErrorResponseDTO.builder()
                    .message("User creation failed")
                    .errors(Map.of(
                            "email", "Email is required",
                            "username", "Username must be at least 3 characters"
                    ))
                    .status(400)
                    .path("/api/v1/users")
                    .build();

            // Log the validation error
            LogsSystemDTO validationLog = LogsSystemDTO.builder()
                    .serviceName("user-service")
                    .logLevel(LogLevel.WARN.toString())
                    .message("User validation failed: " + validationError.getMessage())
                    .responseStatus(400)
                    .build();

            logsSystemService.saveLog(validationLog);

        } catch (Exception e) {
            // Create general error response
            ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                    .message("Unexpected error occurred")
                    .status(500)
                    .error("Internal Server Error")
                    .trace(getStackTrace(e))
                    .build();

            // Log the unexpected error
            logsSystemService.logException("user-service", e, null, null);
        }
    }

    /**
     * Example 4: Using enums for type safety
     */
    public void demonstrateEnumUsage() {
        // Use log level enum
        LogLevel currentLevel = LogLevel.INFO;
        if (currentLevel.isEnabledFor(LogLevel.DEBUG)) {
            System.out.println("Debug logging is enabled");
        }

        // Use HTTP method enum
        HttpMethod method = HttpMethod.fromString("POST");
        if (method.hasRequestBody()) {
            System.out.println("This method can have a request body");
        }

        // Use HTTP status enum
        HttpStatus status = HttpStatus.fromCode(201);
        if (status.isSuccess()) {
            System.out.println("Request was successful: " + status.getDescription());
        }

        // Use queue and exchange names
        String userQueue = QueueName.USER_LOGS.getQueueName();
        String portalQueue = QueueName.PORTAL_USER.getQueueName();
        boolean isDLQ = QueueName.USER_LOGS_DLQ.isDeadLetterQueue();

        System.out.println("User logs queue: " + userQueue);
        System.out.println("Portal queue: " + portalQueue);
        System.out.println("Is DLQ: " + isDLQ);
    }

    /**
     * Example 5: Batch operations and reporting
     */
    public void batchOperationsAndReporting() {
        // Get log statistics for reporting
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        Map<String, Long> logStats = logsSystemService.getLogStatsByLevel(startDate, endDate);

        System.out.println("=== Weekly Log Report ===");
        logStats.forEach((level, count) ->
                System.out.println(level + ": " + count + " entries"));

        // Find user activity for the week
        var userLogs = logsUserService.findByOrganizationId(100, startDate, false);
        System.out.println("User activities this week: " + userLogs.size());

        // Find system errors
        var errorLogs = logsSystemService.findErrorLogs(
                startDate,
                org.springframework.data.domain.PageRequest.of(0, 10)
        );
        System.out.println("System errors this week: " + errorLogs.getTotalElements());

        // Find slow requests (> 5 seconds)
        var slowRequests = logsSystemService.findSlowRequests(5000L, startDate);
        System.out.println("Slow requests this week: " + slowRequests.size());
    }

    /**
     * Example 6: Configuration-driven behavior
     */
    public void configurationDrivenExample() {
        // This method would use the application properties to behave differently
        // in different environments

        // Example properties that could be used:
        // webclient.timeout.connect=10s (dev) vs 15s (prod)
        // webclient.enable-logging=true (dev) vs false (prod)

        WebClient client = webClientBuilder
                .baseUrl("https://api.example.com")
                .build();

        // The WebClient is automatically configured based on properties
        client.get()
                .uri("/health")
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> System.out.println("Health check: " + response));
    }

    // Helper methods
    private void handleWorkflowError(Exception e) {
        LogsSystemDTO errorLog = LogsSystemDTO.builder()
                .serviceName("user-service")
                .logLevel(LogLevel.ERROR.toString())
                .message("User workflow failed: " + e.getMessage())
                .stackTrace(getStackTrace(e))
                .build();

        logsSystemService.saveLog(errorLog);
        System.err.println("❌ User workflow failed: " + e.getMessage());
    }

    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(sw));
        return sw.toString();
    }

    private Long calculateExecutionTime() {
        // In a real application, you'd measure this properly
        return System.currentTimeMillis() % 1000;
    }
}

/**
 * Application.properties configuration example
 */
/*
# Domain configuration
domain=https://your-application.com

# WebClient configuration
webclient.timeout.connect=10s
webclient.timeout.read=30s
webclient.timeout.write=30s
webclient.connection-pool.max-connections=100
webclient.connection-pool.keep-alive=5m
webclient.user-agent=YourApp/1.0
webclient.enable-logging=false

# Environment-specific overrides
# For development:
# webclient.timeout.connect=5s
# webclient.enable-logging=true

# For production:
# webclient.timeout.connect=15s
# webclient.connection-pool.max-connections=200
# webclient.enable-logging=false
*/