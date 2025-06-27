# WebClient Guide

The middleware library provides an enhanced, production-ready WebClient configuration with connection pooling, automatic retry logic, error handling, and comprehensive timeout management.

## Overview

The WebClient configuration includes:
- **Connection Pooling** - Efficient connection reuse
- **Timeout Management** - Configurable connect, read, and write timeouts
- **Error Handling** - Standardized exception handling for 4xx and 5xx responses
- **Retry Logic** - Built-in retry mechanisms for transient failures
- **Logging Support** - Optional request/response logging
- **Security Headers** - Automatic security and browser headers

## Quick Start

### Basic Usage

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
            .bodyToMono(String.class);
    }
}
```

### Static Factory Methods (Backward Compatibility)

```java
// Simple client creation
WebClient client = WebClientConfig.build("https://api.example.com");

// With URL object
URL baseUrl = new URL("https://api.example.com");
WebClient client = WebClientConfig.build(baseUrl);
```

## Configuration

### Application Properties

```yaml
# WebClient Configuration
webclient:
  timeout:
    connect: 10s        # Connection timeout
    read: 30s          # Read timeout
    write: 30s         # Write timeout
  connection-pool:
    max-connections: 100                    # Total connections
    max-connections-per-route: 20          # Per-route connections
    keep-alive: 5m                         # Keep-alive duration
    evict-idle-connections: 1m             # Idle connection cleanup
  user-agent: "YourApp/1.0"               # Custom user agent
  enable-logging: false                   # Request/response logging

# Domain configuration (for security headers)
domain: https://your-application.com
```

### Environment-Specific Configuration

```yaml
# Development
spring:
  profiles: development
webclient:
  timeout:
    connect: 5s
    read: 10s
  enable-logging: true

---
# Production
spring:
  profiles: production
webclient:
  timeout:
    connect: 15s
    read: 60s
  connection-pool:
    max-connections: 200
  enable-logging: false
```

## Advanced Usage

### Custom Configuration

```java
@Service
public class CustomApiService {
    
    @Autowired
    private WebClientConfig webClientConfig;
    
    public Mono<String> callProtectedApi() {
        WebClient client = webClientConfig.buildWithCustomization(
            "https://api.secured-service.com",
            builder -> builder
                .defaultHeader("Authorization", "Bearer " + getAccessToken())
                .defaultHeader("X-API-Key", "your-api-key")
                .defaultHeader("X-Client-Version", "1.0")
        );
        
        return client.get()
            .uri("/protected-resource")
            .retrieve()
            .bodyToMono(String.class);
    }
}
```

### Error Handling

The library provides built-in error handling with custom exceptions:

```java
public Mono<String> apiCallWithErrorHandling() {
    return webClientBuilder
        .baseUrl("https://api.might-fail.com")
        .build()
        .get()
        .uri("/risky-endpoint")
        .retrieve()
        .bodyToMono(String.class)
        .onErrorResume(WebClientConfig.WebClientClientException.class, ex -> {
            // Handle 4xx client errors
            logger.warn("Client error: {} - {}", ex.getStatusCode(), ex.getMessage());
            return Mono.just("Client error occurred");
        })
        .onErrorResume(WebClientConfig.WebClientServerException.class, ex -> {
            // Handle 5xx server errors
            logger.error("Server error: {} - {}", ex.getStatusCode(), ex.getMessage());
            return Mono.just("Server error occurred");
        })
        .onErrorResume(Exception.class, ex -> {
            // Handle other errors (timeouts, connection issues)
            logger.error("Network error: {}", ex.getMessage());
            return Mono.just("Network error occurred");
        });
}
```

### Retry Logic

```java
public Mono<String> apiCallWithRetry() {
    return webClientBuilder
        .baseUrl("https://api.external-service.com")
        .build()
        .get()
        .uri("/data")
        .retrieve()
        .bodyToMono(String.class)
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
            .filter(throwable -> throwable instanceof WebClientConfig.WebClientServerException))
        .timeout(Duration.ofMinutes(2));
}
```

## Request Types

### GET Requests

```java
// Simple GET
Mono<String> response = client.get()
    .uri("/users/{id}", 123)
    .retrieve()
    .bodyToMono(String.class);

// GET with query parameters
Mono<String> response = client.get()
    .uri(uriBuilder -> uriBuilder
        .path("/search")
        .queryParam("q", "spring boot")
        .queryParam("limit", 10)
        .build())
    .retrieve()
    .bodyToMono(String.class);

