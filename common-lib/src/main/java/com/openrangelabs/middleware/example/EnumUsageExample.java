package com.openrangelabs.middleware.example;

import com.openrangelabs.middleware.config.Environment;
import com.openrangelabs.middleware.logging.dto.LogsSystemDTO;
import com.openrangelabs.middleware.logging.dto.LogsUserDTO;
import com.openrangelabs.middleware.logging.model.LogLevel;
import com.openrangelabs.middleware.logging.model.UserLogType;
import com.openrangelabs.middleware.messaging.ExchangeName;
import com.openrangelabs.middleware.messaging.QueueName;
import com.openrangelabs.middleware.web.HttpMethod;
import com.openrangelabs.middleware.web.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Example demonstrating the use of enums for constants
 * Note: This example shows usage patterns. RabbitMQ dependencies are optional.
 */
@Component
public class EnumUsageExample {

    /**
     * Example of using queue and exchange enums
     */
    public void demonstrateMessagingEnums() {
        // Get queue and exchange names using enums
        String userLogsQueue = QueueName.USER_LOGS.getQueueName();
        String loggingExchange = ExchangeName.LOGGING.getExchangeName();

        System.out.println("User logs queue: " + userLogsQueue);
        System.out.println("Logging exchange: " + loggingExchange);

        // Check if a queue is a dead letter queue
        if (QueueName.USER_LOGS_DLQ.isDeadLetterQueue()) {
            System.out.println("This is a dead letter queue");
        }

        // Get the DLQ for a main queue
        QueueName dlq = QueueName.USER_LOGS.getDeadLetterQueue();
        System.out.println("DLQ for user logs: " + dlq.getQueueName());
    }

    /**
     * Example of creating log DTOs with enums
     */
    public LogsUserDTO createUserLog() {
        return LogsUserDTO.builder()
                .userId(12345)
                .organizationId(100)
                .type(UserLogType.LOGIN.getCode()) // Using enum
                .description("User login from mobile app")
                .build();
    }

    /**
     * Example of creating system log with enums
     */
    public LogsSystemDTO createSystemLog() {
        return LogsSystemDTO.builder()
                .serviceName("api-gateway")
                .logLevel(LogLevel.INFO.toString()) // Using enum
                .message("Request processed successfully")
                .requestMethod(HttpMethod.POST.getMethod()) // Using enum
                .responseStatus(HttpStatus.CREATED.getCode()) // Using enum
                .environment(Environment.PRODUCTION.getName()) // Using enum
                .build();
    }

    /**
     * Example of processing user logs by type
     */
    public void processUserLog(LogsUserDTO log) {
        // Validate and process using enum
        UserLogType logType = UserLogType.fromCode(log.getType());

        switch (logType) {
            case LOGIN:
                System.out.println("User " + log.getUserId() + " logged in");
                break;
            case LOGOUT:
                System.out.println("User " + log.getUserId() + " logged out");
                break;
            case ERROR:
                System.out.println("Error for user " + log.getUserId() + ": " + log.getDescription());
                break;
            default:
                System.out.println("User action: " + logType.getDescription());
        }
    }

    /**
     * Example of using HTTP enums for request/response handling
     */
    public void handleHttpRequest(String method, int statusCode) {
        // Parse and validate HTTP method
        HttpMethod httpMethod = HttpMethod.fromString(method);

        if (httpMethod.isIdempotent()) {
            System.out.println("Request is idempotent, can be retried safely");
        }

        if (httpMethod.hasRequestBody()) {
            System.out.println("Expecting request body for " + httpMethod);
        }

        // Parse and validate response status
        try {
            HttpStatus status = HttpStatus.fromCode(statusCode);

            if (status.isSuccess()) {
                System.out.println("Request successful: " + status);
            } else if (status.isClientError()) {
                System.out.println("Client error: " + status.getDescription());
            } else if (status.isServerError()) {
                System.out.println("Server error: " + status.getDescription());
                // Maybe trigger retry logic
            }

            System.out.println("Status category: " + status.getCategory());
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown status code: " + statusCode);
        }
    }

    /**
     * Example of environment-specific logic
     */
    public void configureForEnvironment(String envName) {
        Environment env = Environment.fromString(envName);

        if (env.isProductionLike()) {
            System.out.println("Applying production configurations");
            // Enable strict security, monitoring, etc.
        } else if (env.isDevelopmentLike()) {
            System.out.println("Applying development configurations");
            // Enable debug logging, relaxed security, etc.
        }

        System.out.println("Environment: " + env.getName() + " (" + env.getShortName() + ")");
    }

    /**
     * Example of log level filtering
     */
    public void filterLogsByLevel(String minLevelStr) {
        LogLevel minLevel = LogLevel.fromString(minLevelStr);

        // Example logs to filter
        LogLevel[] logLevels = {
                LogLevel.TRACE,
                LogLevel.DEBUG,
                LogLevel.INFO,
                LogLevel.WARN,
                LogLevel.ERROR
        };

        System.out.println("Filtering logs with minimum level: " + minLevel);

        for (LogLevel level : logLevels) {
            if (level.isEnabledFor(minLevel)) {
                System.out.println("✓ " + level + " is enabled");
            } else {
                System.out.println("✗ " + level + " is filtered out");
            }
        }
    }

    /**
     * Example of dead letter queue handling
     */
    public void handleDeadLetterQueue(String queueName) {
        QueueName queue = QueueName.fromQueueName(queueName);

        if (queue.isDeadLetterQueue()) {
            System.out.println("Processing dead letter queue: " + queue.getDescription());
            // Implement retry logic or alerting
        } else {
            QueueName dlq = queue.getDeadLetterQueue();
            if (dlq != null) {
                System.out.println("Main queue: " + queue.getQueueName());
                System.out.println("DLQ: " + dlq.getQueueName());
            }
        }
    }

    /**
     * Example of comprehensive logging with all enums
     */
    public LogsSystemDTO createComprehensiveLog() {
        return LogsSystemDTO.builder()
                .serviceName("user-service")
                .hostName("prod-server-01")
                .logLevel(LogLevel.ERROR.toString())
                .message("Failed to process user request")
                .requestMethod(HttpMethod.PUT.getMethod())
                .responseStatus(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                .environment(Environment.PRODUCTION.getName())
                .executionTimeMs(1500L)
                .build();
    }

    /**
     * Example of using the ORLCommon class with enums
     */
    public void demonstrateORLCommon() {
        // The ORLCommon class provides backward compatibility
        String userLogsQueue = com.openrangelabs.middleware.ORLCommon.USER_LOGS_QUEUE;
        String loggingExchange = com.openrangelabs.middleware.ORLCommon.LOGGING_EXCHANGE;

        // It also provides helper methods
        QueueName queue = com.openrangelabs.middleware.ORLCommon.getQueue(userLogsQueue);
        ExchangeName exchange = com.openrangelabs.middleware.ORLCommon.getExchange(loggingExchange);

        System.out.println("Queue enum: " + queue);
        System.out.println("Exchange enum: " + exchange);
    }
}