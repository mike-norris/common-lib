---
layout: default
title: Microservice Integration
parent: Examples
nav_order: 1
---

# Microservice Integration Example

This example demonstrates how to integrate the middleware library into a complete microservice architecture with user management, order processing, and external API integration.

## Project Structure

```
user-service/
├── src/main/java/com/example/userservice/
│   ├── UserServiceApplication.java
│   ├── config/
│   │   └── ServiceConfiguration.java
│   ├── controller/
│   │   └── UserController.java
│   ├── service/
│   │   ├── UserService.java
│   │   └── NotificationService.java
│   └── dto/
│       ├── CreateUserRequest.java
│       └── UserResponse.java
└── src/main/resources/
    ├── application.yml
    ├── application-development.yml
    └── application-production.yml
```

## Main Application

```java
package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
    "com.openrangelabs.middleware",
    "com.example.userservice.repository"
})
@EntityScan(basePackages = {
    "com.openrangelabs.middleware",
    "com.example.userservice.entity"
})
@EnableRabbit
@EnableAsync
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

## Configuration

### Application Configuration

```java
package com.example.userservice.config;

import com.openrangelabs.middleware.messaging.ExchangeName;
import com.openrangelabs.middleware.messaging.QueueName;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceConfiguration {
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}
```

### Application Properties

```yaml
# application.yml
spring:
  application:
    name: user-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:development}
    
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:userservice}
    username: ${DB_USERNAME:userservice}
    password: ${DB_PASSWORD:password}
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:guest}
    password: ${RABBITMQ_PASSWORD:guest}

# WebClient configuration
webclient:
  timeout:
    connect: 10s
    read: 30s
  connection-pool:
    max-connections: 100
  user-agent: "UserService/1.0"

# Domain for security headers
domain: https://api.yourcompany.com

