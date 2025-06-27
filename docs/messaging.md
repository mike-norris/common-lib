# Messaging Guide

The middleware library provides RabbitMQ integration with predefined queues, exchanges, and dead letter handling patterns for reliable message processing.

## Overview

The messaging module includes:
- **Predefined Queues & Exchanges** - Standardized naming conventions
- **Dead Letter Queues (DLQ)** - Automatic failure handling
- **Type-Safe Enums** - Compile-time validation of queue/exchange names
- **Optional Configuration** - Enable only when RabbitMQ is available

## Setup

### 1. Enable RabbitMQ Support

Add RabbitMQ starter to your dependencies (already included if using the library):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### 2. Enable Messaging Configuration

Uncomment the messaging configuration in your application:

```java
// In your main application class or configuration
@EnableRabbit
@SpringBootApplication
public class YourApplication {
    // ...
}
```

### 3. RabbitMQ Connection Properties

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    connection-timeout: 60000
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          multiplier: 2
```

## Queue and Exchange Structure

### Available Queues

The `QueueName` enum defines all available queues:

```java
public enum QueueName {
    // User logging queues
    USER_LOGS("q.logs-user", "Queue for user activity logs"),
    USER_LOGS_DLQ("q.logs-user-dlq", "Dead letter queue for user logs"),
    
    // System logging queues
    SYSTEM_LOGS("q.logs-system", "Queue for system logs"),
    SYSTEM_LOGS_DLQ("q.logs-system-dlq", "Dead letter queue for system logs"),
    
    // Portal user queues
    PORTAL_USER("q.portal-user", "Queue for portal user operations"),
    PORTAL_USER_DLQ("q.portal-user-dlq", "Dead letter queue for portal user operations");
}
```

### Available Exchanges

The `ExchangeName` enum defines all available exchanges:

```java
public enum ExchangeName {
    // Logging exchanges
    LOGGING("x.logging", "Main exchange for logging messages"),
    LOGGING_DLX("x.logging-dlx", "Dead letter exchange for logging messages"),
    
    // User creation exchanges
    CREATE_USER("x.create-user", "Exchange for user creation events"),
    CREATE_USER_DLX("x.create-user-dlx", "Dead letter exchange for user creation events");
}
```

## Usage Examples

### 1. Basic Message Publishing

```java
@Service
public class UserEventPublisher {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishUserCreationEvent(UserCreationEventDTO event) {
        rabbitTemplate.convertAndSend(
            ExchangeName.CREATE_USER.getExchangeName(),
            QueueName.PORTAL_USER.getQueueName(),
            event
        );
    }
    
    public void publishUserLog(LogsUserDTO log) {
        rabbitTemplate.convertAndSend(
            ExchangeName.LOGGING.getExchangeName(),
            QueueName.USER_LOGS.getQueueName(),
            log
        );
    }
}
```

### 2. Message Consumers

```java
@Component
public class UserEventConsumer {
    
    @Autowired
    private PortalUserService portalUserService;
    
    @RabbitListener(queues = "#{T(com.openrangelabs.middleware.messaging.QueueName).PORTAL_USER.queueName}")
    public void handleUserCreationEvent(UserCreationEventDTO event) {
        try {
            // Process the user creation event
            portalUserService.processUserCreationEvent(event);
            
        } catch (Exception e) {
            // Exception will send message to DLQ automatically
            throw new RuntimeException("Failed to process user creation event", e);
        }
    }
    
    @RabbitListener(queues = "#{T(com.openrangelabs.middleware.messaging.QueueName).USER_LOGS.queueName}")
    public void handleUserLog(LogsUserDTO log) {
        try {
            // Process the user log
            processUserLog(log);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to process user log", e);
        }
    }
}
```

### 3. Dead Letter Queue Handling

```java
@Component
public class DeadLetterQueueHandler {
    
