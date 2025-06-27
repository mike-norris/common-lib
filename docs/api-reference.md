# API Reference

Complete API reference for the OpenRange Labs Middleware Common Library.

## Core Packages

- [Logging](#logging) - User and system logging functionality
- [Messaging](#messaging) - RabbitMQ integration
- [Web](#web) - HTTP utilities and WebClient configuration
- [Validation](#validation) - Custom validation annotations
- [User Management](#user-management) - Portal user management
- [Configuration](#configuration) - Application configuration
- [Exception Handling](#exception-handling) - Error response DTOs

---

## Logging

### LogsUserService

**Package:** `com.openrangelabs.middleware.logging.service`

#### Methods

```java
public LogsUserDTO saveLog(@Valid @NotNull LogsUserDTO dto)
```
Save a single user log entry.

**Parameters:**
- `dto` - The log entry to save

**Returns:** The saved log entry

**Throws:** `RuntimeException` if validation fails or save operation fails

---

```java
public List<LogsUserDTO> saveLogsBatch(@Valid List<LogsUserDTO> logs)
```
Save multiple log entries in batch.

**Parameters:**
- `logs` - List of log entries to save

**Returns:** List of saved log entries

---

```java
public Optional<LogsUserDTO> findById(@NotNull Integer userId, @NotNull LocalDateTime createdDt)
```
Find a specific log by composite key.

**Parameters:**
- `userId` - The user ID
- `createdDt` - The creation timestamp

**Returns:** Optional containing the log if found

---

```java
public List<LogsUserDTO> findByUserId(@NotNull Integer userId, LocalDateTime dateLimit, boolean ascending)
```
Find all logs for a specific user with date limit.

**Parameters:**
- `userId` - The user ID
- `dateLimit` - Date limit (logs after this date). If null, defaults to 1 year ago
- `ascending` - true for ascending order, false for descending

**Returns:** List of user logs

---

```java
public LogsUserDTO logLogin(@NotNull Integer userId, @NotNull Integer organizationId, String description)
```
Convenience method to log user login.

**Parameters:**
- `userId` - The user ID
- `organizationId` - The organization ID
- `description` - Optional description (e.g., IP address, browser)

**Returns:** The created log entry

### LogsSystemService

**Package:** `com.openrangelabs.middleware.logging.service`

#### Methods

```java
public LogsSystemDTO saveLog(@Valid @NotNull LogsSystemDTO dto)
```
Save a system log entry.

**Parameters:**
- `dto` - The log entry to save

**Returns:** The saved log entry

---

```java
public Page<LogsSystemDTO> searchLogs(String serviceName, String logLevel, Integer userId, Integer organizationId, String searchTerm, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)
```
Search logs with multiple criteria.

**Parameters:**
- `serviceName` - Optional service name filter
- `logLevel` - Optional log level filter
- `userId` - Optional user ID filter
- `organizationId` - Optional organization ID filter
- `searchTerm` - Optional search term for message content
- `startDate` - Start date for the search
- `endDate` - End date for the search
- `pageable` - Pagination information

**Returns:** Page of matching logs

---

```java
public LogsSystemDTO logException(String serviceName, Exception exception, Integer userId, Integer organizationId)
```
Create a log entry for an exception.

**Parameters:**
- `serviceName` - The service name
- `exception` - The exception to log
- `userId` - Optional user ID
- `organizationId` - Optional organization ID

**Returns:** The created log entry

### LogsUserDTO

**Package:** `com.openrangelabs.middleware.logging.dto`

#### Builder Pattern

```java
LogsUserDTO log = LogsUserDTO.builder()
    .userId(12345)
    .organizationId(100)
    .type(UserLogType.LOGIN.getCode())
    .description("User logged in successfully")
    .build();
```

#### Properties

| Property | Type | Required | Validation | Description |
|----------|------|----------|------------|-------------|
| `userId` | `Integer` | Yes | `@Min(1)` | User ID |
| `organizationId` | `Integer` | Yes | `@Min(1)` | Organization ID |
| `createdDt` | `LocalDateTime` | Yes | `@PastOrPresent` | Creation timestamp |
| `type` | `String` | No | `@Pattern` | Log type (LOGIN, LOGOUT, etc.) |
| `description` | `String` | No | `@Size(max=255)` | Log description |

### LogsSystemDTO

**Package:** `com.openrangelabs.middleware.logging.dto`

#### Builder Pattern

```java
LogsSystemDTO log = LogsSystemDTO.builder()
    .serviceName("user-service")
    .logLevel(LogLevel.INFO.toString())
    .message("Request processed successfully")
    .userId(12345)
    .requestMethod(HttpMethod.POST.getMethod())
    .responseStatus(HttpStatus.OK.getCode())
    .build();
```

#### Properties

| Property | Type | Required | Validation | Description |
|----------|------|----------|------------|-------------|
| `serviceName` | `String` | Yes | `@NotBlank @Size(max=100)` | Service name |
| `logLevel` | `String` | Yes | `@ValidLogLevel` | Log level |
| `message` | `String` | Yes | `@NotBlank` | Log message |
| `userId` | `Integer` | No | `@Min(1)` | User ID |
| `organizationId` | `Integer` | No | `@Min(1)` | Organization ID |
| `requestMethod` | `String` | No | `@ValidHttpMethod` | HTTP method |
| `responseStatus` | `Integer` | No | `@Min(100) @Max(599)` | HTTP status code |
| `executionTimeMs` | `Long` | No | `@Min(0)` | Execution time in milliseconds |
| `correlationId` | `String` | No | `@Size(max=50)` | Correlation ID for tracing |

---

## Messaging

### QueueName Enum

**Package:** `com.openrangelabs.middleware.messaging`

#### Constants

```java
USER_LOGS("q.logs-user")
USER_LOGS_DLQ("q.logs-user-dlq")
SYSTEM_LOGS("q.logs-system")
SYSTEM_LOGS_DLQ("q.logs-system-dlq")
PORTAL_USER("q.portal-user")
PORTAL_USER_DLQ("q.portal-user-dlq")
```

#### Methods

```java
public String getQueueName()
```
Get the queue name string.

---

```java
public static QueueName fromQueueName(String queueName)
```
Get QueueName enum from string representation.

**Throws:** `IllegalArgumentException` if invalid

---

```java
public QueueName getDeadLetterQueue()
```
Get the corresponding dead letter queue.

**Returns:** DLQ enum value or null if this is already a DLQ

---

```java
public boolean isDeadLetterQueue()
```
Check if this queue is a dead letter queue.

### ExchangeName Enum

**Package:** `com.openrangelabs.middleware.messaging`

#### Constants

```java
LOGGING("x.logging")
LOGGING_DLX("x.logging-dlx") 
CREATE_USER("x.create-user")
CREATE_USER_DLX("x.create-user-dlx")
```

#### Methods

```java
public String getExchangeName()
```
Get the exchange name string.

---

```java
public static ExchangeName fromExchangeName(String exchangeName)
```
Get ExchangeName enum from string representation.

---

```java
public ExchangeName getDeadLetterExchange()
```
Get the corresponding dead letter exchange.

---

```java
public boolean isDeadLetterExchange()
```
Check if this exchange is a dead letter exchange.

### UserCreationEventDTO

**Package:** `com.openrangelabs.middleware.messaging.dto`

#### Builder Pattern

```java
UserCreationEventDTO event = UserCreationEventDTO.builder()
    .eventType("USER_CREATED")
    .userId(12345)
    .organizationId(100)
    .username("john.doe")
    .email("john.doe@example.com")
    .status("ACTIVE")
    .sourceService("user-service")
    .build();
```

#### Properties

| Property | Type | Required | Validation | Description |
|----------|------|----------|------------|-------------|
| `eventId` | `String` | Yes | Auto-generated UUID | Unique event identifier |
| `eventType` | `String` | Yes | Pattern validation | Event type (USER_CREATED, etc.) |
| `userId` | `Integer` | Yes | `@Min(1)` | User ID |
| `organizationId` | `Integer` | Yes | `@Min(1)` | Organization ID |
| `username` | `String` | Yes | `@NotBlank @Size(max=100)` | Username |
| `email` | `String` | Yes | `@Email @Size(max=255)` | Email address |
| `eventTimestamp` | `LocalDateTime` | Yes | Auto-generated | Event timestamp |
| `correlationId` | `String` | Yes | Auto-generated UUID | Correlation ID |

---

## Web

### HttpMethod Enum

**Package:** `com.openrangelabs.middleware.web`

#### Constants

```java
GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE, CONNECT
```

#### Methods

```java
public static HttpMethod fromString(String method)
```
Get HttpMethod from string representation.

---

```java
public boolean isIdempotent()
```
Check if this method is idempotent.

---

```java
public boolean hasRequestBody()
```
Check if this method typically has a request body.

### HttpStatus Enum

**Package:** `com.openrangelabs.middleware.web`

#### Common Constants

```java
OK(200, "OK")
CREATED(201, "Created")
BAD_REQUEST(400, "Bad Request")
UNAUTHORIZED(401, "Unauthorized")
NOT_FOUND(404, "Not Found")
INTERNAL_SERVER_ERROR(500, "Internal Server Error")
```

#### Methods

```java
public static HttpStatus fromCode(int code)
```
Get HttpStatus from status code.

---

```java
public boolean isSuccess()
```
Check if this is a success status (2xx).

---

```java
public boolean isClientError()
```
Check if this is a client error status (4xx).

---

```java
public boolean isServerError()
```
Check if this is a server error status (5xx).

### WebClientConfig

**Package:** `com.openrangelabs.middleware.config`

#### Static Methods

```java
public static WebClient build(String baseUrl)
```
Create a WebClient with base URL using default configuration.

---

```java
public static WebClient build(URL baseUrl)
```
Create a WebClient with base URL object.

#### Bean Methods

```java
@Bean
public WebClient.Builder webClientBuilder()
```
Configured WebClient builder with enhanced features:
- Connection pooling
- Timeout management
- Error handling
- Security headers
- Optional logging

---

## Validation

### Custom Validation Annotations

**Package:** `com.openrangelabs.middleware.validation`

#### @ValidLogLevel

Validates log level strings.

```java
@ValidLogLevel
private String logLevel;
```

**Valid values:** TRACE, DEBUG, INFO, WARN, ERROR, FATAL

#### @ValidUserLogType

Validates user log type strings.

```java
@ValidUserLogType
private String type;
```

**Valid values:** LOGIN, LOGOUT, CREATE, UPDATE, DELETE, VIEW, DOWNLOAD, UPLOAD, ERROR, AUDIT

#### @ValidHttpMethod

Validates HTTP method strings.

```java
@ValidHttpMethod
private String method;
```

**Valid values:** GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE, CONNECT

#### @ValidEnvironment

Validates environment strings.

```java
@ValidEnvironment
private String environment;
```

**Valid values:** development/dev, testing/test, staging/stage, production/prod

---

## User Management

### PortalUserService

**Package:** `com.openrangelabs.middleware.user.service`

Currently a placeholder service for portal user operations.

### PortalUserDTO

**Package:** `com.openrangelabs.middleware.user.dto`

#### Builder Pattern

```java
PortalUserDTO user = PortalUserDTO.builder()
    .userId(12345)
    .organizationId(100)
    .username("john.doe")
    .email("john.doe@example.com")
    .operationType("CREATE")
    .status("PENDING")
    .build();
```

#### Properties

| Property | Type | Required | Validation | Description |
|----------|------|----------|------------|-------------|
| `userId` | `Integer` | Yes | `@Min(1)` | User ID |
| `organizationId` | `Integer` | Yes | `@Min(1)` | Organization ID |
| `username` | `String` | Yes | `@NotBlank @Size(max=100)` | Username |
| `email` | `String` | Yes | `@Email @Size(max=255)` | Email address |
| `operationType` | `String` | Yes | Pattern validation | Operation type |
| `status` | `String` | Yes | Pattern validation | User status |

### PortalUserRepository

**Package:** `com.openrangelabs.middleware.user.repository`

#### Key Methods

```java
Optional<PortalUser> findByUserId(Integer userId)
```

```java
List<PortalUser> findByOrganizationId(Integer organizationId)
```

```java
Page<PortalUser> searchPortalUsers(Integer organizationId, String status, String operationType, String username, String email, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)
```

---

## Configuration

### Environment Enum

**Package:** `com.openrangelabs.middleware.config`

#### Constants

```java
DEVELOPMENT("development", "dev")
TESTING("testing", "test")
STAGING("staging", "stage")
PRODUCTION("production", "prod")
```

#### Methods

```java
public static Environment fromString(String name)
```
Get Environment from string (accepts both full and short names).

---

```java
public boolean isProductionLike()
```
Check if this is production or staging.

---

```java
public boolean isDevelopmentLike()
```
Check if this is development or testing.

---

## Exception Handling

### ErrorResponseDTO

**Package:** `com.openrangelabs.middleware.exception.dto`

#### Builder Pattern

```java
ErrorResponseDTO error = ErrorResponseDTO.builder()
    .message("User not found")
    .status(404)
    .error("Not Found")
    .path("/api/v1/users/123")
    .build();
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `message` | `String` | Error message |
| `timestamp` | `LocalDateTime` | Error timestamp (auto-generated) |
| `status` | `Integer` | HTTP status code |
| `error` | `String` | Error type description |
| `path` | `String` | Request path that caused error |
| `trace` | `String` | Stack trace (optional) |

### ValidationErrorResponseDTO

**Package:** `com.openrangelabs.middleware.exception.dto`

#### Builder Pattern

```java
ValidationErrorResponseDTO error = ValidationErrorResponseDTO.builder()
    .message("Validation failed")
    .errors(Map.of("email", "Email is required"))
    .status(400)
    .path("/api/v1/users")
    .build();
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `message` | `String` | General error message |
| `errors` | `Map<String, String>` | Field-specific error messages |
| `timestamp` | `LocalDateTime` | Error timestamp (auto-generated) |
| `status` | `Integer` | HTTP status code |
| `path` | `String` | Request path that caused error |

---

## Common Enums

### LogLevel

**Package:** `com.openrangelabs.middleware.logging.model`

```java
TRACE(0), DEBUG(1), INFO(2), WARN(3), ERROR(4), FATAL(5)
```

#### Methods

```java
public boolean isEnabledFor(LogLevel minimumLevel)
```
Check if this level is enabled for the given minimum level.

### UserLogType

**Package:** `com.openrangelabs.middleware.logging.model`

```java
LOGIN, LOGOUT, CREATE, UPDATE, DELETE, VIEW, DOWNLOAD, UPLOAD, ERROR, AUDIT
```

#### Methods

```java
public static UserLogType fromCode(String code)
```
Get UserLogType from string code (case insensitive).

---

```java
public static boolean isValidCode(String code)
```
Check if a string is a valid log type code.

---

## Utility Classes

### ORLCommon

**Package:** `com.openrangelabs.middleware`

Provides backward compatibility and utility methods.

#### Constants

```java
public static final String USER_LOGS_QUEUE = "q.logs-user";
public static final String LOGGING_EXCHANGE = "x.logging";
// ... other constants
```

#### Helper Methods

```java
public static QueueName getQueue(String queue)
```
Get QueueName enum for a given queue string.

---

```java
public static ExchangeName getExchange(String exchange)  
```
Get ExchangeName enum for a given exchange string.

---

```java
public static boolean isDeadLetterQueue(String queue)
```
Check if a queue name represents a dead letter queue.

---

## Exception Types

### Custom WebClient Exceptions

**Package:** `com.openrangelabs.middleware.config.WebClientConfig`

#### WebClientClientException

Thrown for 4xx HTTP client errors.

```java
public HttpStatusCode getStatusCode()
```

#### WebClientServerException

Thrown for 5xx HTTP server errors.

```java
public HttpStatusCode getStatusCode()
```

### ORLException

**Package:** `com.openrangelabs.middleware.logging`

Base exception for library operations.

```java
public ORLException(String message)
public ORLException(String message, Exception e)
```

---

## Usage Examples

### Complete Service Integration

```java
@Service
public class UserManagementService {
    
    @Autowired
    private LogsUserService logsUserService;
    
    @Autowired
    private LogsSystemService logsSystemService;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    public Mono<UserResponse> createUser(CreateUserRequest request) {
        String correlationId = UUID.randomUUID().toString();
        
        // Log user action
        LogsUserDTO userLog = LogsUserDTO.builder()
            .userId(request.getUserId())
            .organizationId(request.getOrganizationId())
            .type(UserLogType.CREATE.getCode())
            .description("User creation initiated")
            .build();
        
        logsUserService.saveLog(userLog);
        
        // Make external API call
        WebClient client = webClientBuilder
            .baseUrl("https://api.user-service.com")
            .build();
            
        return client.post()
            .uri("/users")
            .header("X-Correlation-ID", correlationId)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(UserResponse.class)
            .doOnSuccess(response -> {
                // Log successful creation
                LogsSystemDTO systemLog = LogsSystemDTO.builder()
                    .serviceName("user-management")
                    .logLevel(LogLevel.INFO.toString())
                    .message("User created successfully")
                    .userId(request.getUserId())
                    .correlationId(correlationId)
                    .responseStatus(HttpStatus.CREATED.getCode())
                    .build();
                    
                logsSystemService.saveLog(systemLog);
            })
            .doOnError(error -> {
                // Log error
                logsSystemService.logException("user-management", 
                    (Exception) error, request.getUserId(), request.getOrganizationId());
            });
    }
}
```

This API reference covers all public interfaces and methods available in the middleware library. For additional examples and detailed usage patterns, see the [Examples](examples/) directory.