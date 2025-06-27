package com.openrangelabs.middleware.example;

import com.openrangelabs.middleware.config.WebClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

/**
 * Corrected example demonstrating enhanced WebClient usage
 * Fixed for Spring Boot 3.x compatibility
 */
@Service
public class WebClientUsageExample {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private WebClientConfig webClientConfig;

    /**
     * Example of basic WebClient usage with enhanced configuration
     */
    public Mono<String> basicApiCall() {
        WebClient client = webClientBuilder
                .baseUrl("https://api.example.com")
                .build();

        return client.get()
                .uri("/users/{id}", 123)
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * Example with retry logic for resilience - CORRECTED
     */
    public Mono<Map<String, Object>> apiCallWithRetry() {
        WebClient client = webClientBuilder
                .baseUrl("https://api.external-service.com")
                .build();

        return client.get()
                .uri("/data")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof WebClientConfig.WebClientServerException));
    }

    /**
     * Example with custom headers and POST request
     */
    public Mono<String> postWithCustomHeaders() {
        WebClient client = webClientBuilder
                .baseUrl("https://api.service.com")
                .defaultHeader("X-API-Key", "your-api-key")
                .defaultHeader("X-Client-Version", "1.0")
                .build();

        Map<String, String> requestBody = Map.of(
                "name", "John Doe",
                "email", "john@example.com"
        );

        return client.post()
                .uri("/users")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * Example using the static factory methods (backward compatibility)
     */
    public Mono<String> usingStaticMethods() {
        WebClient client = WebClientConfig.build("https://jsonplaceholder.typicode.com");

        return client.get()
                .uri("/posts/1")
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * Example with custom WebClient configuration
     */
    public Mono<String> customConfiguredClient() {
        WebClient client = webClientConfig.buildWithCustomization(
                "https://api.custom-service.com",
                builder -> builder
                        .defaultHeader("Authorization", "Bearer token123")
                        .defaultHeader("X-Custom-Header", "custom-value")
        );

        return client.get()
                .uri("/protected-resource")
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * Example with error handling
     */
    public Mono<String> apiCallWithErrorHandling() {
        WebClient client = webClientBuilder
                .baseUrl("https://api.might-fail.com")
                .build();

        return client.get()
                .uri("/risky-endpoint")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientConfig.WebClientClientException.class, ex -> {
                    // Handle 4xx errors
                    System.err.println("Client error: " + ex.getMessage());
                    return Mono.just("Default response for client error");
                })
                .onErrorResume(WebClientConfig.WebClientServerException.class, ex -> {
                    // Handle 5xx errors
                    System.err.println("Server error: " + ex.getMessage());
                    return Mono.just("Default response for server error");
                })
                .onErrorResume(Exception.class, ex -> {
                    // Handle other errors (timeouts, connection issues)
                    System.err.println("Network error: " + ex.getMessage());
                    return Mono.just("Default response for network error");
                });
    }

    /**
     * Example with timeout override for specific calls
     */
    public Mono<String> longRunningApiCall() {
        WebClient client = webClientBuilder
                .baseUrl("https://api.slow-service.com")
                .build();

        return client.get()
                .uri("/long-running-operation")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMinutes(5));
    }

    /**
     * Example with authentication header
     */
    public Mono<String> authenticatedApiCall() {
        WebClient client = webClientBuilder
                .baseUrl("https://api.secured-service.com")
                .build();

        return client.get()
                .uri("/protected-data")
                .header("Authorization", "Bearer " + getAccessToken())
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * Example demonstrating different response types - CORRECTED
     */
    public void demonstrateResponseTypes() {
        WebClient client = webClientBuilder
                .baseUrl("https://api.example.com")
                .build();

        // String response
        Mono<String> stringResponse = client.get()
                .uri("/text-data")
                .retrieve()
                .bodyToMono(String.class);

        // JSON response to Map using ParameterizedTypeReference
        Mono<Map<String, Object>> mapResponse = client.get()
                .uri("/json-data")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

        // JSON response to custom DTO
        Mono<UserDTO> userResponse = client.get()
                .uri("/user/{id}", 123)
                .retrieve()
                .bodyToMono(UserDTO.class);

        // Handle responses
        stringResponse.subscribe(System.out::println);
        mapResponse.subscribe(data -> System.out.println("Data: " + data));
        userResponse.subscribe(user -> System.out.println("User: " + user));
    }

    /**
     * Example of working with arrays/lists - BONUS
     */
    public Mono<java.util.List<UserDTO>> getUsers() {
        WebClient client = webClientBuilder
                .baseUrl("https://api.example.com")
                .build();

        return client.get()
                .uri("/users")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<java.util.List<UserDTO>>() {});
    }

    /**
     * Helper method to simulate getting access token
     */
    private String getAccessToken() {
        return "sample-access-token";
    }

    /**
     * Example DTO for demonstration
     */
    public static class UserDTO {
        private Long id;
        private String name;
        private String email;

        // Default constructor for Jackson
        public UserDTO() {}

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        @Override
        public String toString() {
            return "UserDTO{id=" + id + ", name='" + name + "', email='" + email + "'}";
        }
    }

    /**
     * Example of file upload
     */
    public Mono<String> uploadFile() {
        WebClient client = webClientBuilder
                .baseUrl("https://api.file-service.com")
                .build();

        byte[] fileData = "file content".getBytes();

        return client.post()
                .uri("/upload")
                .bodyValue(fileData)
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * Example with query parameters
     */
    public Mono<Map<String, Object>> searchWithParams() {
        WebClient client = webClientBuilder
                .baseUrl("https://api.search-service.com")
                .build();

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", "spring boot")
                        .queryParam("limit", 10)
                        .queryParam("offset", 0)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}