    @RabbitListener(queues = "#{T(com.openrangelabs.middleware.messaging.QueueName).PORTAL_USER_DLQ.queueName}")
    public void handlePortalUserDLQ(UserCreationEventDTO event) {
        // Log the failed message
        logger.error("Failed to process user creation event after retries: {}", event);
        
        // Could implement:
        // - Alerting
        // - Manual review queue
        // - Alternative processing logic
        // - Metrics collection
    }
    
    @RabbitListener(queues = "#{T(com.openrangelabs.middleware.messaging.QueueName).USER_LOGS_DLQ.queueName}")
    public void handleUserLogsDLQ(LogsUserDTO log) {
        logger.error("Failed to process user log after retries: {}", log);
        
        // Implement retry logic or alternative handling
    }
}
```

## Data Transfer Objects

### UserCreationEventDTO

For user management events:

```java
UserCreationEventDTO event = UserCreationEventDTO.builder()
    .eventType("USER_CREATED")
    .userId(12345)
    .organizationId(100)
    .username("john.doe")
    .email("john.doe@example.com")
    .firstName("John")
    .lastName("Doe")
    .status("ACTIVE")
    .sourceService("user-service")
    .triggeredBy("admin")
    .additionalData("{\"region\":\"us-east-1\"}")
    .build();
```

### Event Types

```java
// Supported event types
"USER_CREATED"      // New user account created
"USER_UPDATED"      // User information updated
"USER_DELETED"      // User account deleted
"USER_ACTIVATED"    // User account activated
"USER_DEACTIVATED"  // User account deactivated
```

## Advanced Patterns

### 1. Saga Pattern Implementation

```java
@Service
public class UserRegistrationSaga {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void startUserRegistration(UserRegistrationRequest request) {
        // Step 1: Create portal user event
        UserCreationEventDTO createEvent = UserCreationEventDTO.builder()
            .eventType("USER_CREATED")
            .userId(request.getUserId())
            .organizationId(request.getOrganizationId())
            .username(request.getUsername())
            .email(request.getEmail())
            .status("PENDING")
            .sourceService("registration-service")
            .correlationId(UUID.randomUUID().toString())
            .build();
        
        // Publish to start the saga
        rabbitTemplate.convertAndSend(
            ExchangeName.CREATE_USER.getExchangeName(),
            QueueName.PORTAL_USER.getQueueName(),
            createEvent
        );
    }
    
    @RabbitListener(queues = "#{T(com.openrangelabs.middleware.messaging.QueueName).PORTAL_USER.queueName}")
    public void handleUserCreation(UserCreationEventDTO event) {
        if ("USER_CREATED".equals(event.getEventType())) {
            // Step 2: Create user account
            createUserAccount(event);
            
            // Step 3: Send activation email
            sendActivationEmail(event);
            
            // Step 4: Update status to ACTIVE
            UserCreationEventDTO activateEvent = UserCreationEventDTO.builder()
                .eventType("USER_ACTIVATED")
                .userId(event.getUserId())
                .organizationId(event.getOrganizationId())
                .username(event.getUsername())
                .email(event.getEmail())
                .status("ACTIVE")
                .sourceService("registration-service")
                .correlationId(event.getCorrelationId())
                .build();
                
            rabbitTemplate.convertAndSend(
                ExchangeName.CREATE_USER.getExchangeName(),
                QueueName.PORTAL_USER.getQueueName(),
                activateEvent
            );
        }
    }
}
```

### 2. Event Sourcing Pattern

```java
@Component
public class UserEventStore {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private LogsUserService logsUserService;
    
    public void publishAndStore(UserCreationEventDTO event) {
        // Store event in logs for audit trail
        LogsUserDTO auditLog = LogsUserDTO.builder()
            .userId(event.getUserId())
            .organizationId(event.getOrganizationId())
            .type(UserLogType.AUDIT.getCode())
            .description("Event: " + event.getEventType() + " - " + event.getCorrelationId())
            .build();
        
        logsUserService.saveLog(auditLog);
        
        // Publish event to message bus
        rabbitTemplate.convertAndSend(
            ExchangeName.CREATE_USER.getExchangeName(),
            QueueName.PORTAL_USER.getQueueName(),
            event
        );
    }
}
```

### 3. Message Retry Configuration

```java
@Configuration
public class RetryConfiguration {
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        
        // Configure retry policy
        template.setRetryTemplate(retryTemplate());
        