# External service URLs
external-services:
  notification-service: ${NOTIFICATION_SERVICE_URL:http://localhost:8081}
  payment-service: ${PAYMENT_SERVICE_URL:http://localhost:8082}
  
# Logging
logging:
  level:
    com.example.userservice: INFO
    com.openrangelabs.middleware: INFO
```

## Controller Layer

```java
package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import com.openrangelabs.middleware.exception.dto.ErrorResponseDTO;
import com.openrangelabs.middleware.exception.dto.ValidationErrorResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public Mono<ResponseEntity<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        
        return userService.createUser(request, correlationId)
            .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user))
            .onErrorResume(IllegalArgumentException.class, ex -> 
                Mono.just(ResponseEntity.badRequest()
                    .body(createErrorResponse(ex.getMessage(), 400))));
    }
    
    @GetMapping("/{userId}")
    public Mono<ResponseEntity<UserResponse>> getUser(
            @PathVariable Integer userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        
        return userService.getUser(userId, correlationId)
            .map(user -> ResponseEntity.ok(user))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
    
    @PutMapping("/{userId}")
    public Mono<ResponseEntity<UserResponse>> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody CreateUserRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        
        return userService.updateUser(userId, request, correlationId)
            .map(user -> ResponseEntity.ok(user))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
    
    @DeleteMapping("/{userId}")
    public Mono<ResponseEntity<Void>> deleteUser(
            @PathVariable Integer userId,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        
        return userService.deleteUser(userId, correlationId)
            .then(Mono.just(ResponseEntity.noContent().<Void>build()))
            .onErrorResume(IllegalArgumentException.class, ex ->
                Mono.just(ResponseEntity.notFound().build()));
    }
    
    private UserResponse createErrorResponse(String message, int status) {
        // In a real implementation, you'd return proper error DTOs
        return UserResponse.builder()
            .error(message)
            .build();
    }
}
```

## Service Layer

```java
package com.example.userservice.service;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.openrangelabs.middleware.logging.dto.LogsSystemDTO;
import com.openrangelabs.middleware.logging.dto.LogsUserDTO;
import com.openrangelabs.middleware.logging.model.LogLevel;
import com.openrangelabs.middleware.logging.model.UserLogType;
import com.openrangelabs.middleware.logging.service.LogsSystemService;
import com.openrangelabs.middleware.logging.service.LogsUserService;
import com.openrangelabs.middleware.messaging.dto.UserCreationEventDTO;
import com.openrangelabs.middleware.messaging.ExchangeName;
import com.openrangelabs.middleware.messaging.QueueName;
import com.openrangelabs.middleware.web.HttpMethod;
import com.openrangelabs.middleware.web.HttpStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private LogsUserService logsUserService;
    
    @Autowired
    private LogsSystemService logsSystemService;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private NotificationService notificationService;
    
    // In a real app, you'd have a UserRepository here
    // @Autowired
    // private UserRepository userRepository;
    
    public Mono<UserResponse> createUser(CreateUserRequest request, String correlationId) {
        return Mono.fromCallable(() -> {
            // Log user creation activity
            LogsUserDTO userLog = LogsUserDTO.builder()
                .userId(request.getUserId())
                .organizationId(request.getOrganizationId())
                .type(UserLogType.CREATE.getCode())
                .description("User creation initiated for: " + request.getEmail())
                .build();
            
            logsUserService.saveLog(userLog);
            
            // Log system event
            LogsSystemDTO systemLog = LogsSystemDTO.builder()
                .serviceName("user-service")
                .logLevel(LogLevel.INFO.toString())
                .message("Processing user creation request")
                .userId(request.getUserId())
                .organizationId(request.getOrganizationId())
                .requestMethod(HttpMethod.POST.getMethod())
                .requestUri("/api/v1/users")
                .correlationId(correlationId)
                .build();
            
            logsSystemService.saveLog(systemLog);
            
            // Simulate user creation (in real app, save to database)
            UserResponse response = UserResponse.builder()
                .userId(request.getUserId())
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .organizationId(request.getOrganizationId())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
            
            // Publish user creation event
            UserCreationEventDTO event = UserCreationEventDTO.builder()
                .eventType("USER_CREATED")
                .userId(request.getUserId())
                .organizationId(request.getOrganizationId())
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status("ACTIVE")
                .sourceService("user-service")
                .correlationId(correlationId)
                .build();
            
            rabbitTemplate.convertAndSend(
                ExchangeName.CREATE_USER.getExchangeName(),
                QueueName.PORTAL_USER.getQueueName(),
                event
            );
            
            // Log successful creation
            LogsSystemDTO successLog = LogsSystemDTO.builder()
                .serviceName("user-service")
                .logLevel(LogLevel.INFO.toString())
                .message("User created successfully")
                .userId(request.getUserId())
                .organizationId(request.getOrganizationId())
                .responseStatus(HttpStatus.CREATED.getCode())
                .correlationId(correlationId)
                .executionTimeMs(150L) // In real app, measure actual time
                .build();
            
            logsSystemService.saveLog(successLog);
            
            return response;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(response -> 
            // Send welcome notification asynchronously
            notificationService.sendWelcomeEmail(response.getEmail(), correlationId)
                .then(Mono.just(response))
        )
        .onErrorResume(Exception.class, ex -> {
            // Log error
            LogsSystemDTO errorLog = LogsSystemDTO.builder()
                .serviceName("user-service")
                .logLevel(LogLevel.ERROR.toString())
                .message("User creation failed: " + ex.getMessage())
                .userId(request.getUserId())
                .organizationId(request.getOrganizationId())
                .responseStatus(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                .correlationId(correlationId)
                .stackTrace(getStackTrace(ex))
                .build();
            
            logsSystemService.saveLog(errorLog);
            
            return Mono.error(ex);
        });
    }
    
    public Mono<UserResponse> getUser(Integer userId, String correlationId) {
        return Mono.fromCallable(() -> {
            // Log user view activity
            LogsUserDTO userLog = LogsUserDTO.builder()
                .userId(userId)
                .organizationId(100) // Would get from context in real app
                .type(UserLogType.VIEW.getCode())
                .description("User profile viewed")
                .build();
            
            logsUserService.saveLog(userLog);
            
            // Simulate user retrieval
            UserResponse response = UserResponse.builder()
                .userId(userId)
                .username("john.doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .organizationId(100)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();
            
            // Log system event
            LogsSystemDTO systemLog = LogsSystemDTO.builder()
                .serviceName("user-service")
                .logLevel(LogLevel.INFO.toString())
                .message("User retrieved successfully")
                .userId(userId)
                .organizationId(100)
                .requestMethod(HttpMethod.GET.getMethod())
                .requestUri("/api/v1/users/" + userId)
                .responseStatus(HttpStatus.OK.getCode())
                .correlationId(correlationId)
                .executionTimeMs(45L)
                .build();
            
            logsSystemService.saveLog(systemLog);
            
            return response;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    public Mono<UserResponse> updateUser(Integer userId, CreateUserRequest request, String correlationId) {
        return Mono.fromCallable(() -> {
            // Log user update activity
            LogsUserDTO userLog = LogsUserDTO.builder()
                .userId(userId)
                .organizationId(request.getOrganizationId())
                .type(UserLogType.UPDATE.getCode())
                .description("User profile updated")
                .build();
            
            logsUserService.saveLog(userLog);
            
            // Simulate user update
            UserResponse response = UserResponse.builder()
                .userId(userId)
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .organizationId(request.getOrganizationId())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusDays(30))
                .updatedAt(LocalDateTime.now())
                .build();
            
            // Publish user update event
            UserCreationEventDTO event = UserCreationEventDTO.builder()
                .eventType("USER_UPDATED")
                .userId(userId)
                .organizationId(request.getOrganizationId())
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status("ACTIVE")
                .sourceService("user-service")
                .correlationId(correlationId)
                .build();
            
            rabbitTemplate.convertAndSend(
                ExchangeName.CREATE_USER.getExchangeName(),
                QueueName.PORTAL_USER.getQueueName(),
                event
            );
            
            // Log system event
            LogsSystemDTO systemLog = LogsSystemDTO.builder()
                .serviceName("user-service")
                .logLevel(LogLevel.INFO.toString())
                .message("User updated successfully")
                .userId(userId)
                .organizationId(request.getOrganizationId())
                .requestMethod(HttpMethod.PUT.getMethod())
                .requestUri("/api/v1/users/" + userId)
                .responseStatus(HttpStatus.OK.getCode())
                .correlationId(correlationId)
                .executionTimeMs(120L)
                .build();
            
            logsSystemService.saveLog(systemLog);
            
            return response;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    public Mono<Void> deleteUser(Integer userId, String correlationId) {
        return Mono.fromRunnable(() -> {
            // Log user deletion activity
            LogsUserDTO userLog = LogsUserDTO.builder()
                .userId(userId)
                .organizationId(100) // Would get from context
                .type(UserLogType.DELETE.getCode())
                .description("User account deleted")
                .build();
            
            logsUserService.saveLog(userLog);
            
            // Publish user deletion event
            UserCreationEventDTO event = UserCreationEventDTO.builder()
                .eventType("USER_DELETED")
                .userId(userId)
                .organizationId(100)
                .status("INACTIVE")
                .sourceService("user-service")
                .correlationId(correlationId)
                .build();
            
            rabbitTemplate.convertAndSend(
                ExchangeName.CREATE_USER.getExchangeName(),
                QueueName.PORTAL_USER.getQueueName(),
                event
            );
            
            // Log system event
            LogsSystemDTO systemLog = LogsSystemDTO.builder()
                .serviceName("user-service")
                .logLevel(LogLevel.INFO.toString())
                .message("User deleted successfully")
                .userId(userId)
                .organizationId(100)
                .requestMethod(HttpMethod.DELETE.getMethod())
                .requestUri("/api/v1/users/" + userId)
                .responseStatus(HttpStatus.NO_CONTENT.getCode())
                .correlationId(correlationId)
                .executionTimeMs(80L)
                .build();
            
            logsSystemService.saveLog(systemLog);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(sw));
        return sw.toString();
    }
}
```

## External Service Integration

```java
package com.example.userservice.service;

import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.middleware.logging.dto.LogsSystemDTO;
import com.openrangelabs.middleware.logging.model.LogLevel;
import com.openrangelabs.middleware.logging.service.LogsSystemService;
import com.openrangelabs.middleware.web.HttpMethod;
import com.openrangelabs.middleware.web.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
public class NotificationService {
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Autowired
    private LogsSystemService logsSystemService;
    
    @Value("${external-services.notification-service}")
    private String notificationServiceUrl;
    
    public Mono<Void> sendWelcomeEmail(String email, String correlationId) {
        WebClient client = webClientBuilder
            .baseUrl(notificationServiceUrl)
            .build();
        
        Map<String, Object> emailRequest = Map.of(
            "to", email,
            "template", "welcome",
            "data", Map.of("email", email)
        );
        
        return client.post()
            .uri("/api/v1/notifications/email")
            .header("X-Correlation-ID", correlationId)
            .bodyValue(emailRequest)
            .retrieve()
            .bodyToMono(String.class)
            .doOnSuccess(response -> {
                // Log successful notification
                LogsSystemDTO log = LogsSystemDTO.builder()
                    .serviceName("user-service")
                    .logLevel(LogLevel.INFO.toString())
                    .message("Welcome email sent successfully")
                    .requestMethod(HttpMethod.POST.getMethod())
                    .requestUri("/api/v1/notifications/email")
                    .responseStatus(HttpStatus.OK.getCode())
                    .correlationId(correlationId)
                    .build();
                
                logsSystemService.saveLog(log);
            })
            .onErrorResume(WebClientConfig.WebClientServerException.class, ex -> {
                // Log server error but don't fail the main operation
                LogsSystemDTO errorLog = LogsSystemDTO.builder()
                    .serviceName("user-service")
                    .logLevel(LogLevel.ERROR.toString())
                    .message("Failed to send welcome email: " + ex.getMessage())
                    .requestMethod(HttpMethod.POST.getMethod())
                    .requestUri("/api/v1/notifications/email")
                    .responseStatus(ex.getStatusCode().value())
                    .correlationId(correlationId)
                    .build();
                
                logsSystemService.saveLog(errorLog);
                return Mono.empty(); // Don't fail the main operation
            })
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof WebClientConfig.WebClientServerException))
            .timeout(Duration.ofSeconds(10))
            .then();
    }
    
    public Mono<String> sendUserNotification(Integer userId, String message, String correlationId) {
        WebClient client = webClientBuilder
            .baseUrl(notificationServiceUrl)
            .build();
        
        Map<String, Object> notificationRequest = Map.of(
            "userId", userId,
            "message", message,
            "type", "USER_UPDATE"
        );
        
        return client.post()
            .uri("/api/v1/notifications/user")
            .header("X-Correlation-ID", correlationId)
            .bodyValue(notificationRequest)
            .retrieve()
            .bodyToMono(String.class)
            .doOnSuccess(response -> {
                LogsSystemDTO log = LogsSystemDTO.builder()
                    .serviceName("user-service")
                    .logLevel(LogLevel.INFO.toString())
                    .message("User notification sent successfully")
                    .userId(userId)
                    .correlationId(correlationId)
                    .build();
                
                logsSystemService.saveLog(log);
            })
            .onErrorResume(Exception.class, ex -> {
                logsSystemService.logException("user-service", (Exception) ex, userId, null);
                return Mono.just("Failed to send notification");
            });
    }
}
```

## DTOs

```java
package com.example.userservice.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class CreateUserRequest {
    
    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be positive")
    private Integer userId;
    
    @NotNull(message = "Organization ID is required")  
    @Min(value = 1, message = "Organization ID must be positive")
    private Integer organizationId;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;
    
    @Size(max = 100, message = "Last name cannot exceed 100 characters") 
    private String lastName;
    
    // Constructors, getters, setters...
    public CreateUserRequest() {}
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private CreateUserRequest request = new CreateUserRequest();
        
        public Builder userId(Integer userId) {
            request.userId = userId;
            return this;
        }
        
        public Builder organizationId(Integer organizationId) {
            request.organizationId = organizationId;
            return this;
        }
        
        public Builder username(String username) {
            request.username = username;
            return this;
        }
        
        public Builder email(String email) {
            request.email = email;
            return this;
        }
        
        public Builder firstName(String firstName) {
            request.firstName = firstName;
            return this;
        }
        
        public Builder lastName(String lastName) {
            request.lastName = lastName;
            return this;
        }
        
        public CreateUserRequest build() {
            return request;
        }
    }
    
    // Getters and setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public Integer getOrganizationId() { return organizationId; }
    public void setOrganizationId(Integer organizationId) { this.organizationId = organizationId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
```

```java
package com.example.userservice.dto;

import java.time.LocalDateTime;

public class UserResponse {
    private Integer userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Integer organizationId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String error; // For error responses
    
    // Constructors
    public UserResponse() {}
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UserResponse response = new UserResponse();
        
        public Builder userId(Integer userId) {
            response.userId = userId;
            return this;
        }
        
        public Builder username(String username) {
            response.username = username;
            return this;
        }
        
        public Builder email(String email) {
            response.email = email;
            return this;
        }
        
        public Builder firstName(String firstName) {
            response.firstName = firstName;
            return this;
        }
        
        public Builder lastName(String lastName) {
            response.lastName = lastName;
            return this;
        }
        
        public Builder organizationId(Integer organizationId) {
            response.organizationId = organizationId;
            return this;
        }
        
        public Builder status(String status) {
            response.status = status;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            response.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            response.updatedAt = updatedAt;
            return this;
        }
        
        public Builder error(String error) {
            response.error = error;
            return this;
        }
        
        public UserResponse build() {
            return response;
        }
    }
    
    // Getters and setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public Integer getOrganizationId() { return organizationId; }
    public void setOrganizationId(Integer organizationId) { this.organizationId = organizationId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
```

## Message Consumers

```java
package com.example.userservice.messaging;

import com.openrangelabs.middleware.logging.dto.LogsUserDTO;
import com.openrangelabs.middleware.logging.model.UserLogType;
import com.openrangelabs.middleware.logging.service.LogsUserService;
import com.openrangelabs.middleware.messaging.dto.UserCreationEventDTO;
import com.openrangelabs.middleware.messaging.QueueName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);
    
    @Autowired
    private LogsUserService logsUserService;
    
    @RabbitListener(queues = "#{T(com.openrangelabs.middleware.messaging.QueueName).PORTAL_USER.queueName}")
    public void handleUserEvent(UserCreationEventDTO event) {
        try {
            logger.info("Processing user event: {} for user {}", event.getEventType(), event.getUserId());
            
            // Process different event types
            switch (event.getEventType()) {
                case "USER_CREATED":
                    handleUserCreated(event);
                    break;
                case "USER_UPDATED":
                    handleUserUpdated(event);
                    break;
                case "USER_DELETED":
                    handleUserDeleted(event);
                    break;
                case "USER_ACTIVATED":
                    handleUserActivated(event);
                    break;
                case "USER_DEACTIVATED":
                    handleUserDeactivated(event);
                    break;
                default:
                    logger.warn("Unknown event type: {}", event.getEventType());
            }
            
        } catch (Exception e) {
            logger.error("Failed to process user event: {}", event.getEventId(), e);
            throw new RuntimeException("Event processing failed", e);
        }
    }
    
    private void handleUserCreated(UserCreationEventDTO event) {
        // Log the event processing
        LogsUserDTO auditLog = LogsUserDTO.builder()
            .userId(event.getUserId())
            .organizationId(event.getOrganizationId())
            .type(UserLogType.AUDIT.getCode())
            .description("Processed USER_CREATED event: " + event.getEventId())
            .build();
        
        logsUserService.saveLog(auditLog);
        
        // Additional processing like:
        // - Send welcome email
        // - Create user profile
        // - Initialize preferences
        logger.info("User created event processed for user: {}", event.getUserId());
    }
    
    private void handleUserUpdated(UserCreationEventDTO event) {
        LogsUserDTO auditLog = LogsUserDTO.builder()
            .userId(event.getUserId())
            .organizationId(event.getOrganizationId())
            .type(UserLogType.AUDIT.getCode())
            .description("Processed USER_UPDATED event: " + event.getEventId())
            .build();
        
        logsUserService.saveLog(auditLog);
        
        // Additional processing like:
        // - Update search index
        // - Sync with external systems
        logger.info("User updated event processed for user: {}", event.getUserId());
    }
    
    private void handleUserDeleted(UserCreationEventDTO event) {
        LogsUserDTO auditLog = LogsUserDTO.builder()
            .userId(event.getUserId())
            .organizationId(event.getOrganizationId())
            .type(UserLogType.AUDIT.getCode())
            .description("Processed USER_DELETED event: " + event.getEventId())
            .build();
        
        logsUserService.saveLog(auditLog);
        
        // Additional processing like:
        // - Archive user data
        // - Remove from search index
        // - Cancel subscriptions
        logger.info("User deleted event processed for user: {}", event.getUserId());
    }
    
    private void handleUserActivated(UserCreationEventDTO event) {
        LogsUserDTO auditLog = LogsUserDTO.builder()
            .userId(event.getUserId())
            .organizationId(event.getOrganizationId())
            .type(UserLogType.AUDIT.getCode())
            .description("Processed USER_ACTIVATED event: " + event.getEventId())
            .build();
        
        logsUserService.saveLog(auditLog);
        
        logger.info("User activated event processed for user: {}", event.getUserId());
    }
    
    private void handleUserDeactivated(UserCreationEventDTO event) {
        LogsUserDTO auditLog = LogsUserDTO.builder()
            .userId(event.getUserId())
            .organizationId(event.getOrganizationId())
            .type(UserLogType.AUDIT.getCode())
            .description("Processed USER_DEACTIVATED event: " + event.getEventId())
            .build();
        
        logsUserService.saveLog(auditLog);
        
        logger.info("User deactivated event processed for user: {}", event.getUserId());
    }
    
    @RabbitListener(queues = "#{T(com.openrangelabs.middleware.messaging.QueueName).PORTAL_USER_DLQ.queueName}")
    public void handleFailedUserEvent(UserCreationEventDTO event) {
        logger.error("User event failed after retries: {}", event.getEventId());
        
        // Log the failure
        LogsUserDTO errorLog = LogsUserDTO.builder()
            .userId(event.getUserId())
            .organizationId(event.getOrganizationId())
            .type(UserLogType.ERROR.getCode())
            .description("Failed to process event after retries: " + event.getEventId())
            .build();
        
        logsUserService.saveLog(errorLog);
        
        // Additional error handling:
        // - Send alert to operations team
        // - Store in error table for manual review
        // - Implement compensation logic
    }
}
```

## Health Checks and Monitoring

```java
package com.example.userservice.config;

import com.openrangelabs.middleware.logging.service.LogsSystemService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;

@Component("userServiceHealth")
public class UserServiceHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private LogsSystemService logsSystemService;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Override
    public Health health() {
        Health.Builder healthBuilder = Health.up();
        
        // Check database connectivity
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                healthBuilder.withDetail("database", "UP");
            } else {
                healthBuilder.down().withDetail("database", "Connection invalid");
            }
        } catch (Exception e) {
            healthBuilder.down().withDetail("database", "Error: " + e.getMessage());
        }
        
        // Check RabbitMQ connectivity
        try {
            rabbitTemplate.getConnectionFactory().createConnection().close();
            healthBuilder.withDetail("rabbitmq", "UP");
        } catch (Exception e) {
            healthBuilder.down().withDetail("rabbitmq", "Error: " + e.getMessage());
        }
        
        // Check logging service
        try {
            // Try to get recent log statistics
            var stats = logsSystemService.getLogStatsByLevel(
                java.time.LocalDateTime.now().minusMinutes(5),
                java.time.LocalDateTime.now()
            );
            healthBuilder.withDetail("logging", "UP - " + stats.size() + " log types");
        } catch (Exception e) {
            healthBuilder.down().withDetail("logging", "Error: " + e.getMessage());
        }
        
        // Check external services
        try {
            WebClient client = webClientBuilder
                .baseUrl("http://localhost:8081")
                .build();
                
            String response = client.get()
                .uri("/actuator/health")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();
                
            healthBuilder.withDetail("notification-service", "UP");
        } catch (Exception e) {
            healthBuilder.withDetail("notification-service", "DOWN - " + e.getMessage());
        }
        
        return healthBuilder.build();
    }
}
```

## Testing

```java
package com.example.userservice.service;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.openrangelabs.middleware.logging.service.LogsSystemService;
import com.openrangelabs.middleware.logging.service.LogsUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private LogsUserService logsUserService;
    
    @Mock
    private LogsSystemService logsSystemService;
    
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private UserService userService;
    
    private CreateUserRequest createUserRequest;
    
    @BeforeEach
    void setUp() {
        createUserRequest = CreateUserRequest.builder()
            .userId(12345)
            .organizationId(100)
            .username("john.doe")
            .email("john.doe@example.com")
            .firstName("John")
            .lastName("Doe")
            .build();
    }
    
    @Test
    void testCreateUser_Success() {
        // Given
        when(logsUserService.saveLog(any())).thenReturn(any());
        when(logsSystemService.saveLog(any())).thenReturn(any());
        when(notificationService.sendWelcomeEmail(anyString(), anyString()))
            .thenReturn(Mono.empty());
        
        // When
        Mono<UserResponse> result = userService.createUser(createUserRequest, "test-correlation-id");
        
        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.getUserId().equals(12345) &&
                response.getUsername().equals("john.doe") &&
                response.getEmail().equals("john.doe@example.com")
            )
            .verifyComplete();
        
        // Verify interactions
        verify(logsUserService, times(1)).saveLog(any());
        verify(logsSystemService, times(2)).saveLog(any()); // Initial log + success log
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any());
        verify(notificationService, times(1)).sendWelcomeEmail(anyString(), anyString());
    }
    
    @Test
    void testCreateUser_ServiceFailure() {
        // Given
        when(logsUserService.saveLog(any())).thenThrow(new RuntimeException("Database error"));
        when(logsSystemService.saveLog(any())).thenReturn(any());
        when(notificationService.sendWelcomeEmail(anyString(), anyString()))
            .thenReturn(Mono.empty());
        
        // When
        Mono<UserResponse> result = userService.createUser(createUserRequest, "test-correlation-id");
        
        // Then
        StepVerifier.create(result)
            .expectError(RuntimeException.class)
            .verify();
        
        // Verify error logging
        verify(logsSystemService, atLeastOnce()).saveLog(any());
    }
    
    @Test
    void testGetUser_Success() {
        // Given
        when(logsUserService.saveLog(any())).thenReturn(any());
        when(logsSystemService.saveLog(any())).thenReturn(any());
        
        // When
        Mono<UserResponse> result = userService.getUser(12345, "test-correlation-id");
        
        // Then
        StepVerifier.create(result)
            .expectNextMatches(response -> 
                response.getUserId().equals(12345) &&
                response.getUsername().equals("john.doe")
            )
            .verifyComplete();
        
        // Verify logging
        verify(logsUserService, times(1)).saveLog(any());
        verify(logsSystemService, times(1)).saveLog(any());
    }
}
```

## Docker Configuration

### Dockerfile

```dockerfile
FROM openjdk:17-jre-slim

# Create app directory
WORKDIR /app

# Copy application jar
COPY target/user-service-1.0.0.jar app.jar

# Environment variables
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xms512m -Xmx2g"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Docker Compose

```yaml
version: '3.8'

services:
  user-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: development
      DB_HOST: postgres
      DB_NAME: userservice
      DB_USERNAME: userservice
      DB_PASSWORD: password
      RABBITMQ_HOST: rabbitmq
      NOTIFICATION_SERVICE_URL: http://notification-service:8081
    depends_on:
      - postgres
      - rabbitmq
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    networks:
      - app-network

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: userservice
      POSTGRES_USER: userservice
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    networks:
      - app-network

  rabbitmq:
    image: rabbitmq:3-management
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    ports:
      - "5672:5672"
      - "15672:15672"
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
    networks:
      - app-network

  notification-service:
    image: notification-service:latest
    ports:
      - "8081:8080"
    environment:
      SPRING_PROFILES_ACTIVE: development
    networks:
      - app-network

volumes:
  postgres_data:
  rabbitmq_data:

networks:
  app-network:
    driver: bridge
```

## Key Benefits Demonstrated

This example showcases several key benefits of the middleware library:

### 1. **Standardized Logging**
- Consistent user activity tracking across all operations
- System event logging with correlation IDs for request tracing
- Automatic error logging with stack traces
- Rich querying capabilities for analytics and debugging

### 2. **Reliable Messaging**
- Type-safe queue and exchange names
- Automatic dead letter queue handling
- Structured event DTOs with validation
- Event sourcing patterns for audit trails

### 3. **Robust HTTP Communication**
- Enhanced WebClient with connection pooling
- Automatic retry logic for transient failures
- Comprehensive error handling
- Request/response logging for debugging

### 4. **Comprehensive Validation**
- Request validation with detailed error responses
- Custom validators for domain-specific data types
- Automatic error response formatting
- Input sanitization and security

### 5. **Production-Ready Features**
- Health checks for all dependencies
- Metrics and monitoring integration
- Environment-specific configuration
- Docker containerization support

## Usage Patterns

This example demonstrates common enterprise patterns:

- **Request/Response Logging** - Every operation is logged with correlation IDs
- **Event-Driven Architecture** - User operations publish events for other services
- **External Service Integration** - Reliable communication with other microservices
- **Error Handling** - Comprehensive error logging and graceful degradation
- **Async Processing** - Non-blocking operations with reactive programming
- **Health Monitoring** - Active health checks for all dependencies

## Next Steps

To extend this example:

1. **Add Authentication** - Integrate with JWT tokens or OAuth2
2. **Implement Caching** - Add Redis for user data caching
3. **Add Metrics** - Integrate with Micrometer and Prometheus
4. **Enhance Testing** - Add integration tests with Testcontainers
5. **Deploy to Kubernetes** - Add K8s manifests and Helm charts