// GET with headers
Mono<String> response = client.get()
    .uri("/protected-resource")
    .header("Authorization", "Bearer token")
    .retrieve()
    .bodyToMono(String.class);
```

### POST Requests

```java
// POST with JSON body
Map<String, String> requestBody = Map.of(
    "name", "John Doe",
    "email", "john@example.com"
);

Mono<String> response = client.post()
    .uri("/users")
    .bodyValue(requestBody)
    .retrieve()
    .bodyToMono(String.class);

// POST with custom object
UserDTO user = new UserDTO();
user.setName("John Doe");
user.setEmail("john@example.com");

Mono<UserDTO> response = client.post()
    .uri("/users")
    .bodyValue(user)
    .retrieve()
    .bodyToMono(UserDTO.class);
```

### PUT and PATCH Requests

```java
// PUT request
Mono<String> response = client.put()
    .uri("/users/{id}", 123)
    .bodyValue(updatedUser)
    .retrieve()
    .bodyToMono(String.class);

// PATCH request
Map<String, Object> partialUpdate = Map.of("email", "new@example.com");

Mono<String> response = client.patch()
    .uri("/users/{id}", 123)
    .bodyValue(partialUpdate)
    .retrieve()
    .bodyToMono(String.class);
```

### File Upload

```java
// File upload
byte[] fileData = Files.readAllBytes(Paths.get("document.pdf"));

Mono<String> response = client.post()
    .uri("/upload")
    .contentType(MediaType.APPLICATION_OCTET_STREAM)
    .bodyValue(fileData)
    .retrieve()
    .bodyToMono(String.class);

// Multipart form data
MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
parts.add("file", new FileSystemResource("document.pdf"));
parts.add("description", "Document upload");

Mono<String> response = client.post()
    .uri("/upload")
    .contentType(MediaType.MULTIPART_FORM_DATA)
    .bodyValue(parts)
    .retrieve()
    .bodyToMono(String.class);
```

## Response Handling

### Different Response Types

```java
// String response
Mono<String> stringResponse = client.get()
    .uri("/text-data")
    .retrieve()
    .bodyToMono(String.class);

// JSON to Map
Mono<Map<String, Object>> mapResponse = client.get()
    .uri("/json-data")
    .retrieve()
    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

// JSON to custom DTO
Mono<UserDTO> userResponse = client.get()
    .uri("/user/{id}", 123)
    .retrieve()
    .bodyToMono(UserDTO.class);

// List of objects
Mono<List<UserDTO>> usersResponse = client.get()
    .uri("/users")
    .retrieve()
    .bodyToMono(new ParameterizedTypeReference<List<UserDTO>>() {});
```

### Status Code Handling

```java
Mono<String> response = client.get()
    .uri("/api/data")
    .retrieve()
    .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
        return clientResponse.bodyToMono(String.class)
            .flatMap(body -> Mono.error(new CustomClientException(body)));
    })
    .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
        return Mono.error(new CustomServerException("Server error"));
    })
    .bodyToMono(String.class);
```

### Response Headers

```java
Mono<ResponseEntity<String>> response = client.get()
    .uri("/api/data")
    .retrieve()
    .toEntity(String.class);

response.subscribe(entity -> {
    HttpHeaders headers = entity.getHeaders();
    String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
    String body = entity.getBody();
});
```

## Authentication

### Bearer Token

```java
@Service
public class AuthenticatedApiService {
    
    @Autowired
    private TokenService tokenService;
    
    public Mono<String> callProtectedApi() {
        return tokenService.getAccessToken()
            .flatMap(token -> 
                webClientBuilder
                    .baseUrl("https://api.secured.com")
                    .build()
                    .get()
                    .uri("/protected-data")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(String.class)
            );
    }
}
```

### API Key Authentication

```java
WebClient client = webClientBuilder
    .baseUrl("https://api.service.com")
    .defaultHeader("X-API-Key", "your-api-key")
    .build();
```

### Basic Authentication

```java
String credentials = Base64.getEncoder()
    .encodeToString("username:password".getBytes());

WebClient client = webClientBuilder
    .baseUrl("https://api.service.com")
    .defaultHeader("Authorization", "Basic " + credentials)
    .build();
```

## Performance Optimization

### Connection Pooling

The library automatically configures connection pooling:

```yaml
webclient:
  connection-pool:
    max-connections: 100        # Total connections across all routes
    max-connections-per-route: 20  # Connections per destination
    keep-alive: 5m             # How long to keep connections alive
    evict-idle-connections: 1m  # Cleanup interval for idle connections