        return template;
    }
    
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000); // 2 seconds
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        return retryTemplate;
    }
}
```

## Queue Management Utilities

### 1. Using Enum Helpers

```java
@Service
public class QueueManagementService {
    
    public void demonstrateQueueOperations() {
        // Get queue name safely
        String userQueue = QueueName.USER_LOGS.getQueueName();
        
        // Check if queue is a DLQ
        boolean isDLQ = QueueName.USER_LOGS_DLQ.isDeadLetterQueue();
        
        // Get corresponding DLQ for a main queue
        QueueName dlq = QueueName.USER_LOGS.getDeadLetterQueue();
        
        // Validate queue names
        boolean isValid = QueueName.isValidQueueName("q.logs-user");
        
        // Get queue from string (throws exception if invalid)
        QueueName queue = QueueName.fromQueueName("q.logs-user");
    }
    
    public void demonstrateExchangeOperations() {
        // Get exchange name safely
        String loggingExchange = ExchangeName.LOGGING.getExchangeName();
        
        // Check if exchange is a DLX
        boolean isDLX = ExchangeName.LOGGING_DLX.isDeadLetterExchange();
        
        // Get corresponding DLX for a main exchange
        ExchangeName dlx = ExchangeName.LOGGING.getDeadLetterExchange();
    }
}
```

### 2. Dynamic Queue Operations

```java
@Service
public class DynamicQueueService {
    
    @Autowired
    private AmqpAdmin amqpAdmin;
    
    public void createQueuesIfNotExists() {
        // Create all queues defined in enum
        for (QueueName queueName : QueueName.values()) {
            Queue queue = QueueBuilder.durable(queueName.getQueueName()).build();
            amqpAdmin.declareQueue(queue);
        }
        
        // Create all exchanges defined in enum
        for (ExchangeName exchangeName : ExchangeName.values()) {
            Exchange exchange = new DirectExchange(exchangeName.getExchangeName());
            amqpAdmin.declareExchange(exchange);
        }
    }
    
