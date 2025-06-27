# OpenRange Labs Middleware Common Library

A comprehensive Spring Boot 3.4+ library providing standardized logging, messaging, validation, and HTTP client functionality for enterprise Java applications.

[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4+-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🚀 Quick Start

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

## 📚 Documentation

Complete documentation is available on our **[GitHub Pages](https://openrangelabs.github.io/middleware-library-common/)**

### Quick Links
- **[Getting Started Guide](docs/getting-started.md)** - Setup and basic usage
- **[Logging Guide](docs/logging.md)** - User and system logging
- **[Messaging Guide](docs/messaging.md)** - RabbitMQ integration
- **[WebClient Guide](docs/webclient.md)** - Enhanced HTTP client
- **[API Reference](docs/api-reference.md)** - Complete API documentation
- **[Configuration](docs/configuration.md)** - Application properties
- **[Examples](docs/examples/)** - Code examples and use cases

## ✨ Key Features

- **🔍 Structured Logging** - User activity and system logs with JPA repositories
- **📨 Messaging Integration** - RabbitMQ queues and exchanges with dead letter queues
- **🌐 Enhanced WebClient** - Configured HTTP client with connection pooling and retry logic
- **✅ Validation Framework** - Custom validators for common data types
- **🛡️ Error Handling** - Standardized error responses and exception handling
- **🏗️ Builder Patterns** - Fluent APIs for creating DTOs and entities
- **🔧 Type Safety** - Comprehensive enums for HTTP methods, status codes, log levels

## 🎯 Use Cases

- **Microservices Architecture** - Standardized logging and messaging across services
- **API Gateway Integration** - Enhanced HTTP clients with proper error handling
- **Enterprise Applications** - Comprehensive audit trails and user activity tracking
- **Event-Driven Systems** - RabbitMQ messaging with reliable delivery patterns

## 📖 Quick Example

```java
@Service
public class UserService {
    
    @Autowired
    private LogsUserService logsUserService;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    public void createUser(String username, String email) {
        // Log user creation
        LogsUserDTO userLog = LogsUserDTO.builder()
            .userId(12345)
            .organizationId(100)
            .type(UserLogType.CREATE.getCode())
            .description("User account created")
            .build();
        
        logsUserService.saveLog(userLog);
        
        // Make external API call
        WebClient client = webClientBuilder
            .baseUrl("https://api.external-service.com")
            .build();
            
        client.post()
            .uri("/users")
            .bodyValue(Map.of("username", username, "email", email))
            .retrieve()
            .bodyToMono(String.class)
            .subscribe();
    }
}
```

## 🏗️ Architecture

The library is organized into several key modules:

- **Logging** (`com.openrangelabs.middleware.logging`) - User and system logging with JPA entities
- **Messaging** (`com.openrangelabs.middleware.messaging`) - RabbitMQ configuration and DTOs
- **Web** (`com.openrangelabs.middleware.web`) - HTTP enums and WebClient configuration
- **Validation** (`com.openrangelabs.middleware.validation`) - Custom validation annotations
- **User Management** (`com.openrangelabs.middleware.user`) - Portal user entities and services

## 🛠️ Requirements

- **Java 17+**
- **Spring Boot 3.4+**
- **PostgreSQL** (for JPA entities)
- **RabbitMQ** (optional, for messaging features)

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: [GitHub Pages](https://openrangelabs.github.io/middleware-library-common/)
- **Issues**: [GitHub Issues](https://github.com/openrangelabs/middleware-library-common/issues)
- **Discussions**: [GitHub Discussions](https://github.com/openrangelabs/middleware-library-common/discussions)

---

**OpenRange Labs** - Building enterprise-grade middleware solutions