```

### Timeout Optimization

```yaml
webclient:
  timeout:
    connect: 10s    # Time to establish connection
    read: 30s      # Time to read response
    write: 30s     # Time to write request
```

### Compression

Compression is automatically enabled:

```java
// The library automatically enables:
// - Response compression
// - Follow redirects
// - Connection reuse
```

## Monitoring and Observability

### Metrics Integration

```java
@Component
public class WebClientMetrics {
    
    private final Timer httpRequestTimer;
    private final Counter httpRequestsTotal;
    
    public WebClientMetrics(MeterRegistry meterRegistry) {
        this.httpRequestTimer = Timer.builder("http.client.requests")
            .description("HTTP client request duration")
            .register(meterRegistry);
        this.httpRequestsTotal = Counter.builder("http.client.requests.total")
            .description("Total HTTP client requests")
            .register(meterRegistry);
    }
    
    public <T> Mono<T> measureRequest(Mono<T> request, String uri) {
        return Timer.Sample.start(Micrometer.globalRegistry)
            .stop(httpRequestTimer.tag("uri", uri))
            .doOnNext(result -> httpRequestsTotal.increment(Tags.of("uri", uri, "status", "success")))
            .doOnError(error -> httpRequestsTotal.increment(Tags.of("uri", uri, "status", "error")));
    }
}
```

### Request/Response Logging

Enable logging in development:

```yaml
webclient:
  enable-logging: true

# Or programmatically
logging:
  level:
    reactor.netty.http.client: DEBUG