    public QueueInformation getQueueInfo(QueueName queueName) {
        Properties queueProperties = amqpAdmin.getQueueProperties(queueName.getQueueName());
        return new QueueInformation(queueProperties);
    }
}
```

## Monitoring and Observability

### 1. Message Metrics

```java
@Component
public class MessageMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter messagesSent;
    private final Counter messagesReceived;
    private final Counter messagesFailed;
    
    public MessageMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.messagesSent = Counter.builder("messages.sent")
            .description("Number of messages sent")
            .register(meterRegistry);
        this.messagesReceived = Counter.builder("messages.received")
            .description("Number of messages received")
            .register(meterRegistry);
        this.messagesFailed = Counter.builder("messages.failed")
            .description("Number of messages failed")
            .register(meterRegistry);
    }
    
    @EventListener
    public void handleMessageSent(MessageSentEvent event) {
        messagesSent.increment(
            Tags.of(
                "queue", event.getQueueName(),
                "exchange", event.getExchangeName()
            )
        );
    }
}
```

### 2. Health Checks

```java
@Component
public class RabbitMQHealthIndicator implements HealthIndicator {
    
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @Override
    public Health health() {
        try {
            Connection connection = connectionFactory.createConnection();
            if (connection.isOpen()) {
                connection.close();
                return Health.up()
                    .withDetail("rabbitmq", "Connection successful")
                    .build();
            } else {
                return Health.down()
                    .withDetail("rabbitmq", "Connection failed")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("rabbitmq", "Connection error: " + e.getMessage())
                .build();
        }
    }
}
```

## Best Practices

### 1. Message Design

```java
// Good: Include correlation ID and metadata
UserCreationEventDTO event = UserCreationEventDTO.builder()
    .eventType("USER_CREATED")
    .userId(12345)
    .correlationId(UUID.randomUUID().toString())
    .eventTimestamp(LocalDateTime.now())
    .sourceService("user-service")
    .additionalData("{\"version\":\"1.0\",\"source\":\"api\"}")
    .build();
```

### 2. Error Handling

```java
@RabbitListener(queues = "#{T(com.openrangelabs.middleware.messaging.QueueName).PORTAL_USER.queueName}")
public void handleUserEvent(UserCreationEventDTO event) {
    try {
        processEvent(event);
    } catch (ValidationException e) {
        // Don't retry for validation errors
        logger.error("Validation error for event {}: {}", event.getEventId(), e.getMessage());
        // Send to error queue or handle appropriately
    } catch (TransientException e) {
        // Let it retry by throwing exception
        throw new RuntimeException("Transient error, will retry", e);
    } catch (Exception e) {
        // Log and decide whether to retry
        logger.error("Unexpected error processing event {}: {}", event.getEventId(), e.getMessage());
        throw e; // Will go to DLQ after retries
    }
}
```

### 3. Message Versioning

```java
// Include version information in messages
UserCreationEventDTO event = UserCreationEventDTO.builder()
    .eventType("USER_CREATED")
    .additionalData("{\"messageVersion\":\"2.0\",\"schemaVersion\":\"1.1\"}")
    .build();

// Handle different versions in consumers
@RabbitListener(queues = "...")
public void handleEvent(UserCreationEventDTO event) {
    String additionalData = event.getAdditionalData();
    if (additionalData != null && additionalData.contains("messageVersion")) {
        // Parse version and handle accordingly
        handleVersionedEvent(event);
    } else {
        // Handle legacy format
        handleLegacyEvent(event);
    }
}
```

### 4. Testing

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672"
})
class MessagingIntegrationTest {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Test
    void testUserEventPublishing() {
        UserCreationEventDTO event = UserCreationEventDTO.builder()
            .eventType("USER_CREATED")
            .userId(12345)
            .organizationId(100)
            .username("test.user")
            .email("test@example.com")
            .build();
        
        // Publish message
        rabbitTemplate.convertAndSend(
            ExchangeName.CREATE_USER.getExchangeName(),
            QueueName.PORTAL_USER.getQueueName(),
            event
        );
        
        // Verify message was processed
        // (Use test containers or embedded RabbitMQ for integration tests)
    }
}
```

## Troubleshooting

### Common Issues

1. **Messages not being consumed**
    - Check queue bindings
    - Verify consumer is running
    - Check for exceptions in logs

2. **Messages going to DLQ immediately**
    - Check message format/validation
    - Verify consumer exception handling
    - Review retry configuration

3. **Connection issues**
    - Verify RabbitMQ is running
    - Check connection properties
    - Review firewall/network settings

### Debugging Commands

```bash
# List queues
rabbitmqctl list_queues

# List exchanges
rabbitmqctl list_exchanges

# List bindings
rabbitmqctl list_bindings

# Purge queue (careful!)
rabbitmqctl purge_queue q.logs-user
```

## Configuration Reference

Complete messaging configuration is optional and can be enabled by uncommenting the `MessagingConfig` class in the library. The configuration includes:

- Automatic queue and exchange creation
- Dead letter queue setup
- TTL and retry configuration
- Bindings between queues and exchanges

For production deployments, consider:
- Connection pooling
- Cluster configuration
- Monitoring and alerting
- Backup and recovery procedures

## Next Steps

- [WebClient Guide](webclient.md) - HTTP client integration
- [Configuration Guide](configuration.md) - Advanced configuration options
- [Examples](examples/) - Complete working examples