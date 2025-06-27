# Getting Started

This guide will help you quickly integrate the OpenRange Labs Middleware Common Library into your Spring Boot application.

## Prerequisites

- Java 17 or higher
- Spring Boot 3.4+
- Maven or Gradle build system
- PostgreSQL database (for logging features)
- RabbitMQ (optional, for messaging features)

## Installation

### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.openrangelabs.middleware</groupId>
    <artifactId>common-lib</artifactId>
    <version>2024.12.7</version>
</dependency>
```

### Gradle

Add the dependency to your `build.gradle`:

```gradle
implementation 'com.openrangelabs.middleware:common-lib:2024.12.7'
```

## Basic Configuration

### 1. Application Properties

Add these properties to your `application.yml` or `application.properties`:

```yaml
# Database configuration (required for logging)
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database
    username: your_username
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

# WebClient configuration
webclient:
  timeout:
    connect: 10s
    read: 30s
    write: 30s
  connection-pool:
    max-connections: 100
    keep-alive: 5m
  user-agent: "YourApp/1.0"
  enable-logging: false

# Domain configuration
domain: https://your-application.com
```

### 2. Enable JPA Repositories

Add the repository scan to your main application class:

```java
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.openrangelabs.middleware")
@EntityScan(basePackages = "com.openrangelabs.middleware")
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 3. Database Setup

The library includes JPA entities that will automatically create the required tables:

- `logs_user` - User activity logs
- `logs_system` - System logs
- `portal_user` - Portal user management events

Tables will be created automatically with `ddl-auto: update`.

## First Steps

### 1. Basic User Logging

```java
@Service
public class UserActivityService {
    
    @Autowired
    private LogsUserService logsUserService;
    
    public void logUserLogin(Integer userId, Integer orgId) {
        LogsUserDTO loginLog = LogsUserDTO.builder()
            .userId(userId)
            .organizationId(orgId)
            .type(UserLogType.LOGIN.getCode())
            .description("User logged in successfully")
            .build();
        
        logsUserService.saveLog(loginLog);
    }
}
```

### 2. System Logging

```java
@Service
public class OrderService {
    
    @Autowired
    private LogsSystemService logsSystemService;
    
    public void processOrder(Order order) {
        try {
            // Process order logic here
            
            LogsSystemDTO successLog = LogsSystemDTO.builder()
                .serviceName("order-service")
                .logLevel(LogLevel.INFO.toString())
                .message("Order processed successfully")
                .userId(order.getUserId())
                .organizationId(order.getOrganizationId())
                .responseStatus(HttpStatus.OK.getCode())
                .build();
                
            logsSystemService.saveLog(successLog);
            
        } catch (Exception e) {
            logsSystemService.logException("order-service", e, 
                order.getUserId(), order.getOrganizationId());
        }
    }
}
```

### 3. Enhanced WebClient Usage

```java
@Service
public class ExternalApiService {
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    public Mono<String> callExternalApi() {
        WebClient client = webClientBuilder
            .baseUrl("https://api.external-service.com")
            .build();
            
        return client.get()
            .uri("/data")
            .retrieve()
            .bodyToMono(String.class)
            .onErrorResume(Exception.class, ex -> {
                // Error handling is built-in
                return Mono.just("Default response");
            });
    }
}
```

## Validation

The library includes custom validation annotations:

```java
@RestController
public class UserController {
    
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest request) {
        // Validation is automatic with proper error responses
        return ResponseEntity.ok().build();
    }
}

public class UserRequest {
    @ValidUserLogType
    private String logType;
    
    @ValidEnvironment
    private String environment;
    
    @ValidLogLevel
    private String logLevel;
}
```

## Error Handling

Global error handling is provided automatically:

```java
// ValidationErrorResponseDTO for validation failures
{
  "message": "Validation failed",
  "errors": {
    "email": "Email is required",
    "username": "Username must be at least 3 characters"
  },
  "timestamp": "2024-12-07T10:30:00",
  "path": "/api/v1/users",
  "status": 400
}

// ErrorResponseDTO for general errors
{
  "message": "User not found",
  "timestamp": "2024-12-07T10:30:00",
  "path": "/api/v1/users/123",
  "status": 404,
  "error": "Not Found"
}
```

## Next Steps

1. **[Logging Guide](logging.md)** - Learn about comprehensive logging features
2. **[Messaging Guide](messaging.md)** - Set up RabbitMQ integration
3. **[WebClient Guide](webclient.md)** - Advanced HTTP client usage
4. **[Configuration Guide](configuration.md)** - Detailed configuration options
5. **[Examples](examples/)** - Explore real-world usage examples

## Common Issues

### Database Connection
If you encounter database connection issues, ensure:
- PostgreSQL is running
- Database credentials are correct
- Database exists and is accessible

### Validation Not Working
If validation annotations aren't working:
- Ensure `@Valid` is used on controller parameters
- Check that Spring Boot validation starter is included
- Verify entity scanning includes the middleware package

### WebClient Timeouts
If experiencing timeout issues:
- Adjust timeout values in configuration
- Check network connectivity
- Review connection pool settings

## Support

For additional help:
- Review the [API Reference](api-reference.md)
- Check [GitHub Issues](https://github.com/openrangelabs/middleware-library-common/issues)
- Join our [Discussions](https://github.com/openrangelabs/middleware-library-common/discussions)