---
layout: default
title: Documentation Home
nav_order: 1
---

# OpenRange Labs Middleware Common Library Documentation

Welcome to the comprehensive documentation for the OpenRange Labs Middleware Common Library - a production-ready Spring Boot 3.4+ library that provides standardized logging, messaging, validation, and HTTP client functionality for enterprise Java applications.

## 🚀 Quick Navigation

<div class="doc-nav-grid">
  <div class="doc-nav-item">
    <h3><a href="getting-started">🏁 Getting Started</a></h3>
    <p>Installation, setup, and your first integration</p>
  </div>

  <div class="doc-nav-item">
    <h3><a href="logging">📝 Logging Guide</a></h3>
    <p>User activity and system logging with JPA repositories</p>
  </div>

  <div class="doc-nav-item">
    <h3><a href="messaging">📨 Messaging Guide</a></h3>
    <p>RabbitMQ integration with queues and exchanges</p>
  </div>

  <div class="doc-nav-item">
    <h3><a href="webclient">🌐 WebClient Guide</a></h3>
    <p>Enhanced HTTP client with connection pooling</p>
  </div>

  <div class="doc-nav-item">
    <h3><a href="configuration">⚙️ Configuration</a></h3>
    <p>Complete configuration reference</p>
  </div>

  <div class="doc-nav-item">
    <h3><a href="api-reference">📚 API Reference</a></h3>
    <p>Complete API documentation</p>
  </div>
</div>

## ✨ Key Features

- **🔍 Structured Logging** - User activity and system logs with JPA repositories and rich querying
- **📨 Messaging Integration** - RabbitMQ queues and exchanges with dead letter queue support
- **🌐 Enhanced WebClient** - Production-ready HTTP client with connection pooling and retry logic
- **✅ Validation Framework** - Custom validators for common enterprise data types
- **🛡️ Error Handling** - Standardized error responses and comprehensive exception handling
- **🏗️ Builder Patterns** - Fluent APIs for creating DTOs and entities
- **🔧 Type Safety** - Comprehensive enums for HTTP methods, status codes, and log levels

## 📖 Quick Example

Here's a simple example showing the library in action:

```java
@Service
public class UserService {
    
    @Autowired
    private LogsUserService logsUserService;
    
    @Autowired
    private LogsSystemService logsSystemService;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    public Mono<UserResponse> createUser(CreateUserRequest request) {
        String correlationId = UUID.randomUUID().toString();
        
        // Log user activity
        LogsUserDTO userLog = LogsUserDTO.builder()
            .userId(request.getUserId())
            .organizationId(request.getOrganizationId())
            .type(UserLogType.CREATE.getCode())
            .description("User creation initiated")
            .build();
        
        logsUserService.saveLog(userLog);
        
        // Make external API call with enhanced WebClient
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
                // Log successful operation
                LogsSystemDTO systemLog = LogsSystemDTO.builder()
                    .serviceName("user-service")
                    .logLevel(LogLevel.INFO.toString())
                    .message("User created successfully")
                    .userId(request.getUserId())
                    .correlationId(correlationId)
                    .responseStatus(HttpStatus.CREATED.getCode())
                    .build();
                    
                logsSystemService.saveLog(systemLog);
            });
    }
}
```

## 🛠️ Requirements

- **Java 17+**
- **Spring Boot 3.4+**
- **PostgreSQL** (for JPA entities)
- **RabbitMQ** (optional, for messaging features)

## 📦 Installation

### Maven
```xml
<dependency>
    <groupId>com.openrangelabs.middleware</groupId>
    <artifactId>common-lib</artifactId>
    <version>2024.12.7</version>
</dependency>
```

### Gradle
```gradle
implementation 'com.openrangelabs.middleware:common-lib:2024.12.7'
```

## 🏗️ Architecture Overview

The library is organized into several key modules:

### Core Modules

- **Logging** (`com.openrangelabs.middleware.logging`)
    - User activity tracking with `LogsUserService`
    - System event logging with `LogsSystemService`
    - JPA entities and repositories
    - Rich querying capabilities

- **Messaging** (`com.openrangelabs.middleware.messaging`)
    - RabbitMQ configuration and DTOs
    - Predefined queues and exchanges
    - Dead letter queue support
    - Type-safe enum constants

- **Web** (`com.openrangelabs.middleware.web`)
    - Enhanced WebClient configuration
    - HTTP method and status enums
    - Connection pooling and timeout management
    - Built-in error handling

- **Validation** (`com.openrangelabs.middleware.validation`)
    - Custom validation annotations
    - Environment, log level, and HTTP method validators
    - Integration with Spring Boot validation

- **User Management** (`com.openrangelabs.middleware.user`)
    - Portal user entities and services
    - User creation event DTOs
    - Repository with advanced querying

## 🎯 Use Cases

### Microservices Architecture
Standardize logging, messaging, and HTTP communication across all your microservices with consistent patterns and configurations.

### API Gateway Integration
Enhanced HTTP clients with proper error handling, retry logic, and connection pooling for reliable external service communication.

### Enterprise Applications
Comprehensive audit trails and user activity tracking with structured logging and rich querying capabilities.

### Event-Driven Systems
RabbitMQ messaging integration with reliable delivery patterns, dead letter queues, and type-safe queue/exchange management.

## 🚀 Next Steps

1. **[Start with the Getting Started Guide](getting-started)** - Set up your first integration
2. **[Explore the Logging Guide](logging)** - Learn about comprehensive logging features
3. **[Check out WebClient Guide](webclient)** - Master HTTP client configuration
4. **[Review Configuration Options](configuration)** - Customize for your environment
5. **[Browse Examples](examples/)** - See real-world usage patterns

## 🆘 Support & Community

- **📖 Documentation**: You're here! Browse the guides and API reference
- **🐛 Issues**: [GitHub Issues](https://github.com/openrangelabs/middleware-library-common/issues)
- **💬 Discussions**: [GitHub Discussions](https://github.com/openrangelabs/middleware-library-common/discussions)
- **📧 Contact**: [support@openrangelabs.com](mailto:support@openrangelabs.com)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/openrangelabs/middleware-library-common/blob/main/LICENSE) file for details.

---

<div class="footer-note">
<strong>OpenRange Labs</strong> - Building enterprise-grade middleware solutions for modern Java applications.
</div>

<style>
.doc-nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin: 2rem 0;
}

.doc-nav-item {
  padding: 1.5rem;
  border: 1px solid #e1e4e8;
  border-radius: 8px;
  background: #f6f8fa;
}

.doc-nav-item h3 {
  margin-top: 0;
  margin-bottom: 0.5rem;
}

.doc-nav-item h3 a {
  text-decoration: none;
  color: #0366d6;
}

.doc-nav-item h3 a:hover {
  text-decoration: underline;
}

.doc-nav-item p {
  margin-bottom: 0;
  color: #586069;
}

.footer-note {
  text-align: center;
  margin-top: 3rem;
  padding: 1rem;
  background: #f1f3f4;
  border-radius: 4px;
  color: #586069;
}
</style>