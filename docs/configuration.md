# Configuration Guide

Complete configuration reference for the OpenRange Labs Middleware Common Library.

## Overview

The library uses Spring Boot's configuration system with support for:
- **YAML and Properties files** - Standard Spring Boot configuration
- **Environment-specific profiles** - Different settings per environment
- **External configuration** - Override settings without code changes
- **Validation** - Built-in validation for configuration values

## Core Configuration

### Database Configuration

Required for logging functionality:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database
    username: ${DB_USERNAME:your_username}
    password: ${DB_PASSWORD:your_password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update  # Use 'validate' in production
    show-sql: false     # Set to true for debugging
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        jdbc:
          time_zone: UTC
```

### WebClient Configuration

```yaml
# Domain configuration (used for security headers)
domain: https://your-application.com

# WebClient settings
webclient:
  timeout:
    connect: 10s      # Connection timeout
    read: 30s         # Read timeout
    write: 30s        # Write timeout
  
  connection-pool:
    max-connections: 100                    # Total connections
    max-connections-per-route: 20          # Connections per destination
    keep-alive: 5m                         # Connection keep-alive time
    evict-idle-connections: 1m             # Cleanup interval
  
  user-agent: "YourApp/1.0"               # Custom user agent
  enable-logging: false                   # Request/response logging
```

### RabbitMQ Configuration (Optional)

Only required if using messaging features:

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:guest}
    password: ${RABBITMQ_PASSWORD:guest}
    virtual-host: ${RABBITMQ_VHOST:/}
    connection-timeout: 60000
    
    # Publisher settings
    publisher-confirm-type: correlated
    publisher-returns: true
    
    # Consumer settings
    listener:
      simple:
        acknowledge-mode: auto
        concurrency: 1
        max-concurrency: 10
        prefetch: 10
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          multiplier: 2
          max-interval: 10000
```

## Environment-Specific Configuration

### Development Environment

```yaml
# application-development.yml
spring:
  profiles: development
  
  datasource:
    url: jdbc:postgresql://localhost:5432/middleware_dev
    
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update

webclient:
  timeout:
    connect: 5s
    read: 10s
  enable-logging: true

logging:
  level:
    com.openrangelabs.middleware: DEBUG
    org.springframework.web.reactive.function.client: DEBUG
```

### Testing Environment

```yaml
# application-testing.yml
spring:
  profiles: testing
  
  datasource:
    url: jdbc:postgresql://test-db:5432/middleware_test
    
webclient:
  timeout:
    connect: 8s
    read: 20s
  enable-logging: true

# Disable actual message publishing in tests
spring:
  rabbitmq:
    host: localhost
    port: 5672
```

### Staging Environment

```yaml
# application-staging.yml
spring:
  profiles: staging
  
  datasource:
    url: jdbc:postgresql://staging-db:5432/middleware_staging
    
  jpa:
    hibernate:
      ddl-auto: validate  # Don't auto-update schema
      
webclient:
  timeout:
    connect: 12s
    read: 45s
  connection-pool:
    max-connections: 150

logging:
  level:
    com.openrangelabs.middleware: INFO
```

### Production Environment

```yaml
# application-production.yml
spring:
  profiles: production
  
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    
webclient:
  timeout:
    connect: 15s
    read: 60s
    write: 60s
  connection-pool:
    max-connections: 200
    max-connections-per-route: 50
    keep-alive: 10m
  enable-logging: false

logging:
  level:
    com.openrangelabs.middleware: WARN
    org.springframework.web.reactive.function.client: WARN
```

## Advanced Configuration

### Security Configuration

```yaml
# HTTPS and security settings
server:
  ssl:
    enabled: true
    key-store: ${SSL_KEYSTORE_PATH}
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    
  # Security headers (automatically added by WebClient)
  servlet:
    session:
      tracking-modes: cookie
      cookie:
        secure: true
        http-only: true
        same-site: strict
```

### Monitoring Configuration

```yaml
# Actuator endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
      
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      environment: ${spring.profiles.active}
```

### Logging Configuration

```yaml
logging:
  config: classpath:logback-spring.xml
  
  # File logging
  file:
    name: logs/application.log
    max-size: 100MB
    max-history: 30
    
  # Log levels
  level:
    root: INFO
    com.openrangelabs.middleware: INFO
    org.springframework.web.reactive.function.client: WARN
    reactor.netty.http.client: WARN
    
  # Custom log patterns
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{correlationId:-}] %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{correlationId:-}] %logger{36} - %msg%n"
```

## Configuration Properties Reference

### WebClient Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `webclient.timeout.connect` | Duration | 10s | Connection timeout |
| `webclient.timeout.read` | Duration | 30s | Read timeout |
| `webclient.timeout.write` | Duration | 30s | Write timeout |
| `webclient.connection-pool.max-connections` | Integer | 100 | Maximum total connections |
| `webclient.connection-pool.max-connections-per-route` | Integer | 20 | Maximum connections per route |
| `webclient.connection-pool.keep-alive` | Duration | 5m | Connection keep-alive time |
| `webclient.connection-pool.evict-idle-connections` | Duration | 1m | Idle connection cleanup interval |
| `webclient.user-agent` | String | OpenRangeLabs-Middleware/1.0 | User agent string |
| `webclient.enable-logging` | Boolean | false | Enable request/response logging |

### Domain Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `domain` | String | http://localhost | Application domain for security headers |

## Environment Variables

The library supports environment variable override for sensitive configuration:

```bash
# Database
export DB_HOST=prod-database.example.com
export DB_PORT=5432
export DB_NAME=middleware_prod
export DB_USERNAME=middleware_user
export DB_PASSWORD=secure_password

# RabbitMQ
export RABBITMQ_HOST=rabbitmq.example.com
export RABBITMQ_PORT=5672
export RABBITMQ_USERNAME=middleware_user
export RABBITMQ_PASSWORD=secure_password
export RABBITMQ_VHOST=/middleware

# SSL
export SSL_KEYSTORE_PATH=/etc/ssl/keystore.p12
export SSL_KEYSTORE_PASSWORD=keystore_password

# Application
export SPRING_PROFILES_ACTIVE=production
export JAVA_OPTS="-Xms512m -Xmx2g"
```

## Configuration Validation

The library includes validation for configuration properties:

### Custom Validation

```java
@Component
@ConfigurationProperties(prefix = "webclient")
@Validated
public class WebClientProperties {
    
    @Valid
    private Timeout timeout = new Timeout();
    
    @Valid
    private ConnectionPool connectionPool = new ConnectionPool();
    
    public static class Timeout {
        @NotNull
        @DurationMin(seconds = 1)
        @DurationMax(minutes = 5)
        private Duration connect = Duration.ofSeconds(10);
        
        // getters and setters
    }
}
```

### Validation Errors

Invalid configuration will prevent application startup:

```
***************************
APPLICATION FAILED TO START
***************************

Description:
Binding to target org.springframework.boot.context.properties.bind.BindException: 
Failed to bind properties under 'webclient.timeout.connect' to java.time.Duration

Reason: Failed to convert property value of type 'java.lang.String' to required type 'java.time.Duration'

Action:
Update your application's configuration. The following values are valid:
- PT10S (10 seconds)
- 10s (10 seconds)
- 1m (1 minute)
```

## Configuration Best Practices

### 1. Use Environment-Specific Profiles

```yaml
# Base configuration in application.yml
spring:
  application:
    name: your-service
    
# Environment overrides in application-{profile}.yml
---
spring:
  profiles: production
  datasource:
    url: ${DATABASE_URL}
```

### 2. Externalize Sensitive Configuration

```yaml
# Use environment variables for secrets
spring:
  datasource:
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

# Or use Spring Cloud Config
spring:
  cloud:
    config:
      uri: ${CONFIG_SERVER_URL}
```

### 3. Use Configuration Validation

```java
@ConfigurationProperties(prefix = "app")
@Validated
@Component
public class AppProperties {
    
    @NotBlank
    private String name;
    
    @Min(1)
    @Max(100)
    private int maxUsers;
    
    @Email
    private String adminEmail;
}
```

### 4. Document Configuration

```yaml
# Example configuration with comments
webclient:
  timeout:
    # How long to wait for connection establishment
    connect: 10s
    
    # How long to wait for response data
    read: 30s
  
  connection-pool:
    # Increase for high-traffic applications
    max-connections: 100
```

## Docker Configuration

### Dockerfile Environment

```dockerfile
FROM openjdk:17-jre-slim

# Environment variables with defaults
ENV SPRING_PROFILES_ACTIVE=production
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV WEBCLIENT_TIMEOUT_CONNECT=15s
ENV WEBCLIENT_ENABLE_LOGGING=false

COPY target/your-app.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose

```yaml
version: '3.8'

services:
  app:
    image: your-app:latest
    environment:
      SPRING_PROFILES_ACTIVE: production
      DB_HOST: postgres
      DB_NAME: middleware
      DB_USERNAME: middleware_user
      DB_PASSWORD: secure_password
      RABBITMQ_HOST: rabbitmq
    depends_on:
      - postgres
      - rabbitmq
      
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: middleware
      POSTGRES_USER: middleware_user
      POSTGRES_PASSWORD: secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
      
  rabbitmq:
    image: rabbitmq:3-management
    environment:
      RABBITMQ_DEFAULT_USER: middleware_user
      RABBITMQ_DEFAULT_PASS: secure_password

volumes:
  postgres_data:
```

## Kubernetes Configuration

### ConfigMap

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: middleware-config
data:
  application.yml: |
    spring:
      profiles:
        active: production
    webclient:
      timeout:
        connect: 15s
        read: 60s
      connection-pool:
        max-connections: 200
    logging:
      level:
        com.openrangelabs.middleware: INFO
```

### Secret

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: middleware-secrets
type: Opaque
stringData:
  DB_USERNAME: middleware_user
  DB_PASSWORD: secure_password
  RABBITMQ_USERNAME: middleware_user
  RABBITMQ_PASSWORD: secure_password
```

### Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: middleware-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: middleware-app
  template:
    metadata:
      labels:
        app: middleware-app
    spec:
      containers:
      - name: app
        image: your-app:latest
        envFrom:
        - secretRef:
            name: middleware-secrets
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_HOST
          value: "postgres-service"
        volumeMounts:
        - name: config
          mountPath: /config
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
      volumes:
      - name: config
        configMap:
          name: middleware-config
```

## Configuration Testing

### Test Configuration

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  
  jpa:
    hibernate:
      ddl-auto: create-drop
      
  rabbitmq:
    # Use embedded or test containers
    host: localhost
    port: 5672

webclient:
  timeout:
    connect: 1s
    read: 5s
  enable-logging: true
```

### Configuration Test

```java
@SpringBootTest
@TestPropertySource(properties = {
    "webclient.timeout.connect=5s",
    "webclient.enable-logging=true"
})
class ConfigurationTest {
    
    @Autowired
    private WebClientProperties webClientProperties;
    
    @Test
    void testWebClientConfiguration() {
        assertThat(webClientProperties.getTimeout().getConnect())
            .isEqualTo(Duration.ofSeconds(5));
        assertThat(webClientProperties.isEnableLogging())
            .isTrue();
    }
}
```

## Troubleshooting Configuration

### Common Issues

1. **Configuration not loading**
   ```bash
   # Check active profiles
   java -jar app.jar --spring.profiles.active=production
   
   # Debug configuration
   java -jar app.jar --debug
   ```

2. **Property not found**
   ```yaml
   # Check property name and structure
   webclient:
     timeout:
       connect: 10s  # Not webclient.timeout-connect
   ```

3. **Type conversion errors**
   ```yaml
   # Use proper Duration format
   timeout:
     connect: 10s    # Good
     read: PT30S     # Also good
     write: 30000    # Bad - use duration format
   ```

### Configuration Debugging

Enable configuration debugging:

```yaml
logging:
  level:
    org.springframework.boot.context.config: DEBUG
    org.springframework.boot.context.properties: DEBUG

# Show configuration on startup
debug: true
```

## Migration Guide

### From 1.x to 2.x

Key configuration changes:

```yaml
# Old format (1.x)
orl:
  webclient:
    timeouts:
      connection: 10000  # milliseconds

# New format (2.x)  
webclient:
  timeout:
    connect: 10s       # Duration format
```

### Property Mapping

| Old Property | New Property | Notes |
|--------------|--------------|-------|
| `orl.webclient.timeouts.connection` | `webclient.timeout.connect` | Now uses Duration format |
| `orl.webclient.pool.max-size` | `webclient.connection-pool.max-connections` | Renamed for clarity |
| `orl.logging.enabled` | `webclient.enable-logging` | Moved to webclient section |

## Next Steps

- [API Reference](api-reference.md) - Complete API documentation
- [Examples](examples/) - Configuration examples for different scenarios
- [Logging Guide](logging.md) - Logging-specific configuration
- [WebClient Guide](webclient.md) - WebClient configuration details