```

### Health Checks

```java
@Component
public class ExternalApiHealthIndicator implements HealthIndicator {
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Override
    public Health health() {
        try {
            String response = webClientBuilder
                .baseUrl("https://api.external-service.com")
                .build()
                .get()
                .uri("/health")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();
                
            return Health.up()
                .withDetail("external-api", "Available")
                .withDetail("response", response)
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("external-api", "Unavailable")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

## Testing

### Unit Testing

```java
@ExtendWith(MockitoExtension.class)
class ExternalApiServiceTest {
    
    @Mock
    private WebClient.Builder webClientBuilder;
    
    @Mock
    private WebClient webClient;
    
    @InjectMocks
    private ExternalApiService service;
    
    @Test
    void testApiCall() {
        // Mock WebClient behavior
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        
        WebClient.RequestHeadersUriSpec requestSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        
        when(webClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri("/data")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("test response"));
        
        // Test the service
        StepVerifier.create(service.callExternalApi())
            .expectNext("test response")
            .verifyComplete();
    }
}
```

### Integration Testing with MockWebServer

```java
@SpringBootTest
class WebClientIntegrationTest {
    
    private MockWebServer mockWebServer;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @BeforeEach
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }
    
    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    
    @Test
    void testHttpInteraction() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
            .setBody("{\"status\":\"success\"}")
            .setHeader("Content-Type", "application/json"));
        
        String baseUrl = mockWebServer.url("/").toString();
        
        // Act
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        Mono<String> response = client.get()
            .uri("/test")
            .retrieve()
            .bodyToMono(String.class);
        
        // Assert
        StepVerifier.create(response)
            .expectNext("{\"status\":\"success\"}")
            .verifyComplete();
        
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/test", recordedRequest.getPath());
    }
}
```

## Best Practices

### 1. Reuse WebClient Instances

```java
// Good: Reuse configured client
@Service
public class ApiService {
    private final WebClient apiClient;
    
    public ApiService(WebClient.Builder webClientBuilder) {
        this.apiClient = webClientBuilder
            .baseUrl("https://api.service.com")
            .defaultHeader("X-API-Key", "key")
            .build();
    }
}

// Avoid: Creating new clients for each request
public Mono<String> badExample() {
    return WebClient.create("https://api.service.com") // Don't do this
        .get()
        .retrieve()
        .bodyToMono(String.class);
}
```

### 2. Configure Appropriate Timeouts

```java
// For fast internal APIs
webclient:
  timeout:
    connect: 2s
    read: 5s

// For external APIs
webclient:
  timeout:
    connect: 10s
    read: 30s

// For long-running operations
.timeout(Duration.ofMinutes(5)) // Override per request
```

### 3. Handle Errors Gracefully

```java
public Mono<String> robustApiCall() {
    return apiClient.get()
        .uri("/data")
        .retrieve()
        .bodyToMono(String.class)
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
            .filter(this::isRetryableError))
        .onErrorResume(this::handleError)
        .timeout(Duration.ofSeconds(30))
        .doOnError(error -> logger.error("API call failed", error));
}

private boolean isRetryableError(Throwable throwable) {
    return throwable instanceof WebClientConfig.WebClientServerException ||
           throwable instanceof TimeoutException ||
           throwable instanceof ConnectException;
}

private Mono<String> handleError(Throwable error) {
    if (error instanceof WebClientConfig.WebClientClientException) {
        // Don't retry 4xx errors, return default
        return Mono.just("default-response");
    }
    return Mono.error(error);
}
```

### 4. Use Reactive Patterns

```java
// Good: Chain reactive operations
public Mono<UserProfile> getUserProfile(Long userId) {
    return apiClient.get()
        .uri("/users/{id}", userId)
        .retrieve()
        .bodyToMono(User.class)
        .flatMap(user -> 
            apiClient.get()
                .uri("/users/{id}/preferences", userId)
                .retrieve()
                .bodyToMono(Preferences.class)
                .map(prefs -> new UserProfile(user, prefs))
        );
}

// Avoid: Blocking calls
public UserProfile badExample(Long userId) {
    User user = apiClient.get()
        .uri("/users/{id}", userId)
        .retrieve()
        .bodyToMono(User.class)
        .block(); // Don't block!
    
    return new UserProfile(user, null);
}
```

### 5. Implement Circuit Breaker Pattern

```java
@Component
public class CircuitBreakerApiService {
    
    private final WebClient apiClient;
    private final CircuitBreaker circuitBreaker;
    
    public CircuitBreakerApiService(WebClient.Builder webClientBuilder) {
        this.apiClient = webClientBuilder
            .baseUrl("https://api.external-service.com")
            .build();
            
        this.circuitBreaker = CircuitBreaker.ofDefaults("api-service");
    }
    
    public Mono<String> callWithCircuitBreaker() {
        return Mono.fromCallable(circuitBreaker.decorateCallable(() -> 
            apiClient.get()
                .uri("/data")
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(30))
        )).flatMap(Mono::fromCallable);
    }
}
```

## Troubleshooting

### Common Issues

#### 1. Connection Pool Exhausted

```
reactor.netty.http.client.PrematureCloseException: Connection prematurely closed BEFORE response
```

**Solution:**
```yaml
webclient:
  connection-pool:
    max-connections: 200  # Increase pool size
    max-connections-per-route: 50
```

#### 2. Read Timeout

```
java.util.concurrent.TimeoutException: Did not observe any item or terminal signal within 30000ms
```

**Solution:**
```yaml
webclient:
  timeout:
    read: 60s  # Increase read timeout
```

#### 3. SSL/TLS Issues

```java
@Configuration
public class WebClientSslConfig {
    
    @Bean
    public WebClient.Builder webClientBuilderWithSsl() {
        SslContext sslContext = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();
            
        HttpClient httpClient = HttpClient.create()
            .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
            
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
```

#### 4. Memory Leaks

Ensure proper subscription handling:

```java
// Good: Proper subscription
Disposable subscription = apiClient.get()
    .uri("/data")
    .retrieve()
    .bodyToMono(String.class)
    .subscribe(
        result -> processResult(result),
        error -> handleError(error)
    );

// Remember to dispose when appropriate
subscription.dispose();

// Better: Use operators that auto-dispose
return apiClient.get()
    .uri("/data")
    .retrieve()
    .bodyToMono(String.class)
    .doOnNext(this::processResult)
    .doOnError(this::handleError);
```

### Debug Configuration

Enable detailed logging for troubleshooting:

```yaml
logging:
  level:
    reactor.netty.http.client: DEBUG
    org.springframework.web.reactive.function.client: DEBUG
    io.netty: DEBUG
    
# Application properties for debugging
webclient:
  enable-logging: true
  timeout:
    connect: 30s
    read: 60s
```

### Performance Monitoring

```java
@Component
public class WebClientPerformanceMonitor {
    
    @EventListener
    public void onHttpRequest(HttpRequestEvent event) {
        logger.info("HTTP Request: {} {} - Duration: {}ms", 
            event.getMethod(), 
            event.getUri(), 
            event.getDuration());
    }
    
    @Scheduled(fixedRate = 60000) // Every minute
    public void reportConnectionPoolStats() {
        // Log connection pool metrics
        // Implement based on your monitoring needs
    }
}
```

## Migration Guide

### From RestTemplate

```java
// Old RestTemplate code
@Service
public class OldApiService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public String callApi() {
        return restTemplate.getForObject("https://api.service.com/data", String.class);
    }
}

// New WebClient code
@Service
public class NewApiService {
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    public Mono<String> callApi() {
        return webClientBuilder
            .baseUrl("https://api.service.com")
            .build()
            .get()
            .uri("/data")
            .retrieve()
            .bodyToMono(String.class);
    }
    
    // If you need blocking behavior temporarily
    public String callApiBlocking() {
        return callApi().block();
    }
}
```

### From Basic WebClient

```java
// Basic WebClient usage
WebClient client = WebClient.create("https://api.service.com");

// Enhanced WebClient with library
@Autowired
private WebClient.Builder webClientBuilder;

WebClient client = webClientBuilder
    .baseUrl("https://api.service.com")
    .build();
// Now includes: connection pooling, timeouts, error handling, security headers
```

## Real-World Examples

### 1. Microservice Communication

```java
@Service
public class OrderServiceClient {
    
    private final WebClient orderServiceClient;
    
    public OrderServiceClient(WebClient.Builder webClientBuilder,
                             @Value("${services.order.url}") String orderServiceUrl) {
        this.orderServiceClient = webClientBuilder
            .baseUrl(orderServiceUrl)
            .defaultHeader("X-Service-Name", "payment-service")
            .build();
    }
    
    public Mono<Order> getOrder(Long orderId) {
        return orderServiceClient.get()
            .uri("/orders/{id}", orderId)
            .retrieve()
            .bodyToMono(Order.class)
            .timeout(Duration.ofSeconds(5))
            .retryWhen(Retry.backoff(2, Duration.ofMillis(500)));
    }
    
    public Mono<Order> updateOrderStatus(Long orderId, OrderStatus status) {
        return orderServiceClient.put()
            .uri("/orders/{id}/status", orderId)
            .bodyValue(Map.of("status", status))
            .retrieve()
            .bodyToMono(Order.class);
    }
}
```

### 2. External API Integration

```java
@Service
public class PaymentGatewayService {
    
    private final WebClient paymentClient;
    
    public PaymentGatewayService(WebClient.Builder webClientBuilder,
                                @Value("${payment.gateway.url}") String gatewayUrl,
                                @Value("${payment.gateway.api-key}") String apiKey) {
        this.paymentClient = webClientBuilder
            .baseUrl(gatewayUrl)
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .defaultHeader("X-API-Version", "2024-01")
            .build();
    }
    
    public Mono<PaymentResponse> processPayment(PaymentRequest request) {
        return paymentClient.post()
            .uri("/payments")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, this::handleClientError)
            .onStatus(HttpStatus::is5xxServerError, this::handleServerError)
            .bodyToMono(PaymentResponse.class)
            .timeout(Duration.ofSeconds(30))
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(this::isRetryableError));
    }
    
    private Mono<Throwable> handleClientError(ClientResponse response) {
        return response.bodyToMono(ErrorResponse.class)
            .flatMap(error -> Mono.error(new PaymentValidationException(error.getMessage())));
    }
    
    private Mono<Throwable> handleServerError(ClientResponse response) {
        return Mono.error(new PaymentGatewayException("Payment gateway unavailable"));
    }
}
```

### 3. File Upload Service

```java
@Service
public class DocumentUploadService {
    
    private final WebClient documentServiceClient;
    
    public DocumentUploadService(WebClient.Builder webClientBuilder,
                                @Value("${services.document.url}") String documentServiceUrl) {
        this.documentServiceClient = webClientBuilder
            .baseUrl(documentServiceUrl)
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
            .build();
    }
    
    public Mono<DocumentResponse> uploadDocument(String filename, byte[] content, String mimeType) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename;
            }
        });
        parts.add("metadata", Map.of("mimeType", mimeType, "uploadedAt", Instant.now()));
        
        return documentServiceClient.post()
            .uri("/documents/upload")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(parts)
            .retrieve()
            .bodyToMono(DocumentResponse.class)
            .timeout(Duration.ofMinutes(5)); // Longer timeout for uploads
    }
}
```

## Next Steps

- [Configuration Guide](configuration.md) - Detailed configuration options
- [API Reference](api-reference.md) - Complete API documentation
- [Examples](examples/) - More comprehensive examples
- [Logging Guide](logging.md) - Integration with logging features